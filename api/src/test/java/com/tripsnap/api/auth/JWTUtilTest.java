package com.tripsnap.api.auth;

import org.junit.jupiter.api.Test;

class JWTUtilTest {
    JWTUtil jwtUtil = new JWTUtil();

    @Test
    void createToken() {

        System.out.println(jwtUtil.createToken("yewl1110@naver.com"));;
    }

    @Test
    void verify() {
        System.out.println(jwtUtil.verify(jwtUtil.createToken("yewl1110@naver.com")));
    }
}