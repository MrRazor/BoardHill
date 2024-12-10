package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.Message;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.MessageService;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "chat/messages", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class ChatMessageView extends VerticalLayout implements HasUrlParameter<String> {

  private final ChatService chatService;
  private final MessageService messageService;
  private final AuthenticationContext authContext;
  private List<Message> messages;
  int page=0;

  public ChatMessageView(ChatService chatService, MessageService messageService, AuthenticationContext authContext) {
    this.chatService = chatService;
    this.messageService = messageService;
    this.authContext = authContext;
  }

  @Override
  public void setParameter(BeforeEvent beforeEvent, String s) {
    List<GrantedAuthority> authorities = authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities().stream().collect(Collectors.toList());

    Optional<Chat> chatOptional;
    if(authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"))) {
      chatOptional = chatService.findById(s);
    }
    else {
      chatOptional = chatService.findAllNotDeletedChatsByUser(authContext.getPrincipalName().get()).stream().filter(c->c.getName().equals(s)).findFirst();
    }

    if(chatOptional.isPresent()) {
      HorizontalLayout inputBar = new HorizontalLayout();
      TextArea input = new TextArea("Enter Message");
      Button sendMessage = new Button("Send Message");
      sendMessage.addClickListener(e->{
        if(input.getValue() != null && !input.getValue().equals("")) {
          messageService.createMessage(s, authContext.getPrincipalName().get(), input.getValue());
          input.setValue("");
        }
      });
      inputBar.add(input, sendMessage);
      add(inputBar);

      HorizontalLayout pageBar = new HorizontalLayout();
      Text pageText = new Text(Integer.toString(page+1));
      Button previousPage = new Button("Previous Page");
      previousPage.addClickListener(e->{
        if(page > 0) {
          page--;
          pageText.setText(Integer.toString(page+1));
          reloadMessages(s, page);
        }
      });
      Button nextPage = new Button("Next Page");
      nextPage.addClickListener(e->{
        page++;
        pageText.setText(Integer.toString(page+1));
        reloadMessages(s, page);
      });
      pageBar.add(previousPage, pageText, nextPage);
      add(pageBar);
    }
    else {
      Notification notification = Notification.show("You cannot view this chat");
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  private void reloadMessages(String chatName, int page) {
    messages = messageService.getMessages(chatName, page, 10).stream().collect(Collectors.toList());
    Collections.reverse(messages);
  }
}
