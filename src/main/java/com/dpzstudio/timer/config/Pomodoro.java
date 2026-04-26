package com.dpzstudio.timer.config;

/**
 * Pomodoro mode: 25 minutes work session.
 */
public class Pomodoro implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return 25; }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Pomodoro"; }
}
