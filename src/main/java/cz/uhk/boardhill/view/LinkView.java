package cz.uhk.boardhill.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class LinkView extends VerticalLayout {
  private final transient AuthenticationContext authContext;

  public LinkView(AuthenticationContext authContext) {
    setAlignItems(Alignment.CENTER);
    this.authContext = authContext;

    List<GrantedAuthority> authorities = authContext.getAuthenticatedUser(UserDetails.class).get().getAuthorities().stream().collect(Collectors.toList());
    if (authorities.stream().map(a -> a.getAuthority()).anyMatch(a -> a.equals("ROLE_ADMIN"))) {

    } else {

    }
  }
}