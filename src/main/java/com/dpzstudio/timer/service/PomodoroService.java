package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.model.LongBreak;
import com.dpzstudio.timer.model.Pomodoro;
import com.dpzstudio.timer.model.PomodoroMode;
import com.dpzstudio.timer.model.ShortBreak;

public class PomodoroService {

    private int sessionCount = 0;

    private final AppConfigService configService = new AppConfigService();

    public PomodoroMode determineNextMode(PomodoroMode current) {
        AppConfig config = configService.loadCfg();

        if (current instanceof Pomodoro) {
            sessionCount++;
            if (sessionCount % config.getLongBreakInterval() == 0) {
                return new LongBreak(config.getLongBreakMinute());
            }
            return new ShortBreak(config.getShortBreakMinute());
        }

        return new Pomodoro(config.getPomodoroMinute());
    }

    public boolean shouldAutoStart(PomodoroMode next) {
        AppConfig config = configService.loadCfg();
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
