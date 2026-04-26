package com.dpzstudio.timer.config;

/**
 * Short Break mode: 5 minutes break.
 */
public class ShortBreak implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return 5; }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Short Break"; }
}
