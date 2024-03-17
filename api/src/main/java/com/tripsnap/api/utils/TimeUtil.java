package com.tripsnap.api.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static Date timeCalc(Date date, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }
}
