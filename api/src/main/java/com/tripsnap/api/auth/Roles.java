package com.tripsnap.api.auth;

public class Roles {
    final public static String USER = "USER";           // 이메일 인증을 마친 회원
    final public static String WAITING = "WAITING";     // 이메일 인증 전 회원
    final public static String ANONYMOUS = "ANONYMOUS"; // 로그인 안한 회원
}
