package cz.uhk.boardhill.view.adminview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.uhk.boardhill.entity.User;

public class BanUserDialog extends Dialog {

  public BanUserDialog() {
    VerticalLayout showChatUsersLayout = new VerticalLayout();
    Grid<User> chatUsersGrid = new Grid<>();
    chatUsersGrid.addColumn(User::getUsername).setHeader("Username").setResizable(true);
    chatUsersGrid.addColumn(User::isEnabled).setHeader("Banned").setResizable(true);
    Button banUserButton = new Button("Ban/Unban User");
    showChatUsersLayout.add(banUserButton, chatUsersGrid);

    add(new VerticalLayout(new H3("Block Chat User"), showChatUsersLayout));
  }
}
