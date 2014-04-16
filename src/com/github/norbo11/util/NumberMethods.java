package com.github.norbo11.util;

import java.security.SecureRandom;

public class NumberMethods {
    private static SecureRandom random = new SecureRandom();

    public static double getDouble(String amount) {
        try {
            double dbl = Double.parseDouble(amount);
            if (dbl >= 0) return dbl;
            else return -99999;
        } catch (Exception e) {
            return -99999;
        }
    }

    public static int getInteger(String string) {
        return getInteger(string, false);
    }

    private static int getInteger(String string, boolean positive) {
        try {
            int integer = Integer.parseInt(string);
            if (positive) {
                if (integer < 0) return -99999;
            }
            return integer;
        } catch (Exception e) {
            return -99999;
        }
    }

    // Returns integer if the supplied string is a positiveinteger, otherwise returns -99999
    public static int getPositiveInteger(String string) {
        return getInteger(string, true);
    }

    public static int getRandomInteger(int to) {
        return random.nextInt(to);
    }

    public static double roundDouble(double valueToRound, int numberOfDecimalPlaces) {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return Math.round(interestedInZeroDPs) / multipicationFactor;
    }
}
