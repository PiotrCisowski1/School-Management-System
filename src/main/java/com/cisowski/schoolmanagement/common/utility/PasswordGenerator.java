package com.cisowski.schoolmanagement.common.utility;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
    private static final SecureRandom random = new SecureRandom();
    private static final char[] passwordDict = (ALPHA_CAPS + ALPHA + NUMERIC + SPECIAL_CHARS).toCharArray();
    private static Integer passwordLength = 12;

    public static String generatePassword(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            sb.append(passwordDict[random.nextInt(passwordDict.length)]);
        }
        return sb.toString();
    }
}
