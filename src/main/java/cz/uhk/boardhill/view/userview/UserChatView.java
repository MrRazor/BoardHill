package cz.uhk.boardhill.view.userview;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "chat/user", layout = MainLayout.class)
@RolesAllowed("USER")
public class UserChatView extends VerticalLayout {

}
