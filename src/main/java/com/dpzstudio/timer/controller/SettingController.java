package com.dpzstudio.timer.controller;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.service.AppConfigService;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class SettingController implements Initializable {
    @FXML private CheckBox checkboxAutoStartPomo;
    @FXML private CheckBox checkboxAutoStartBreak;
    @FXML private Spinner<Integer> spinnerLongBreakInterval;
    @FXML private Spinner<Integer> spinnerDefaultPomodoro, spinnerDefaultShortBreak, spinnerDefaultLongBreak;
    @FXML private Button btnSaveSetting, btnCancel;

    private final AppConfigService cfgService = new AppConfigService();

    private AppConfig loadedCfg;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadedCfg = cfgService.loadCfg();
        initializeSpinner();
        bindData();
        btnSaveSetting.setOnAction(e -> saveSetting());
        btnCancel.setOnAction(e -> cancelSetting());
    }

    private void initializeSpinner() {

        spinnerLongBreakInterval.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                10,
                loadedCfg.getLongBreakInterval()
            )
        );

        spinnerDefaultPomodoro.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedCfg.getPomodoroMinute()
            )
        );

        spinnerDefaultShortBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedCfg.getShortBreakMinute()
            )
        );

        spinnerDefaultLongBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedCfg.getLongBreakMinute()
            )
        );
    }

    private void bindData() {
        checkboxAutoStartPomo.setSelected(loadedCfg.isAutoStartPomodoro());
        checkboxAutoStartBreak.setSelected(loadedCfg.isAutoStartBreak());
    }

    private void saveSetting() {

        try {

            AppConfig newConfig = new AppConfig();

            newConfig.setPomodoroMinute(
                spinnerDefaultPomodoro.getValue()
            );

            newConfig.setShortBreakMinute(
                spinnerDefaultShortBreak.getValue()
            );

            newConfig.setLongBreakMinute(
                spinnerDefaultLongBreak.getValue()
            );

            newConfig.setLongBreakInterval(
                spinnerLongBreakInterval.getValue()
            );

            newConfig.setAutoStartPomodoro(
                checkboxAutoStartPomo.isSelected()
            );

            newConfig.setAutoStartBreak(
                checkboxAutoStartBreak.isSelected()
            );

            cfgService.saveCfg(newConfig);

            closeWin();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText("Save Failed");

            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }
    }

    private void cancelSetting() {
        if (!hasChanges())
            closeWin();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Discard changes?");
        alert.setContentText("Unsaved changes will be lost");
        Optional<ButtonType> res = alert.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) closeWin();
    }

    private boolean hasChanges() {
        return
        !Objects.equals(
            spinnerDefaultPomodoro.getValue(),
            loadedCfg.getPomodoroMinute()
        )
        ||
        !Objects.equals(
            spinnerDefaultShortBreak.getValue(),
            loadedCfg.getShortBreakMinute()
        )
        ||
        !Objects.equals(
            spinnerDefaultLongBreak.getValue(),
            loadedCfg.getLongBreakMinute()
        )
        ||
        !Objects.equals(
            spinnerLongBreakInterval.getValue(),
            loadedCfg.getLongBreakInterval()
        )
        ||
        checkboxAutoStartPomo.isSelected()
            != loadedCfg.isAutoStartPomodoro()
        ||
        checkboxAutoStartBreak.isSelected()
            != loadedCfg.isAutoStartBreak();
    }

    private void closeWin() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
