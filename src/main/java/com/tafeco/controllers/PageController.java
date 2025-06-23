package com.tafeco.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "public/index"; // имя шаблона index.html в templates
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart"; // templates/cart.html
    }

    @GetMapping("/shop")
    public String shop() {
        return "public/shop"; // templates/shop.html
    }
}

