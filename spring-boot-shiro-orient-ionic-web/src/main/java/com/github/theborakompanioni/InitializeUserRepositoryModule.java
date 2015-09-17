package com.github.theborakompanioni;

import com.github.theborakompanioni.model.Permission;
import com.github.theborakompanioni.model.Role;
import com.github.theborakompanioni.model.User;
import com.github.theborakompanioni.repository.PermissionRepository;
import com.github.theborakompanioni.repository.RoleRepository;
import com.github.theborakompanioni.repository.UserRepository;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by void on 22.08.15.
 */
@Configuration
public class InitializeUserRepositoryModule {
    private static final Logger log = LoggerFactory.
            getLogger(InitializeUserRepositoryModule.class);

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Bean
    public InitializingBean initializeUserRepository() {
        return () -> {
            log.info("Initializing user scenario..");

            // clean-up users, roles and permissions
            userRepo.deleteAll();
            roleRepo.deleteAll();
            permissionRepo.deleteAll();

            // define permissions
            final Permission p1 = new Permission();
            p1.setName("user:delete");
            final Permission p2 = new Permission();
            p2.setName("user:create");

            List<Permission> permissions = Arrays.asList(p1, p2);
            //permissions.forEach(permissionRepo::save);

            // define roles
            final Role roleAdmin = new Role();
            roleAdmin.setName("administrator");
            roleAdmin.setPermissions(permissions);

            final Role roleUser = new Role();
            roleUser.setName("user");

            //Arrays.asList(roleAdmin, roleUser).forEach(roleRepo::save);

            // define users
            final User user = new User();
            user.setActive(true);
            user.setCreated(System.currentTimeMillis());
            user.setEmail("user@example.com");
            user.setName("John Doe");
            user.setPassword(passwordService.encryptPassword("password"));
            user.setRoles(Collections.singletonList(roleUser));

            final User admin = new User();
            admin.setActive(true);
            admin.setCreated(System.currentTimeMillis());
            admin.setEmail("admin@example.com");
            admin.setName("Mr. Admin");
            admin.setPassword(passwordService.encryptPassword("admin"));
            admin.setRoles(Collections.singletonList(roleAdmin));

            Arrays.asList(admin, user).forEach(userRepo::save);

            log.info("User scenario initiated.");
            log.info("Users: " + userRepo.findAll().size());
            log.info("Roles: " + roleRepo.findAll().size());
            log.info("Permissions: " + permissionRepo.findAll().size());

            sanityCheck();
        };
    }


    private void sanityCheck() throws RuntimeException {
        final List<User> users = userRepo.findAll();
        assert users.size() == 2;

        final List<Role> roles = roleRepo.findAll();
        assert roles.size() == 2;

        final List<Permission> permissions = permissionRepo.findAll();
        assert permissions.size() == 2;
    }

}
