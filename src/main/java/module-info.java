module com.dpzstudio.timer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.graphics;
    requires javafx.media;

    opens com.dpzstudio.timer to javafx.fxml;
    opens com.dpzstudio.timer.controller to javafx.fxml;
    
    exports com.dpzstudio.timer;
    exports com.dpzstudio.timer.controller;
}
