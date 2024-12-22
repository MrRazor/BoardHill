package cz.uhk.boardhill;

import cz.uhk.boardhill.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineRunnerImpl.class);
    private final UserService userService;

    @Override
    public void run(String... args) {
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
