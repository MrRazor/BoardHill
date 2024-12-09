package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.uhk.boardhill.entity.User;
import cz.uhk.boardhill.service.UserService;

public class ShowChatUsersDialog extends Dialog {
  public ShowChatUsersDialog(UserService userService, String chatName) {

    Grid<User> chatUsersGrid = new Grid<>();
    chatUsersGrid.addColumn(User::getUsername).setHeader("Username").setResizable(true);
    chatUsersGrid.addColumn(User::isEnabled).setHeader("Enabled").setResizable(true);
    chatUsersGrid.setItems(userService.findAllUsersByChat(chatName));
    chatUsersGrid.setSelectionMode(SelectionMode.NONE);

    add(new VerticalLayout(new H3("Manage Chat Users"), chatUsersGrid));
  }
}
