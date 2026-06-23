package com.tnc.userManagement.config;

import com.tnc.userManagement.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapAdminInitializer implements CommandLineRunner {

    private final IUserService userService;

    @Value("${app.bootstrap-admin.enabled:true}")
    private boolean bootstrapAdminEnabled;

    @Value("${app.bootstrap-admin.first-name:Admin}")
    private String firstName;

    @Value("${app.bootstrap-admin.last-name:System}")
    private String lastName;

    @Value("${app.bootstrap-admin.email:admin@animalshelter.com}")
    private String email;

    @Value("${app.bootstrap-admin.password:Admin123!}")
    private String password;

    @Override
    public void run(String... args) {
        if (!bootstrapAdminEnabled) {
            log.info("Bootstrap admin creation is disabled");
            return;
        }

        if (userService.findByEmail(email) != null) {
            log.info("Bootstrap admin already exists: {}", email);
            return;
        }

        userService.addNewUserWithPassword(
                firstName,
                lastName,
                email,
                password,
                "ROLE_ADMIN",
                true,
                true
        );
        log.info("Bootstrap admin user created: {}", email);
    }
}
