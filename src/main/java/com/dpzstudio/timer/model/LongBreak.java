package com.dpzstudio.timer.model;

public class LongBreak implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return AppConfig.getInstance().getLongBreakMinute(); }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Long Break"; }
}
