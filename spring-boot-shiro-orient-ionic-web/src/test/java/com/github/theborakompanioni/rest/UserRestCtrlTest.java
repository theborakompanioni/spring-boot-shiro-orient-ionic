package com.github.theborakompanioni.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theborakompanioni.Application;
import com.github.theborakompanioni.OrientDbConfiguration;
import com.github.theborakompanioni.ShiroConfiguration;
import com.github.theborakompanioni.model.Permission;
import com.github.theborakompanioni.model.Role;
import com.github.theborakompanioni.model.User;
import com.github.theborakompanioni.repository.PermissionRepository;
import com.github.theborakompanioni.repository.RoleRepository;
import com.github.theborakompanioni.repository.UserRepository;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes
        = {Application.class, OrientDbConfiguration.class, ShiroConfiguration.class})
@WebIntegrationTest("server.port:0")
public class UserRestCtrlTest extends AbstractJUnit4SpringContextTests {
    private final String USER_NAME = "John Doe";
    private final String USER_EMAIL = "john_doe@example.org";
    private final String USER_PWD = "any_password";

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    private AbstractShiroFilter shiroFilter;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void beforeTest() {
        setupRepositories();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .dispatchOptions(true).addFilters(shiroFilter)
                .build();
    }

    private void setupRepositories() {
        // clean-up users, roles and permissions
        userRepo.deleteAll();
        roleRepo.deleteAll();
        permissionRepo.deleteAll();

        // define permissions
        final Permission p1 = new Permission();
        p1.setName("VIEW_USER_ROLES");
        permissionRepo.save(p1);

        // define roles
        final Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        roleRepo.save(roleAdmin);

        // define user
        final User user = new User();
        user.setActive(true);
        user.setCreated(System.currentTimeMillis());
        user.setEmail(USER_EMAIL);
        user.setName(USER_NAME);
        user.setPassword(passwordService.encryptPassword(USER_PWD));
        user.getRoles().add(roleAdmin);
        userRepo.save(user);
    }

    @Test
    public void test_count() {
        assertThat(userRepo.count(), is(1L));
    }

    @Test
    public void test_authenticate_success() throws Exception {
        final User admin = userRepo.findByEmail(USER_EMAIL);

        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(admin.getEmail(), USER_PWD));

        mockMvc.perform(post("/users/auth").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void test_authenticate_failure() throws Exception {
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, "wrong password"));

        mockMvc.perform(post("/users/auth").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
