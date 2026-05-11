module com.dpzstudio.timer {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.dpzstudio.timer to javafx.fxml;
    opens com.dpzstudio.timer.controller to javafx.fxml;

    exports com.dpzstudio.timer;
    exports com.dpzstudio.timer.controller;
    exports com.dpzstudio.timer.model;
    exports com.dpzstudio.timer.service;
    exports com.dpzstudio.timer.util;
}
