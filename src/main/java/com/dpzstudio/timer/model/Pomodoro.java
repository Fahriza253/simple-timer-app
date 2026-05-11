package com.dpzstudio.timer.model;

public class Pomodoro implements PomodoroMode {

    private final int minute;

    public Pomodoro(int minute) {
        this.minute = minute;
    }

    @Override
    public int getDefaultHours() { return 0; }

    @Override
    public int getDefaultMinutes() { return minute; }

    @Override
    public int getDefaultSeconds() { return 0; }

    @Override
    public String getModeName() { return "Pomodoro"; }

}
