package com.dpzstudio.timer.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class HomeController implements Initializable {

    @FXML
    private TextField inputSecond, inputMinute, inputHour;

    @FXML
    private Label labelSecond, labelMinute, labelHour;

    @FXML
    private Button btnStart, btnPause, btnReset;

    @FXML
    private ProgressBar progressBar;

    private Timeline timer;
    private int totalSeconds;
    private int remainingSeconds;

    private AudioClip alarmSound;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configureNumericTextField(inputSecond, 2);
        configureNumericTextField(inputMinute, 2);
        configureNumericTextField(inputHour, 2);

        initResources();

        btnStart.setOnAction(e -> startTimer());
        btnPause.setOnAction(e -> pauseTimer());
        btnReset.setOnAction(e -> resetTimer());

        resetTimer();
    }

    private void configureNumericTextField(TextField field, int maxLength) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0," + maxLength + "}")) {
                return change;
            }
            return null;
        }));
    }

    private void startTimer() {
        if (!validateInput()) {
            showWarning("Masukkan waktu minimal 1 detik dan hanya angka saja.");
            return;
        }

        if (timer != null && timer.getStatus() == Animation.Status.RUNNING) {
            return;
        }

        if (timer == null) {
            timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                remainingSeconds--;
                updateDisplay();

                if (remainingSeconds <= 0) {
                    timer.stop();
                    remainingSeconds = 0;
                    updateDisplay();
                    setButtonState(true, false, true);
                    if (alarmSound != null) {
                        alarmSound.play();
                    }
                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
        }

        setButtonState(false, true, true);
        timer.play();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.pause();
            setButtonState(true, false, true);
        }
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }

        totalSeconds = 0;
        remainingSeconds = 0;
        inputSecond.clear();
        inputMinute.clear();
        inputHour.clear();
        labelSecond.setText("00");
        labelMinute.setText("00");
        labelHour.setText("00");
        progressBar.setProgress(0);
        setButtonState(true, false, false);
    }

    private boolean validateInput() {
        int seconds = parseOrZero(inputSecond.getText());
        int minutes = parseOrZero(inputMinute.getText());
        int hours = parseOrZero(inputHour.getText());

        totalSeconds = hours * 3600 + minutes * 60 + seconds;
        remainingSeconds = totalSeconds;
        return totalSeconds >= 1;
    }

    private int parseOrZero(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    private void updateDisplay() {
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        labelHour.setText(String.format("%02d", hours));
        labelMinute.setText(String.format("%02d", minutes));
        labelSecond.setText(String.format("%02d", seconds));

        updateProgress();
    }

    private void updateProgress() {
        if (totalSeconds <= 0) {
            progressBar.setProgress(0);
            return;
        }
        double progress = (double) (totalSeconds - remainingSeconds) / totalSeconds;
        progressBar.setProgress(progress);
    }

    private void setButtonState(boolean startEnabled, boolean pauseEnabled, boolean resetEnabled) {
        btnStart.setDisable(!startEnabled);
        btnPause.setDisable(!pauseEnabled);
        btnReset.setDisable(!resetEnabled);
    }

    private void initResources() {
        URL getAlarmSound = getClass().getResource("/sounds/ding.wav");

        if (getAlarmSound != null) {
            alarmSound = new AudioClip(getAlarmSound.toExternalForm());
        } else {
            showWarning("Audio not found!");
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
