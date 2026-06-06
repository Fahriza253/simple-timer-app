package com.dpzstudio.timer.util;

public class InputValidation {

    private static final String ALPHA_NUMERIC_AND_SPACE = "^[A-Za-z0-9 ]+$";

    private InputValidation() { }

    public static boolean isValidTagName(String value) {
        return value != null && !value.isBlank() && value.matches(ALPHA_NUMERIC_AND_SPACE);
    }

    public static String normalizeName(String rawName) {
        if (rawName == null) return "";

        String normalized = rawName.trim().replaceAll("\\s+", " ");
        return toTitleCase(normalized);
    }

    private static String toTitleCase(String value) {
        if (value.isBlank()) return value;

        StringBuilder result = new StringBuilder();
        String[] parts = value.split(" ");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
            if (i < parts.length - 1) {
                result.append(' ');
            }
        }

        return result.toString();
    }
}
