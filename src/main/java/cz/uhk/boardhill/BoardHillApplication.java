package cz.uhk.boardhill;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import cz.uhk.boardhill.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@Push
@AllArgsConstructor
@SpringBootApplication
public class BoardHillApplication implements CommandLineRunner, AppShellConfigurator {

    public static final ZoneId DEFAULT_TZ = ZoneId.of("CET");
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("cs");

    private static final Logger LOGGER = LogManager.getLogger(BoardHillApplication.class);
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(BoardHillApplication.class, args);
    }

    @Override
    public void run(String... args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        Locale.setDefault(Locale.UK);

        try {
            userService.register("admin", "password", true);
            LOGGER.info("Default user with admin rights (username: \"admin\", password: \"password\") created.");
        } catch (Exception e) {
            LOGGER.info("Default user with admin rights (username: \"admin\", password: \"password\") was not created, probably already exists (is created at first run of application).");
        }
        try {
            userService.register("user", "password", false);
            LOGGER.info("Default user without admin rights (username: \"user\", password: \"password\") created.");
        } catch (Exception e) {
            LOGGER.info("Default user without admin rights (username: \"user\", password: \"password\") was not created, probably already exists (is created at first run of application).");
        }
    }
}