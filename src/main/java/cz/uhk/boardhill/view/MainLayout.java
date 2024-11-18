package cz.uhk.boardhill.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

public class MainLayout extends AppLayout {

  private final transient AuthenticationContext authContext;

  public MainLayout(AuthenticationContext authContext) {
    this.authContext = authContext;

    H4 logo;
    if (authContext.isAuthenticated()) {
      logo = new H4("Welcome User: " +
          authContext.getAuthenticatedUser(UserDetails.class).get().getUsername());
      logo.getStyle().set("left", "var(--lumo-space-l)").set("position", "absolute");

      Button logout = new Button("Logout", click ->
          this.authContext.logout());
      logout.getStyle().set("right", "var(--lumo-space-l)").set("position", "absolute");
      addToNavbar(logo, logout);
    } else {
      logo = new H4("Welcome");
      logo.getStyle().set("left", "var(--lumo-space-l)").set("position", "absolute");
      addToNavbar(logo);
    }
  }
}