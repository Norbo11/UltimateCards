package com.github.norbo11.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.norbo11.UltimateCards;

public class DateMethods
{
    private static DateFormat dateFormat = new SimpleDateFormat(UltimateCards.getPluginConfig().getDateFormat());

    public static String getDate()
    {
        Date currentDate = new Date();
        return "[" + dateFormat.format(currentDate) + "]";
    }
}
