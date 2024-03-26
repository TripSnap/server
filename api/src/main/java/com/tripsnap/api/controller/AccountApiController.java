package com.tripsnap.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountApiController implements AccountApi {
    @Override
    @GetMapping("/test2")
    public String test() {
        return "hihi";
    }
    @Override
    public String test2() {
        return "hihi";
    }
}
