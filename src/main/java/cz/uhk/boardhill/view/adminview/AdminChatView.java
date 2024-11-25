package cz.uhk.boardhill.view.adminview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Route(value = "chat/admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminChatView extends VerticalLayout {
  private final ChatService chatService;

  public AdminChatView(ChatService chatService) {
    this.chatService = chatService;

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

    HorizontalLayout bar = new HorizontalLayout();
    Button manageUsersButton = new Button("Manage User bans");
    bar.add(manageUsersButton);
    Button createChatButton = new Button("Create Chat");
    bar.add(createChatButton);
    Button deleteChatButton = new Button("Delete Chat");
    bar.add(deleteChatButton);

    Grid<Chat> table = new Grid<>(Chat.class, false);
    table.addColumn(Chat::getName).setHeader("Name").setResizable(true);
    table.addColumn(Chat::getOwner).setHeader("Owner").setResizable(true);
    table.addColumn(chat->chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).setHeader("Created at").setResizable(true);
    table.addColumn(Chat::isDeleted).setHeader("Deleted").setResizable(true);
    table.setItems(chatService.findAllChats());

    add(bar, table);
  }
}
