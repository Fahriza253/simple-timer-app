package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.model.LongBreak;
import com.dpzstudio.timer.model.Pomodoro;
import com.dpzstudio.timer.model.PomodoroMode;
import com.dpzstudio.timer.model.ShortBreak;

public class PomodoroService {
    private int sessionCount = 0;

    private final AppConfig config = AppConfig.getInstance();

    private final Pomodoro pomodoro = new Pomodoro();
    private final ShortBreak shortBreak = new ShortBreak();
    private final LongBreak longBreak = new LongBreak();

    public PomodoroMode determineNextMode(PomodoroMode current) {
        if (current instanceof Pomodoro) {
            sessionCount++;

            if (sessionCount % config.getLongBreakInterval() == 0) {
                return longBreak;
            }
            return shortBreak;
        }
        return pomodoro;
    }

    public boolean shouldAutoStart(PomodoroMode next) {
        if (next instanceof Pomodoro) {
            return config.isAutoStartPomodoro();
        }
        return config.isAutoStartBreak();
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void reset() {
        sessionCount = 0;
    }
}
