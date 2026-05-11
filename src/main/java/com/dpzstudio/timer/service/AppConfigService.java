package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.repository.AppConfigRepository;

public class AppConfigService {

    private static AppConfig cacheCfg;

    private final AppConfigRepository repo = new AppConfigRepository();

    public AppConfig loadCfg() {
        if (cacheCfg != null) return cacheCfg;

        try {
            AppConfig cfg = repo.find();

            if (cfg == null)
                cfg = AppConfig.createDefault();

            cacheCfg = cfg;
        } catch (Exception e) {
            e.printStackTrace();

            cacheCfg = AppConfig.createDefault();
        }

        return cacheCfg;
    }

    public void saveCfg(AppConfig cfg) {
        validate(cfg);

        try {
            repo.update(cfg);
            cacheCfg = cfg;
        } catch (Exception e) {
            throw new RuntimeException("Failed save app config", e);
        }
    }

    private void validate(AppConfig cfg) {
        if (cfg.getPomodoroMinute() < 1)
            throw new IllegalArgumentException("Pomodoro minute invalid!");

        if (cfg.getShortBreakMinute() < 1)
            throw new IllegalArgumentException("Short break minute invalid");

        if (cfg.getLongBreakMinute() < 1)
            throw new IllegalArgumentException("Long break minute invalid");

        if (cfg.getLongBreakInterval() < 1)
            throw new IllegalArgumentException("Long break interval invalid");
    }
}
