package com.tripsnap.api.controller;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PersistenceContext
    EntityManager entityManager;

    @GetMapping("/test")
    public void test() {
//        JPAQuery<Member> query = new JPAQuery<>(entityManager);
//        QM
    }
}
