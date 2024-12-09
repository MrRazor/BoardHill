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

public class ManageChatUsersDialog extends Dialog {
  public ManageChatUsersDialog(UserService userService, ChatService chatService, String chatName, AuthenticationContext authContext) {
    VerticalLayout showChatUsersLayout = new VerticalLayout();

    Grid<User> chatUsersGrid = new Grid<>();
    chatUsersGrid.addColumn(User::getUsername).setHeader("Username").setResizable(true);
    chatUsersGrid.addColumn(User::isEnabled).setHeader("Enabled").setResizable(true);
    chatUsersGrid.setItems(userService.findAllUsersByChat(chatName));
    chatUsersGrid.setSelectionMode(SelectionMode.SINGLE);

    Button banUserButton = new Button("Kick User");
    banUserButton.addClickListener(e->{
      Optional<User> userOptional = chatUsersGrid.getSelectedItems().stream().findFirst();
      if (userOptional.isPresent()) {
        try {
          chatService.removeUserFromChat(chatName, authContext.getPrincipalName().get(), userOptional.get().getUsername());
          Notification notification = Notification.show("User kicked!");
          notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
          chatUsersGrid.setItems(userService.findAllUsersByChat(chatName));
        }
        catch(IllegalArgumentException e1) {
          Notification notification = Notification.show(e1.getMessage());
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        catch(Exception e2) {
          Notification notification = Notification.show("Kicking user failed!");
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
      }
    });

    showChatUsersLayout.add(banUserButton, chatUsersGrid);

    add(new VerticalLayout(new H3("Manage Chat Users"), showChatUsersLayout));
  }
}
