package cz.uhk.boardhill.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "chat/owner", layout = MainLayout.class)
@RolesAllowed("USER")
public class OwnerChatView extends VerticalLayout {

}
