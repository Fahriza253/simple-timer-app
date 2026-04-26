package com.dpzstudio.timer.config;

/**
 * Long Break mode: 15 minutes break.
 */
public class LongBreak implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return 15; }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Long Break"; }
}
