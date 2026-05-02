package com.dpzstudio.timer.model;

public class AppConfig {
    private static AppConfig instance;

    private boolean autoStartPomodoro   = false;
    private boolean autoStartBreak      = false;
    private int longBreakInterval       = 4;

    private int pomodoroMinute      = 25;
    private int shortBreakMinute    = 5;
    private int longBreakMinute     = 15;

    private AppConfig() { }

    public static AppConfig getInstance() {
        if (instance == null) instance = new AppConfig();
        return instance;
    }

    public boolean isAutoStartPomodoro() {
        return autoStartPomodoro;
    }

    public boolean isAutoStartBreak() {
        return autoStartBreak;
    }

    public int getPomodoroMinute() {
        return pomodoroMinute;
    }

    public int getShortBreakMinute() {
        return shortBreakMinute;
    }

    public int getLongBreakMinute() {
        return longBreakMinute;
    }

    public int getLongBreakInterval() {
        return longBreakInterval;
    }

    public void setPomodoroMinute(int newValue) {
        this.pomodoroMinute = newValue;
    }

    public void setShortBreakMinute(int newValue) {
        this.shortBreakMinute = newValue;
    }

    public void setLongBreakMinute(int newValue) {
        this.longBreakMinute = newValue;
    }

    public void setAutoStartPomodoro(boolean mode) {
        this.autoStartPomodoro = mode;
    }

    public void setAutoStartBreak(boolean mode) {
        this.autoStartBreak = mode;
    }

    public void setLongBreakInterval(int interval) {
        this.longBreakInterval = interval;
    }
}
