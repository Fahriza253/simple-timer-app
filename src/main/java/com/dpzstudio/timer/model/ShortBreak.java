package com.dpzstudio.timer.model;

public class ShortBreak implements PomodoroMode {

    private final int minute;

    public ShortBreak(int minute) {
        this.minute = minute;
    }

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return minute; }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Short Break"; }
}
