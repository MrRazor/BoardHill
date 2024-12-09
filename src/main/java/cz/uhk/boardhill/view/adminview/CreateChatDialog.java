package cz.uhk.boardhill.view.adminview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.boardhill.service.ChatService;

public class CreateChatDialog extends Dialog {
  public CreateChatDialog(ChatService chatService, AuthenticationContext authContext) {
    TextField chatName = new TextField("Chat Name");
    Button createChatButton = new Button("Create Chat");
    createChatButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    createChatButton.addClickListener(e->{
      if(chatName == null || chatName.equals("")) {
        Notification notification = Notification.show("You need to fill everything!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
      try {
        chatService.createChat(chatName.getValue(), authContext.getPrincipalName().get());
        Notification notification = Notification.show("Chat created!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        this.close();
      }
      catch(IllegalArgumentException e1) {
        Notification notification = Notification.show(e1.getMessage());
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
      catch(Exception e2) {
        Notification notification = Notification.show("Creating chat failed!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
    });

    add(new VerticalLayout(new H3("Create Chat"), chatName, createChatButton));
  }
}
