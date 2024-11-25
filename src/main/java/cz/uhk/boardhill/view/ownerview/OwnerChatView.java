package cz.uhk.boardhill.view.ownerview;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cz.uhk.boardhill.view.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "chat/owner", layout = MainLayout.class)
@RolesAllowed("USER")
public class OwnerChatView extends VerticalLayout {

}
