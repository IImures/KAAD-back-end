package com.imures.kaadbackend.configuration;

public class PhoneNumberConverter {

    public static String convertToCompactFormat(String phoneNumber) {
        return phoneNumber.replaceAll("[^\\d+]", "").replaceAll("(?<!^)\\+", "");
    }
}