# Refactor Code

## UX/behavior issue

2. `HomeController.switchMode(...)` only prompts when timer is running
   - If the timer is paused, mode switching will still reset the timer without a confirmation dialog.
   - That can silently discard a paused session and mark it as failed in `resetTimer()`.

Fix `switchMode` so when timer is play or pause show prompts confirmation.

## Main logic/business issue

`PomodoroService` caches mode objects on construction

- `private Pomodoro pomodoro; private ShortBreak shortBreak; private LongBreak longBreak;`
- These are created once from the config loaded at app startup.
- If the user updates timer settings in `SettingController`, `HomeController` reloads `appConfig`, but `progressTracker` still uses the old `shortBreak` / `longBreak` durations.
- Result: after changing break durations, the next auto-selected mode can use stale values until the app restarts.

Fix this Issue!

- Refresh or rebuild `PomodoroService` mode objects when settings change.
- Make `PomodoroService` use the latest config dynamically for mode durations.

## About

Version : 1.5.2
Date: 11 Mei 2026
