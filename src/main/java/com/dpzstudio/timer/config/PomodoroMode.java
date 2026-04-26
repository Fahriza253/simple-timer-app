package com.dpzstudio.timer.config;

/**
 * Interface for Pomodoro timer modes.
 */
public interface PomodoroMode {

    /**
     * Gets the default hours for this mode.
     * @return default hours
     */
    int getDefaultHours();

    /**
     * Gets the default minutes for this mode.
     * @return default minutes
     */
    int getDefaultMinutes();

    /**
     * Gets the default seconds for this mode.
     * @return default seconds
     */
    int getDefaultSeconds();

    /**
     * Gets the name of this mode.
     * @return mode name
     */
    String getModeName();
}
