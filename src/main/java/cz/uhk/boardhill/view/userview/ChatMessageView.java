package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Supplier;
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
  private int page=0;
  private final int PAGE_SIZE = 10;
  private Chat chat;
  private String username;
  private VerticalLayout messageLayout;
  private Timer timer;

  public ChatMessageView(ChatService chatService, MessageService messageService, AuthenticationContext authContext) {
    this.chatService = chatService;
    this.messageService = messageService;
    this.authContext = authContext;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        attachEvent.getUI().access(()->reloadMessages());
      }
    }, 10000L, 10000L);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    timer.cancel();
    timer = null;
  }

  @Override
  public void setParameter(BeforeEvent beforeEvent, String s) {
    List<GrantedAuthority> authorities = authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities().stream().collect(Collectors.toList());
    username = authContext.getPrincipalName().get();

    Optional<Chat> chatOptional;
    if(authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"))) {
      chatOptional = chatService.findById(s);
    }
    else {
      chatOptional = chatService.findAllNotDeletedChatsByUser(username).stream().filter(c->c.getName().equals(s)).findFirst();
    }

    if(chatOptional.isPresent()) {
      chat = chatOptional.get();

      messageLayout = new VerticalLayout();
      add(messageLayout);

      reloadMessages();

      HorizontalLayout inputBar = new HorizontalLayout();
      TextArea input = new TextArea("Enter Message");
      Button sendMessage = new Button("Send Message");
      sendMessage.addClickListener(e->{
        if(input.getValue() != null && !input.getValue().equals("")) {
          messageService.createMessage(s, username, input.getValue());
          input.setValue("");
          reloadMessages();
        }
      });
      inputBar.add(input, sendMessage);
      add(inputBar);

      HorizontalLayout pageBar = new HorizontalLayout();
      Text pageText = new Text(Integer.toString(page+1));
      Button previousPage = new Button("Newer");
      previousPage.addClickListener(e->{
        if(page > 0) {
          page--;
          pageText.setText(Integer.toString(page+1));
          reloadMessages();
        }
      });
      Button nextPage = new Button("Older");
      nextPage.addClickListener(e->{
        if(messages.size() == PAGE_SIZE) {
          page++;
          pageText.setText(Integer.toString(page+1));
          reloadMessages();
        }
      });
      pageBar.add(previousPage, pageText, nextPage);
      add(pageBar);
    }
    else {
      Notification notification = Notification.show("You cannot view this chat");
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  private void reloadMessages() {
    if(chat == null) {
      return;
    }
    messages = messageService.getMessages(chat.getName(), page, PAGE_SIZE).stream().collect(Collectors.toCollection(new Supplier<List<Message>>() {
      @Override
      public List<Message> get() {
        return new ArrayList<>();
      }
    }));
    Collections.reverse(messages);

    messageLayout.removeAll();

    for(Message message : messages) {
      HorizontalLayout singleMessage = new HorizontalLayout();
      VerticalLayout messageContent = new VerticalLayout(
        new Span("At: " + message.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))),
        new Span("User: " + message.getUser().getUsername()),
        new Span(message.getContent())
      );
      messageContent.setSpacing(false);
      messageContent.setPadding(false);

      Button deleteMessageButton = new Button("Delete");
      deleteMessageButton.addClickListener(e->{
        messageService.deleteMessage(message.getId(), username);
      });
      if(!message.getUser().getUsername().equals(username) && !chat.getOwner().getUsername().equals(username)) {
        deleteMessageButton.setEnabled(false);
      }
      singleMessage.add(deleteMessageButton, messageContent);

      messageLayout.add(singleMessage);
    }
  }
}
