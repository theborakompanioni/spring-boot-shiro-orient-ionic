package com.github.theborakompanioni.web;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by void on 05.09.15.
 */
@Controller
public class LogoutCtrl {

    private static final Logger log = LoggerFactory.
            getLogger(LogoutCtrl.class);

    @RequestMapping(value = "/logout", method = GET)
    public String logout() {
        Optional.ofNullable(SecurityUtils.getSubject())
                .ifPresent(subject -> {
                    log.info("Logout {}", subject.getPrincipal());
                    subject.logout();
                });

        return "redirect:/";
    }
}
