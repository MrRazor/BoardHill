package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.BoardHillApplication;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.Message;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.MessageSentEvent;
import cz.uhk.boardhill.service.MessageSentEventBroadcaster;
import cz.uhk.boardhill.service.MessageService;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Route(value = "chat/messages", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class ChatMessageView extends VerticalLayout implements HasUrlParameter<String> {

    private static final int PAGE_SIZE = 7;
    private final transient ChatService chatService;
    private final transient MessageService messageService;
    private final transient AuthenticationContext authContext;
    private transient List<Message> messages;
    private int page = 0;
    private transient Chat chat;
    private String username;
    private boolean isAdmin = false;
    private VerticalLayout messageLayout;
    private boolean fullyLoaded = false;
    private Registration broadcasterRegistration;

    public ChatMessageView(ChatService chatService, MessageService messageService, AuthenticationContext authContext) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.authContext = authContext;

        Tabs tabs = new Tabs();
        tabs.setAutoselect(false);
        Map<Tab, String> tabsToLinks = new HashMap<>();
        tabs.addSelectedChangeListener(e ->
                tabs.getUI().ifPresent(ui -> ui.navigate(tabsToLinks.get(tabs.getSelectedTab())))
        );

        Tab tab = new Tab("Go Back");
        tabsToLinks.put(tab, "chat/user");
        tabs.add(tab);

        H1 header = new H1("Message View");

        add(header, tabs);

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
        List<GrantedAuthority> authorities = new ArrayList<>(authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities());
        username = authContext.getPrincipalName().get();
        isAdmin = authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"));

        Optional<Chat> chatOptional;
        if (isAdmin) {
            chatOptional = chatService.findById(s);
        } else {
            chatOptional = chatService.findAllNotDeletedChatsByUser(username).stream().filter(c -> c.getName().equals(s)).findFirst();
        }

        if (chatOptional.isPresent() && !chatOptional.get().isDeleted()) {
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
                    try {
                        messageService.createMessage(s, username, input.getValue());
                        Notification notification = Notification.show("Message sent!");
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        MessageSentEventBroadcaster.broadcast(new MessageSentEvent(chat.getName()));
                        input.clear();
                    } catch (IllegalArgumentException | IllegalStateException e1) {
                        Notification notification = Notification.show(e1.getMessage());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } catch (Exception e2) {
                        Notification notification = Notification.show("Sending message failed!");
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
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
                    new Span(message.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(BoardHillApplication.VIEW_TZ).localizedBy(BoardHillApplication.VIEW_LOCALE))),
                    new Span("User: " + message.getUser().getUsername()),
                    new Span(message.getContent())
            );
            messageContent.setSpacing(false);
            messageContent.setPadding(false);

            if (message.getUser().getUsername().equals(username)) {
                messageContent.getStyle().set("background-color", "#E6EFF0");
            }

            Button deleteMessageButton = new Button("Delete");
            deleteMessageButton.addClickListener(e -> {
                try {
                    messageService.deleteMessage(message.getId(), username);
                    Notification notification = Notification.show("Message deleted!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    MessageSentEventBroadcaster.broadcast(new MessageSentEvent(chat.getName()));
                } catch (IllegalArgumentException | IllegalStateException e1) {
                    Notification notification = Notification.show(e1.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (Exception e2) {
                    Notification notification = Notification.show("Deleting message failed!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            if (!message.getUser().getUsername().equals(username) && !chat.getOwner().getUsername().equals(username) && !isAdmin) {
                deleteMessageButton.setEnabled(false);
            }

            singleMessage.add(deleteMessageButton, messageContent);
            messageLayout.add(singleMessage);
        }
    }
}
