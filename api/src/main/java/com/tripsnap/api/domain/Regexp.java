package com.tripsnap.api.domain;

public class Regexp {
    public static final String NICKNAME = "^[a-zA-Z가-힣][0-9a-zA-Z가-힣]{4,19}$";
    public static final String PASSWORD = "^(?=.*[a-zA-Z])(?=.*[0-9]).{12,100}$";
}
