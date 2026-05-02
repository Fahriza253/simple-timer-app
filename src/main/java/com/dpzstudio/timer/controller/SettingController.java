package com.dpzstudio.timer.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.dpzstudio.timer.model.AppConfig;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.CheckBox;

public class SettingController implements Initializable {
    @FXML private CheckBox checkboxAutoStartPomo;
    @FXML private CheckBox checkboxAutoStartBreak;
    @FXML private Spinner<Integer> spinnerLongBreakInterval;
    @FXML private Spinner<Integer> spinnerDefaultPomodoro, spinnerDefaultShortBreak, spinnerDefaultLongBreak;

    private final AppConfig config = AppConfig.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkboxAutoStartPomo.setSelected(config.isAutoStartPomodoro());
        checkboxAutoStartBreak.setSelected(config.isAutoStartBreak());

        SpinnerValueFactory<Integer> spinnerFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, config.getLongBreakInterval());

        spinnerLongBreakInterval.setValueFactory(spinnerFactory);
        spinnerDefaultPomodoro.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, config.getPomodoroMinute())
        );
        spinnerDefaultShortBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, config.getShortBreakMinute())
        );
        spinnerDefaultLongBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, config.getLongBreakMinute())
        );

        spinnerDefaultPomodoro.valueProperty().addListener((obs, oldVal, newVal) ->
            config.setPomodoroMinute(newVal)
        );
        spinnerDefaultShortBreak.valueProperty().addListener((obs, oldVal, newVal) ->
            config.setShortBreakMinute(newVal)
        );
        spinnerDefaultLongBreak.valueProperty().addListener((obs, oldVal, newVal) ->
            config.setLongBreakMinute(newVal)
        );
        checkboxAutoStartPomo.selectedProperty().addListener((obs, oldVal, newVal) -> {
            config.setAutoStartPomodoro(newVal);
        });
        checkboxAutoStartBreak.selectedProperty().addListener((obs, oldVal, newval) -> {
            config.setAutoStartBreak(newval);
        });
        spinnerLongBreakInterval.valueProperty().addListener((obs, oldVal, newVal) -> {
            config.setLongBreakInterval(newVal);
        });
    }


}
