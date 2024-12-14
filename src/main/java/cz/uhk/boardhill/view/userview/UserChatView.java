package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.UserService;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Route(value = "chat/user", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class UserChatView extends VerticalLayout {

    public UserChatView(UserService userService, ChatService chatService, AuthenticationContext authContext) {

        setAlignItems(Alignment.CENTER);

        Tabs tabs = new Tabs();
        tabs.setAutoselect(false);
        Map<Tab, String> tabsToLinks = new HashMap<>();
        tabs.addSelectedChangeListener(e ->
                tabs.getUI().ifPresent(ui -> ui.navigate(tabsToLinks.get(tabs.getSelectedTab())))
        );


        Tab tab = new Tab("Homepage");
        tabsToLinks.put(tab, "");
        tabs.add(tab);

        H1 header = new H1("User Chat View");

        add(header, tabs);

        Grid<Chat> table = new Grid<>(Chat.class, false);
        table.addColumn(Chat::getName).setHeader("Name").setResizable(true);
        table.addColumn(c -> c.getOwner().getUsername()).setHeader("Owner").setResizable(true);
        table.addColumn(chat -> chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).setHeader("Created at").setResizable(true);
        table.addColumn(Chat::isDeleted).setHeader("Deleted").setResizable(true);
        table.setSelectionMode(SelectionMode.SINGLE);
        table.setItems(chatService.findAllNotDeletedChatsByUser(authContext.getPrincipalName().get()));

        HorizontalLayout bar = new HorizontalLayout();
        Button manageChatUsersButton = new Button("Show Chat Users");
        manageChatUsersButton.addClickListener(e -> {
            Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
            chatOptional.ifPresent(chat -> new ShowChatUsersDialog(userService, chat.getName()).open());
        });
        bar.add(manageChatUsersButton);
        Button openChatButton = new Button("Open Chat");
        openChatButton.addClickListener(e -> {
            Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
            if (chatOptional.isPresent()) {
                openChatButton.getUI().ifPresent(ui -> ui.navigate("chat/messages/" + chatOptional.get().getName()));
            }
        });
        bar.add(openChatButton);

        add(bar, table);
    }
}
