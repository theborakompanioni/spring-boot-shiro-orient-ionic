package com.github.theborakompanioni.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theborakompanioni.Application;
import com.github.theborakompanioni.CorsConfiguration;
import com.github.theborakompanioni.OrientDbConfiguration;
import com.github.theborakompanioni.ShiroConfiguration;
import com.github.theborakompanioni.model.User;
import com.github.theborakompanioni.repository.UserRepository;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;

import javax.servlet.Filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringApplicationConfiguration(classes
        = {Application.class, OrientDbConfiguration.class, ShiroConfiguration.class})
@WebAppConfiguration
@WebIntegrationTest("server.port:0")
@TestExecutionListeners(inheritListeners = false, listeners
        = {DependencyInjectionTestExecutionListener.class})
public class UserRestCtrlIT extends AbstractTestNGSpringContextTests {
    private final String ADMIN_USER_EMAIL = "admin@example.com";
    private final String ADMIN_USER_PASSWORD = "admin";

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        final Filter simpleCorsFilter = this.wac.getBean(CorsConfiguration.SimpleCorsFilter.class);
        assertThat(simpleCorsFilter, is(notNullValue()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .dispatchOptions(true).addFilters(simpleCorsFilter)
                .build();
    }

    @Test
    public void test_mental_sanity() {
        final User admin = userRepo.findByEmail(ADMIN_USER_EMAIL);
        assertThat(admin, notNullValue());
        assertThat(admin.getEmail(), Matchers.equalTo(ADMIN_USER_EMAIL));
    }

    @Test
    public void test_authenticate_success() throws Exception {
        final User admin = userRepo.findByEmail(ADMIN_USER_EMAIL);

        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(admin.getEmail(), ADMIN_USER_PASSWORD));

        mockMvc.perform(post("/users/auth", json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                /*.andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"))
                .andExpect(header().string("Access-Control-Allow-Headers", "x-requested-with"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"))*/;
    }

    @Test
    public void test_authenticate_failure() throws Exception {/*
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(ADMIN_USER_EMAIL, "wrong password"));

        final ResponseEntity<String> response = new TestRestTemplate(
                HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat("/users/auth"),
                HttpMethod.POST, new HttpEntity<>(json, headers), String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));*/


        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(ADMIN_USER_EMAIL, "wrong password"));

        mockMvc.perform(post("/users/auth", json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
