package com.jun.crewcheckback.application;

import org.springframework.web.bind.annotation.GetMapping;

public class MainController {

    @GetMapping("/health")
    public String healthCheck(){
        return "health";
    }
}
