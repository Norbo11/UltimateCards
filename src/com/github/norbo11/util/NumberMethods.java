package com.github.norbo11.util;

public class NumberMethods {
    public static double getDouble(String amount) {
        try {
            double dbl = Double.parseDouble(amount);
            if (dbl >= 0) return dbl;
            else return -99999;
        } catch (Exception e) {
            return -99999;
        }
    }

    // Returns integer if the supplied string is an integer, otherwise returns
    // -99999
    public static int getInteger(String string) {
        try {
            int integer = Integer.parseInt(string);
            if (integer >= 0) return integer;
            else return -99999;
        } catch (Exception e) {
            return -99999;
        }
    }

    public static double roundDouble(double valueToRound, int numberOfDecimalPlaces) {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return Math.round(interestedInZeroDPs) / multipicationFactor;
    }
}
