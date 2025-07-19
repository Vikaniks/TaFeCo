package com.tafeco.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/cart")
    public String cart() {
        return "public/cart";
    }

    @GetMapping("/shop")
    public String shopPage(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("categoryId", id);
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


    @GetMapping("/confirmar_order")
    public String confirmar_order() {
        return "public/confirmar_order";
    }


    @GetMapping("/finalOrder")
    public String finalOrder() {
        return "public/finalOrder";
    }

    @GetMapping("/order")
    public String order() {
        return "public/order";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/admin";
    }

    @GetMapping("/admin/user")
    public String user() {
        return "admin/user";
    }
    @GetMapping("/admin/orders")
    public String orders() {
        return "admin/orders";
    }
    @GetMapping("/admin/reportOrder")
    public String reportOrder() {
        return "admin/reportOrder";
    }
    @GetMapping("/admin/product")
    public String product() {
        return "admin/product";
    }

    @GetMapping("/admin/reportProduct")
    public String reportProduct() {
        return "admin/reportProduct";
    }

    @GetMapping("/admin/store")
    public String store() {
        return "admin/store";
    }

    @GetMapping("/admin/warehouse")
    public String warehouse() {
        return "admin/warehouse";
    }

}

