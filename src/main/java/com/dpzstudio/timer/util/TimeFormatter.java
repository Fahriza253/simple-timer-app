package com.dpzstudio.timer.util;

import javafx.scene.control.TextFormatter;

public class TimeFormatter {

    public static TextFormatter<String> createTimeFormatter(int maxValue) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) return change;

            if (!newText.matches("\\d+")) return null;

            try {
                int value = Integer.parseInt(newText);
                if (value >= 0 && value <= maxValue && newText.length() <= 2) return change;

            } catch (NumberFormatException e) {
                return null;
            }

            return null;
        });
    }

    public static int parseOrZero(String txt) {
        if (txt == null || txt.isBlank()) return 0;

        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String formatTwoDigit(int val) {
        return String.format("%02d", val);
    }

}
