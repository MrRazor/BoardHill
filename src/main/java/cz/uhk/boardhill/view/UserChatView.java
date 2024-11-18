package cz.uhk.boardhill.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "chat/user", layout = MainLayout.class)
@RolesAllowed("USER")
public class UserChatView extends VerticalLayout {

}
