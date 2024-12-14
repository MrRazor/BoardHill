package cz.uhk.boardhill.view.ownerview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

@Route(value = "chat/owner", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class OwnerChatView extends VerticalLayout {

    public OwnerChatView(UserService userService, ChatService chatService, AuthenticationContext authContext) {

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

        H1 header = new H1("Owner Chat View");

        add(header, tabs);

        Grid<Chat> table = new Grid<>(Chat.class, false);
        table.addColumn(Chat::getName).setHeader("Name").setResizable(true);
        table.addColumn(c -> c.getOwner().getUsername()).setHeader("Owner").setResizable(true);
        table.addColumn(chat -> chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).setHeader("Created at").setResizable(true);
        table.addColumn(Chat::isDeleted).setHeader("Deleted").setResizable(true);
        table.setSelectionMode(SelectionMode.SINGLE);
        table.setItems(chatService.findAllNotDeletedChatsByOwner(authContext.getPrincipalName().get()));

        HorizontalLayout bar = new HorizontalLayout();
        Button createChatButton = new Button("Create Chat");
        createChatButton.addClickListener(e -> new CreateChatDialog(chatService, table, authContext).open());
        bar.add(createChatButton);
        Button deleteChatButton = new Button("Delete Chat");
        deleteChatButton.addClickListener(e -> {
            Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
            if (chatOptional.isPresent()) {
                try {
                    chatService.deleteChat(chatOptional.get(), authContext.getPrincipalName().get());
                    table.setItems(chatService.findAllNotDeletedChatsByOwner(authContext.getPrincipalName().get()));
                } catch (IllegalArgumentException | IllegalStateException e1) {
                    Notification notification = Notification.show(e1.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (Exception e2) {
                    Notification notification = Notification.show("Deleting chat failed!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        bar.add(deleteChatButton);
        Button manageChatUsersButton = new Button("Manage Chat Users");
        manageChatUsersButton.addClickListener(e -> {
            Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
            chatOptional.ifPresent(chat -> new ManageChatUsersDialog(userService, chatService, chat.getName(), authContext).open());
        });
        bar.add(manageChatUsersButton);
        Button addChatUserButton = new Button("Add Chat User");
        addChatUserButton.addClickListener(e -> {
            Optional<Chat> chatOptional = table.getSelectedItems().stream().findFirst();
            chatOptional.ifPresent(chat -> new AddChatUserDialog(userService, chatService, chat.getName(), authContext).open());
        });
        bar.add(addChatUserButton);

        add(bar, table);
    }
}
