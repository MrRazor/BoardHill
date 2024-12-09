package cz.uhk.boardhill.view.adminview;

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
import cz.uhk.boardhill.service.UserService;
import java.util.Optional;

public class BanUserDialog extends Dialog {

  public BanUserDialog(UserService userService, AuthenticationContext authContext) {
    VerticalLayout showChatUsersLayout = new VerticalLayout();
    Grid<User> chatUsersGrid = new Grid<>();
    chatUsersGrid.addColumn(User::getUsername).setHeader("Username").setResizable(true);
    chatUsersGrid.addColumn(User::isEnabled).setHeader("Banned").setResizable(true);
    chatUsersGrid.setItems(userService.findAllUsers());
    chatUsersGrid.setSelectionMode(SelectionMode.SINGLE);

    Button banUserButton = new Button("Ban/Unban User");
    banUserButton.addClickListener(e->{
      Optional<User> userOptional = chatUsersGrid.getSelectedItems().stream().findFirst();
      if (userOptional.isPresent()) {
        try {
          if(userOptional.get().isEnabled()) {
            userService.changeUserEnabledStatus(userOptional.get().getUsername(), false, authContext.getPrincipalName().get());
            Notification notification = Notification.show("User account banned!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
          }
          else {
            userService.changeUserEnabledStatus(userOptional.get().getUsername(), true, authContext.getPrincipalName().get());
            Notification notification = Notification.show("User account unbanned!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
          }
          chatUsersGrid.setItems(userService.findAllUsers());
        }
        catch(IllegalArgumentException e1) {
          Notification notification = Notification.show(e1.getMessage());
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        catch(Exception e2) {
          Notification notification = Notification.show("Ban/Unban failed!");
          notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
      }
    });

    showChatUsersLayout.add(banUserButton, chatUsersGrid);

    add(new VerticalLayout(new H3("Block Chat User"), showChatUsersLayout));
  }
}
