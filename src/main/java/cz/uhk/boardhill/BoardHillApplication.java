package cz.uhk.boardhill;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@Push
@SpringBootApplication
public class BoardHillApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    public static final ZoneId VIEW_TZ = ZoneId.of("CET");
    public static final Locale VIEW_LOCALE = Locale.UK;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        Locale.setDefault(Locale.UK);

        SpringApplication.run(BoardHillApplication.class, args);
    }

}