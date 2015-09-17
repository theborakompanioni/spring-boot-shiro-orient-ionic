package com.github.theborakompanioni.rest;

import com.github.theborakompanioni.model.User;
import com.github.theborakompanioni.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/users")
public class UserRestCtrl {

    private static final Logger log = LoggerFactory.
            getLogger(UserRestCtrl.class);

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    private UserRepository userRepo;

    @RequestMapping(value = "/auth", method = POST)
    public void authenticate(@RequestBody final UsernamePasswordToken credentials) {
        log.info("Authenticating {}", credentials.getUsername());
        final Subject subject = SecurityUtils.getSubject();
        subject.login(credentials);
        // set attribute that will allow session querying
        subject.getSession().setAttribute("email", credentials.getUsername());
    }

    @RequestMapping(method = GET)
    @RequiresAuthentication
    @RequiresRoles("administrator")
    public List<User> getAll() {
        return userRepo.findAll();
    }
}
