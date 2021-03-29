package com.github.norbo11.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.norbo11.util.config.PluginConfig;

public class DateMethods {
    private static DateFormat dateFormat = new SimpleDateFormat(PluginConfig.getDateFormat());

    public static String getDate() {
        Date currentDate = new Date();
        return "[" + dateFormat.format(currentDate) + "]";
    }
}
