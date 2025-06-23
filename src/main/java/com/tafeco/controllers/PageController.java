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
        return "public/register";
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    @GetMapping("/card")
    public String card() {
        return "public/card";
    }

    @GetMapping("/shop")
    public String shop() {
        return "public/shop";
    }

    @GetMapping("/sale")
    public String sale() {
        return "public/sale";
    }

    @GetMapping("/condition")
    public String condition() {
        return "public/condition";
    }

    @GetMapping("/chiken")
    public String chiken() {
        return "public/chiken";
    }
    @GetMapping("/chiken_joven")
    public String chiken_joven() {
        return "public/chiken_joven";
    }
    @GetMapping("/confirmar_order")
    public String confirmar_order() {
        return "public/confirmar_order";
    }
    @GetMapping("/conserva")
    public String conserva() {
        return "public/conserva";
    }
    @GetMapping("/egg")
    public String egg() {
        return "public/egg";
    }

    @GetMapping("/finalOrder")
    public String finalOrder() {
        return "public/finalOrder";
    }
    @GetMapping("/green")
    public String green() {
        return "public/green";
    }

    @GetMapping("/milk")
    public String milk() {
        return "public/milk";
    }
    @GetMapping("/order")
    public String order() {
        return "public/order";
    }
    @GetMapping("/vegetables")
    public String vegetables() {
        return "public/vegetables";
    }
}

