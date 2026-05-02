package com.dpzstudio.timer.model;

public class ShortBreak implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return AppConfig.getInstance().getShortBreakMinute(); }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Short Break"; }
}
