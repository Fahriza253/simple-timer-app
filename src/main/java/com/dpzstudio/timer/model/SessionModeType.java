package com.dpzstudio.timer.model;

/**
 * Enum untuk tipe mode session yang dicatat di database.
 * Digunakan untuk tracking statistik dan membedakan antara session types.
 *
 * Design Principle:
 * - Single Source of Truth: Semua mode type constants di satu tempat
 * - Type Safety: Compiler akan catch invalid mode values
 * - Extensibility: Easy untuk add mode type baru
 */
public enum SessionModeType {

    /**
     * Pomodoro work session.
     * Only this type is recorded in statistics.
     */
    POMODORO("POMODORO", "Pomodoro Work Session"),

    /**
     * Short break session.
     * Not recorded in statistics per business requirement.
     */
    SHORT_BREAK("SHORT_BREAK", "Short Break"),

    /**
     * Long break session.
     * Not recorded in statistics per business requirement.
     */
    LONG_BREAK("LONG_BREAK", "Long Break");

    private final String dbValue;
    private final String displayName;

    /**
     * Constructor untuk SessionModeType.
     *
     * @param dbValue     Value yang disimpan di database
     * @param displayName Display name untuk UI
     */
    SessionModeType(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    /**
     * Gets the database value untuk mode ini.
     *
     * @return database value string
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Gets display name untuk UI.
     *
     * @return display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks apakah mode ini adalah Pomodoro mode.
     * Used untuk determine apakah session harus di-record di statistics.
     *
     * @return true jika ini adalah POMODORO mode
     */
    public boolean isPomodoro() {
        return this == POMODORO;
    }

    /**
     * Checks apakah mode ini adalah Break mode.
     * (Either SHORT_BREAK atau LONG_BREAK)
     *
     * @return true jika ini adalah break mode
     */
    public boolean isBreakMode() {
        return this == SHORT_BREAK || this == LONG_BREAK;
    }

    /**
     * Converts database value ke SessionModeType enum.
     *
     * @param dbValue value dari database
     * @return SessionModeType corresponding value
     * @throws IllegalArgumentException jika value tidak recognize
     */
    public static SessionModeType fromDbValue(String dbValue) {
        if (dbValue == null)
            throw new IllegalArgumentException("SessionModeType value tidak boleh null");

        for (SessionModeType mode : values()) {
            if (mode.dbValue.equals(dbValue)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown SessionModeType: " + dbValue);
    }

    /**
     * Converts PomodoroMode implementation ke SessionModeType.
     * Helper method untuk bridge antara PomodoroMode interface dan SessionModeType enum.
     *
     * @param mode PomodoroMode instance
     * @return SessionModeType corresponding value
     * @throws IllegalArgumentException jika mode tidak recognized
     */
    public static SessionModeType fromPomodoroMode(PomodoroMode mode) {
        if (mode instanceof Pomodoro) {
            return POMODORO;
        } else if (mode instanceof ShortBreak) {
            return SHORT_BREAK;
        } else if (mode instanceof LongBreak) {
            return LONG_BREAK;
        } else {
            throw new IllegalArgumentException("Unknown PomodoroMode type: " + mode.getClass().getName());
        }
    }
}
