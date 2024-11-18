package cz.uhk.boardhill.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "", layout = MainLayout.class)
@RolesAllowed({"USER", "ADMIN"})
public class LinkView extends VerticalLayout {
  private final transient AuthenticationContext authContext;

  public LinkView(AuthenticationContext authContext) {
    setAlignItems(Alignment.CENTER);
    this.authContext = authContext;

    List<GrantedAuthority> authorities = authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities().stream().collect(Collectors.toList());
    if (authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_USER"))) {
      Button userChatView = new Button("User Chat View");
      add(userChatView);
      userChatView.addClickListener(e ->
          userChatView.getUI().ifPresent(ui ->
              ui.navigate("chat/user"))
      );
      Button ownerChatView = new Button("Owner Chat View");
      add(ownerChatView);
      ownerChatView.addClickListener(e ->
          ownerChatView.getUI().ifPresent(ui ->
              ui.navigate("chat/owner"))
      );
    }
    if (authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"))) {
      Button adminChatView = new Button("Admin Chat View");
      add(adminChatView);
      adminChatView.addClickListener(e ->
          adminChatView.getUI().ifPresent(ui ->
              ui.navigate("chat/admin"))
      );
    }
  }
}