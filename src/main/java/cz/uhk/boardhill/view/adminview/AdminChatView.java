package cz.uhk.boardhill.view.adminview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.User;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.UserService;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hibernate.Hibernate;

@Route(value = "chat/admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminChatView extends VerticalLayout {
  private final UserService userService;
  private final ChatService chatService;
  private final AuthenticationContext authContext;

  public AdminChatView(UserService userService, ChatService chatService, AuthenticationContext authContext) {
    this.userService = userService;
    this.chatService = chatService;
    this.authContext = authContext;

    setAlignItems(FlexComponent.Alignment.CENTER);

    Tabs tabs = new Tabs();
    tabs.setAutoselect(false);
    Map<Tab, String> tabsToLinks = new HashMap<>();
    tabs.addSelectedChangeListener(e ->
        tabs.getUI().ifPresent(ui -> ui.navigate(tabsToLinks.get(tabs.getSelectedTab())))
    );


    Tab tab = new Tab("Homepage");
    tabsToLinks.put(tab, "");
    tabs.add(tab);

    H1 header = new H1("Admin Chat View");

    add(header, tabs);

    Grid<Chat> table = new Grid<>(Chat.class, false);
    table.addColumn(Chat::getName).setHeader("Name").setResizable(true);
    table.addColumn(c->{
      Hibernate.initialize(c.getOwner().getAuthorities());
      return c.getOwner().getUsername();
    }
    ).setHeader("Owner").setResizable(true);
    table.addColumn(chat->chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).setHeader("Created at").setResizable(true);
    table.addColumn(Chat::isDeleted).setHeader("Deleted").setResizable(true);
    table.setSelectionMode(SelectionMode.SINGLE);
    table.setItems(chatService.findAllNotDeletedChats());

    HorizontalLayout bar = new HorizontalLayout();
    Button manageUsersButton = new Button("Manage User bans");
    manageUsersButton.addClickListener(e->new BanUserDialog(userService, authContext).open());
    bar.add(manageUsersButton);
    Button createChatButton = new Button("Create Chat");
    createChatButton.addClickListener(e->new CreateChatDialog(chatService, authContext).open());
    bar.add(createChatButton);
    Button deleteChatButton = new Button("Delete Chat");
    deleteChatButton.addClickListener(e->{
      Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
      if (chatOptional.isPresent()) {
        try {
          chatService.deleteChat(chatOptional.get(), authContext.getPrincipalName().get());
          table.setItems(chatService.findAllNotDeletedChats());
        }
        catch(Exception ex) {
          Notification notification = Notification.show(ex.getMessage());
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
      }
    });
    bar.add(deleteChatButton);

    add(bar, table);
  }
}
