package com.github.norbo11.util;

import java.security.SecureRandom;

public class NumberMethods {
    private static SecureRandom random = new SecureRandom();

    public static double getDouble(String amount) throws NumberFormatException {
        try {
            double dbl = Double.parseDouble(amount);
            if (dbl >= 0) return dbl;
            else throw new NumberFormatException();
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    public static int getInteger(String string) throws NumberFormatException {
        return getInteger(string, false);
    }

    private static int getInteger(String string, boolean positive) throws NumberFormatException  {
        try {
            int integer = Integer.parseInt(string);
            if (positive) {
                if (integer < 0) throw new NumberFormatException();
            }
            return integer;
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    public static int getPositiveInteger(String string) throws NumberFormatException {
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
