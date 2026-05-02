package com.dpzstudio.timer.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.io.IOException;
import com.dpzstudio.timer.model.LongBreak;
import com.dpzstudio.timer.model.Pomodoro;
import com.dpzstudio.timer.model.PomodoroMode;
import com.dpzstudio.timer.model.ShortBreak;
import com.dpzstudio.timer.service.AudioPlayer;
import com.dpzstudio.timer.service.TimerEngine;
import com.dpzstudio.timer.util.TimeFormatter;
import com.dpzstudio.timer.service.PomodoroService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    @FXML private TextField inputSecond, inputMinute, inputHour;
    @FXML private Label labelSecond, labelMinute, labelHour;
    @FXML private Text labelProgressCounter;
    @FXML private Button btnStart, btnPause, btnReset;
    @FXML private Button btnPomodoroMode, btnShortBreakMode, btnLongBreakMode;
    @FXML private Button btnSetting;
    @FXML private ProgressBar progressBar;

    private Button activeModeButton;

    private final TimerEngine engine = new TimerEngine();
    private AudioPlayer audioPlayer;

    private PomodoroMode currentMode;
    private final Pomodoro pomodoro     = new Pomodoro();
    private final ShortBreak shortBreak = new ShortBreak();
    private final LongBreak longBreak   = new LongBreak();
    private final PomodoroService progressTracker = new PomodoroService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureNumericTextField();
        initService();

        applyMode(pomodoro);

        btnSetting.setOnAction(e -> openSettingMenu());
        btnStart.setOnAction(e -> startTimer());
        btnPause.setOnAction(e -> pauseTimer());
        btnReset.setOnAction(e -> resetTimer());

        btnPomodoroMode.setOnAction(e -> switchMode(pomodoro));
        btnShortBreakMode.setOnAction(e -> switchMode(shortBreak));
        btnLongBreakMode.setOnAction(e -> switchMode(longBreak));
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
        if (!engine.isRunning()) {
            int total   = calculateTotalSecondFromInput();

            if (total < 1) {
                showWarning("Duration Incorrect!");
                return;
            }

            if (engine.getRemainingSecond() == 0 || engine.getRemainingSecond() == engine.getTotalSecond()) {
                engine.setDuration(total);
            }
        }

        engine.start();
        setButtonState(false, true, true);
    }

    private void pauseTimer() {
        engine.pause();
        setButtonState(true, false, false);
    }

    private void resetTimer() {
        engine.hardReset();
        clearTimeDisplay();
        updateDisplay();
        setButtonState(true, false, false);
    }

    private void handleTimerFinished() {
        audioPlayer.play();

        PomodoroMode next = progressTracker.determineNextMode(currentMode);
        labelProgressCounter.setText("Completed Pomodoros: " + progressTracker.getSessionCount());

        applyMode(next);

        if (progressTracker.shouldAutoStart(next)) {
            startTimer();
        } else {
            updateDisplay();
            setButtonState(true, false, true);
        }
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
                getClass().getResource("/views/Setting.fxml")
            );
            Parent root = load.load();

            Scene settingScene = new Scene(root);
            settingScene.getStylesheets().add(getClass().getResource("/styles/themes.css").toExternalForm());
            settingScene.getStylesheets().add(getClass().getResource("/styles/setting.css").toExternalForm());

            Stage settingStage = new Stage();

            settingStage.setTitle("Settings");
            settingStage.setScene(settingScene);
            settingStage.initModality(Modality.APPLICATION_MODAL);
            settingStage.initOwner(btnSetting.getScene().getWindow());
            settingStage.setResizable(false);

            settingStage.showAndWait();
            if (!engine.isRunning() && currentMode != null) applyMode(currentMode);
        } catch (IOException e) {
            e.printStackTrace();
            showWarning("Failed to load Setting menu");
        }
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
        if (engine.isRunning()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Switch Mode");
            alert.setHeaderText("Timer is currently running.");
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
        inputHour.setText(String.valueOf(currentMode.getDefaultHours()));
        inputMinute.setText(String.valueOf(currentMode.getDefaultMinutes()));
        inputSecond.setText(String.valueOf(currentMode.getDefaultSeconds()));
    }

    private void updateActiveModeButton() {
        btnPomodoroMode.getStyleClass().remove("btn-active");
        btnShortBreakMode.getStyleClass().remove("btn-active");
        btnLongBreakMode.getStyleClass().remove("btn-active");

        if (activeModeButton != null) activeModeButton.getStyleClass().add("btn-active");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
