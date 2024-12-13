package cz.uhk.boardhill.view.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import cz.uhk.boardhill.service.UserService;

public class RegistrationDialog extends Dialog {

  private final UserService userService;

  public RegistrationDialog(UserService userService) {
    this.userService = userService;

    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    PasswordField passwordAgain = new PasswordField("Password again");
    Button createAccountButton = new Button("Create account");
    createAccountButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    createAccountButton.addClickListener(e->{
      if(username.getValue() == null || username.getValue().equals("") || password.getValue() == null || password.getValue().equals("")) {
        Notification notification = Notification.show("You need to fill everything!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return;
      }
      if(!password.getValue().equals(passwordAgain.getValue())) {
        Notification notification = Notification.show("Password is not same!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return;
      }
      try {
        userService.register(username.getValue(), password.getValue(), false);
        Notification notification = Notification.show("User account created!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        this.close();
      }
      catch(IllegalArgumentException e1) {
        Notification notification = Notification.show(e1.getMessage());
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
      catch(Exception e2) {
        Notification notification = Notification.show("Registration failed!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
    });

    add(new VerticalLayout(new H3("Registration"), username, password, passwordAgain, createAccountButton));
  }


}
