package com.example.demo.util;

public final class GenerateCode {

    public static String generateProductCode() {

        String code = "P" + System.currentTimeMillis();
        return code;

    }

    public static String generateCategoryCode() {

        String code = "C" + System.currentTimeMillis();
        return code;

    }

    public static String generateProductOrderCode() {

        String code = "PO" + System.currentTimeMillis();
        return code;

    }

}
