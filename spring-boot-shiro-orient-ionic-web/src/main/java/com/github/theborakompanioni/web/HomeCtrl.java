package com.github.theborakompanioni.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by void on 21.08.15.
 */
@Controller
public class HomeCtrl {
    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping("/login")
    String login() {
        return "login";
    }
}
