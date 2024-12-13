package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.Message;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.MessageSentEvent;
import cz.uhk.boardhill.service.MessageSentEventBroadcaster;
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
  private int page = 0;
  private final int PAGE_SIZE = 10;
  private Chat chat;
  private String username;
  private VerticalLayout messageLayout;
  private boolean fullyLoaded = false;
  private Registration broadcasterRegistration;

  public ChatMessageView(ChatService chatService, MessageService messageService, AuthenticationContext authContext) {
    this.chatService = chatService;
    this.messageService = messageService;
    this.authContext = authContext;

    setSizeFull();
    setAlignItems(Alignment.CENTER);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    UI ui = attachEvent.getUI();
    broadcasterRegistration = MessageSentEventBroadcaster.register(newMessageSendEvent -> {
      if (fullyLoaded && newMessageSendEvent.getChatId().equals(chat.getName())) {
        ui.access(this::reloadMessages);
      }
    });
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    broadcasterRegistration.remove();
    broadcasterRegistration = null;
  }

  @Override
  public void setParameter(BeforeEvent beforeEvent, String s) {
    List<GrantedAuthority> authorities = authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities().stream().collect(Collectors.toList());
    username = authContext.getPrincipalName().get();

    Optional<Chat> chatOptional;
    if (authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"))) {
      chatOptional = chatService.findById(s);
    } else {
      chatOptional = chatService.findAllNotDeletedChatsByUser(username).stream().filter(c -> c.getName().equals(s)).findFirst();
    }

    if (chatOptional.isPresent()) {
      chat = chatOptional.get();

      messageLayout = new VerticalLayout();
      messageLayout.setWidth("50%");
      messageLayout.setAlignItems(Alignment.STRETCH);
      add(messageLayout);

      reloadMessages();

      HorizontalLayout inputBar = new HorizontalLayout();
      inputBar.setWidth("50%");
      inputBar.setAlignItems(Alignment.END);

      TextArea input = new TextArea();
      input.setPlaceholder("Enter Message...");
      input.setWidthFull();

      Button sendMessage = new Button("Send");
      sendMessage.addClickListener(e -> {
        if (input.getValue() != null && !input.getValue().isEmpty()) {
          messageService.createMessage(s, username, input.getValue());
          MessageSentEventBroadcaster.broadcast(new MessageSentEvent(chat.getName()));
          input.clear();
        }
      });

      inputBar.add(input, sendMessage);
      inputBar.setFlexGrow(1, input);
      add(inputBar);

      HorizontalLayout pageBar = new HorizontalLayout();
      pageBar.setAlignItems(Alignment.CENTER);

      Text pageText = new Text(Integer.toString(page + 1));
      Button previousPage = new Button("Newer");
      previousPage.addClickListener(e -> {
        if (page > 0) {
          page--;
          pageText.setText(Integer.toString(page + 1));
          reloadMessages();
        }
      });

      Button nextPage = new Button("Older");
      nextPage.addClickListener(e -> {
        if (messages.size() == PAGE_SIZE) {
          page++;
          pageText.setText(Integer.toString(page + 1));
          reloadMessages();
        }
      });

      pageBar.add(previousPage, pageText, nextPage);
      add(pageBar);

      fullyLoaded = true;
    } else {
      Notification notification = Notification.show("You cannot view this chat");
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
  }

  private void reloadMessages() {
    messages = messageService.getMessages(chat.getName(), page, PAGE_SIZE).stream().collect(Collectors.toCollection((Supplier<List<Message>>) ArrayList::new));
    Collections.reverse(messages);

    messageLayout.removeAll();

    for (Message message : messages) {
      HorizontalLayout singleMessage = new HorizontalLayout();
      singleMessage.setWidthFull();

      VerticalLayout messageContent = new VerticalLayout(
              new Span("At: " + message.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))),
              new Span("User: " + message.getUser().getUsername()),
              new Span(message.getContent())
      );
      messageContent.setSpacing(false);
      messageContent.setPadding(false);
      messageContent.setWidthFull();

      Button deleteMessageButton = new Button("Delete");
      deleteMessageButton.addClickListener(e -> {
        messageService.deleteMessage(message.getId(), username);
      });
      if (!message.getUser().getUsername().equals(username) && !chat.getOwner().getUsername().equals(username)) {
        deleteMessageButton.setEnabled(false);
      }

      singleMessage.add(deleteMessageButton, messageContent);
      messageLayout.add(singleMessage);
    }
  }
}
