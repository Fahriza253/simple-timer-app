package com.dpzstudio.timer.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.model.LongBreak;
import com.dpzstudio.timer.model.Pomodoro;
import com.dpzstudio.timer.model.PomodoroMode;
import com.dpzstudio.timer.model.ShortBreak;
import com.dpzstudio.timer.model.Statistic;
import com.dpzstudio.timer.service.AppConfigService;
import com.dpzstudio.timer.service.AudioPlayer;
import com.dpzstudio.timer.service.PomodoroService;
import com.dpzstudio.timer.service.StatisticService;
import com.dpzstudio.timer.service.TagSelectionManager;
import com.dpzstudio.timer.service.TimerEngine;
import com.dpzstudio.timer.util.TimeFormatter;

public class HomeController implements Initializable {

    @FXML private Button btnSetting, btnTagsSetting;
    @FXML private TextField inputSecond, inputMinute, inputHour;
    @FXML private Label labelSecond, labelMinute, labelHour;
    @FXML private Button btnPomodoroMode, btnShortBreakMode, btnLongBreakMode;
    @FXML private Button btnStart, btnPause, btnReset;
    @FXML private ProgressBar progressBar;
    @FXML private Label labelTodayStats, lbSelectedTag;

    private Button activeModeButton;

    private final TimerEngine engine = new TimerEngine();
    private AudioPlayer audioPlayer;

    private final AppConfigService configService = new AppConfigService();
    private AppConfig appConfig;

    private PomodoroMode currentMode;
    private Pomodoro pomodoro;
    private ShortBreak shortBreak;
    private LongBreak longBreak;
    private final PomodoroService progressTracker = new PomodoroService();
    private final StatisticService statisticService = new StatisticService();
    private final TagSelectionManager selectionManager = TagSelectionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureNumericTextField();
        initService();

        appConfig = configService.loadCfg();
        pomodoro = new Pomodoro(appConfig.getPomodoroMinute());
        shortBreak = new ShortBreak(appConfig.getShortBreakMinute());
        longBreak = new LongBreak(appConfig.getLongBreakMinute());

        applyMode(pomodoro);

        updateTodayStats();
        btnSetting.setOnAction(e -> openSettingMenu());
        btnTagsSetting.setOnAction(e -> openTagsMenu());
        btnStart.setOnAction(e -> startTimer());
        btnPause.setOnAction(e -> pauseTimer());
        btnReset.setOnAction(e -> resetTimer());

        btnPomodoroMode.setOnAction(e -> switchMode(pomodoro));
        btnShortBreakMode.setOnAction(e -> switchMode(shortBreak));
        btnLongBreakMode.setOnAction(e -> switchMode(longBreak));

        updateSelectedTagLabel();
    }

    private void configureNumericTextField() {
        inputSecond.setTextFormatter(TimeFormatter.createTimeFormatter(59));
        inputMinute.setTextFormatter(TimeFormatter.createTimeFormatter(59));
        inputHour.setTextFormatter(TimeFormatter.createTimeFormatter(99));
    }

    private void initService() {
        audioPlayer = new AudioPlayer("/sounds/ding.wav");
        if (!audioPlayer.isLoaded()) showWarning("Audio resource not found! Alarm will be silent");
        engine.setCallbacks(this::updateDisplay, this::handleTimerFinished);
    }

    private void startTimer() {
        if (engine.isRunning()) {
            return;
        }

        if (engine.isPaused()) {
            return;
        }

        int total = calculateTotalSecondFromInput();
        if (total < 1) {
            showWarning("Duration Incorrect!");
            return;
        }

        engine.setDuration(total);
        engine.start();
        btnPause.setText("Pause");
        setButtonState(false, true, true);
    }

    private void pauseTimer() {
        if (engine.isRunning()) {
            engine.pause();
            btnPause.setText("Continue");
            setButtonState(false, true, true);
        } else if (engine.isPaused()) {
            engine.start();
            btnPause.setText("Pause");
            setButtonState(false, true, true);
        }
    }

    private void resetTimer() {
        if (engine.isRunning() || engine.isPaused()) {
            recordSessionResult(false);
        }
        engine.hardReset();
        clearTimeDisplay();
        updateDisplay();
        btnPause.setText("Pause");
        setButtonState(true, false, false);
    }

    private void handleTimerFinished() {
        audioPlayer.play();
        recordSessionResult(true);

        PomodoroMode next = progressTracker.determineNextMode(currentMode);

        applyMode(next);

        if (progressTracker.shouldAutoStart(next)) {
            startTimer();
        } else {
            updateDisplay();
            setButtonState(true, false, true);
        }
    }

    private void recordSessionResult(boolean success) {
        if (currentMode == null) {
            return;
        }

        statisticService.recordSession(currentMode, success);
        updateTodayStats();
    }

    private void updateDisplay() {
        int remaining   = engine.getRemainingSecond();
        int total       = engine.getTotalSecond();
        int hours       = remaining / 3600;
        int minutes     = (remaining % 3600) / 60;
        int seconds     = remaining % 60;

        labelHour.setText(TimeFormatter.formatTwoDigit(hours));
        labelMinute.setText(TimeFormatter.formatTwoDigit(minutes));
        labelSecond.setText(TimeFormatter.formatTwoDigit(seconds));

        progressBar.setProgress(
            total > 0 ? (double)(total - remaining) / total : 0
        );
    }

    private void openSettingMenu() {
        try {
            FXMLLoader load = new FXMLLoader(
                getClass().getResource("/views/setting.fxml")
            );
            Parent root = load.load();

            Scene settingScene = new Scene(root);
            settingScene.getStylesheets().add(getClass().getResource("/styles/default-themes.css").toExternalForm());

            Stage settingStage = new Stage();

            settingStage.setTitle("Settings");
            settingStage.setScene(settingScene);
            settingStage.initModality(Modality.APPLICATION_MODAL);
            settingStage.initOwner(btnSetting.getScene().getWindow());
            settingStage.setResizable(false);

            settingStage.showAndWait();

            appConfig = configService.loadCfg();
            if (!engine.isRunning() && currentMode != null) applyMode(currentMode);
        } catch (IOException e) {
            e.printStackTrace();
            showWarning("Failed to load Setting menu");
        }
    }

    private void openTagsMenu() {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/views/tags.fxml"));
            Parent root = load.load();

            Scene tagsScene = new Scene(root);
            tagsScene.getStylesheets().add(getClass().getResource("/styles/default-themes.css").toExternalForm());

            Stage tagsStage = new Stage();
            tagsStage.setTitle("Tags Setting");
            tagsStage.setScene(tagsScene);
            tagsStage.initOwner(btnTagsSetting.getScene().getWindow());
            tagsStage.initModality(Modality.APPLICATION_MODAL);
            tagsStage.setResizable(false);
            tagsStage.showAndWait();
            updateSelectedTagLabel();
        } catch (IOException e) {
            showWarning("Failed to load Tags Setting ");
        }
    }

    private void updateSelectedTagLabel() {
        String selectedName = selectionManager.getSelectedTagName();
        lbSelectedTag.setText(selectedName != null ? "Selected tag: " + selectedName : "Selected tag: none");
    }

    private Button getButtonForMode(PomodoroMode mode) {
        if (mode instanceof Pomodoro)   return btnPomodoroMode;
        if (mode instanceof ShortBreak) return btnShortBreakMode;
        return btnLongBreakMode;
    }

    private void setButtonState(boolean startEnabled, boolean pauseEnabled, boolean resetEnabled) {
        btnStart.setDisable(!startEnabled);
        btnPause.setDisable(!pauseEnabled);
        btnReset.setDisable(!resetEnabled);
    }

    private void switchMode(PomodoroMode mode) {
        if (engine.isRunning() || engine.isPaused()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Switch Mode");
            alert.setHeaderText("Timer is currently active.");
            alert.setContentText("Switching mode will reset the timer. Do you want to continue?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;
        }

        applyMode(mode);
    }

    private void applyMode(PomodoroMode mode) {
        currentMode = mode;
        activeModeButton = getButtonForMode(mode);

        resetTimer();
        setModeInputs();
        updateActiveModeButton();
    }

    private int calculateTotalSecondFromInput() {
        int sec = TimeFormatter.parseOrZero(inputSecond.getText());
        int min = TimeFormatter.parseOrZero(inputMinute.getText());
        int hour = TimeFormatter.parseOrZero(inputHour.getText());

        return (hour * 3600) + (min * 60) + sec;
    }

    private void clearTimeDisplay() {
        inputSecond.setText("00");
        inputMinute.setText("00");
        inputHour.setText("00");
        labelSecond.setText("00");
        labelMinute.setText("00");
        labelHour.setText("00");
    }

    private void setModeInputs() {
        int minute = 0;
        if (currentMode instanceof Pomodoro) {
            minute = appConfig.getPomodoroMinute();
        } else if (currentMode instanceof ShortBreak) {
            minute = appConfig.getShortBreakMinute();
        } else if (currentMode instanceof LongBreak) {
            minute = appConfig.getLongBreakMinute();
        }
        inputHour.setText("0");
        inputMinute.setText(String.valueOf(minute));
        inputSecond.setText("0");
    }

    private void updateActiveModeButton() {
        btnPomodoroMode.getStyleClass().remove("btn-active");
        btnShortBreakMode.getStyleClass().remove("btn-active");
        btnLongBreakMode.getStyleClass().remove("btn-active");

        if (activeModeButton != null) activeModeButton.getStyleClass().add("btn-active");
    }

    private void updateTodayStats() {
        Statistic today = statisticService.getTodayStatistic();
        labelTodayStats.setText("Today: " + today.getSuccessfulSessions() + " success, " + today.getFailedSessions() + " failed");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
