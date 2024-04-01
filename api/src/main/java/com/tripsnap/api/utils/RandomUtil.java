package com.tripsnap.api.utils;

import java.util.Random;

public class RandomUtil {

    public static String getRandomString(int length) {
        String charList = "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i =0; i<length; i++) {
            int index = random.nextInt(charList.length());
            sb.append(charList.charAt(index));
        }
        return sb.toString();
    }
}
