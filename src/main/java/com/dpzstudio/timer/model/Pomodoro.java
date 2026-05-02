package com.dpzstudio.timer.model;

public class Pomodoro implements PomodoroMode {

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return AppConfig.getInstance().getPomodoroMinute(); }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Pomodoro"; }
}
