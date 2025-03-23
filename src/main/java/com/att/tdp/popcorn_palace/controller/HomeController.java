package com.att.tdp.popcorn_palace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle root URL redirects
 */
@Controller
public class HomeController {

    /**
     * Redirects the root URL to the Swagger UI
     * 
     * @return redirect to Swagger UI
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui/index.html";
    }
}