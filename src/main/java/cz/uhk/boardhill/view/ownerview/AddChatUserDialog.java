package cz.uhk.boardhill.view.ownerview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.entity.User;
import cz.uhk.boardhill.service.ChatService;
import cz.uhk.boardhill.service.UserService;
import java.util.Optional;

public class AddChatUserDialog extends Dialog {
  public AddChatUserDialog(UserService userService, ChatService chatService, String chatName, AuthenticationContext authContext) {
    setWidth("600px");
    setMinWidth("400px");
    setMaxWidth("90vw");

    VerticalLayout showChatUsersLayout = new VerticalLayout();
    showChatUsersLayout.setSizeFull();
    showChatUsersLayout.setPadding(false);
    showChatUsersLayout.setSpacing(true);

    showChatUsersLayout.setMaxHeight("80vh");
    showChatUsersLayout.getStyle().set("overflow", "auto");

    Grid<User> chatUsersGrid = new Grid<>();
    chatUsersGrid.addColumn(User::getUsername).setHeader("Username").setResizable(true);
    chatUsersGrid.addColumn(User::isEnabled).setHeader("Enabled").setResizable(true);
    chatUsersGrid.setItems(userService.findAllUsers());
    chatUsersGrid.setSelectionMode(SelectionMode.SINGLE);

    chatUsersGrid.setWidthFull();

    Button banUserButton = new Button("Add User");
    banUserButton.addClickListener(e->{
      Optional<User> userOptional = chatUsersGrid.getSelectedItems().stream().findFirst();
      if (userOptional.isPresent()) {
        try {
          chatService.addUserToChat(chatName, authContext.getPrincipalName().get(), userOptional.get().getUsername());
          Notification notification = Notification.show("User added!");
          notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        catch(IllegalArgumentException e1) {
          Notification notification = Notification.show(e1.getMessage());
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        catch(Exception e2) {
          Notification notification = Notification.show("Adding user failed!");
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
      }
    });

    showChatUsersLayout.add(banUserButton, chatUsersGrid);

    add(new VerticalLayout(new H3("Add Chat User"), showChatUsersLayout));
  }
}
