package com.dpzstudio.timer.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_FOLDER = System.getProperty("user.home") + File.separator + ".dpzstudio.timer";
    private static final String DB_FILE = DB_FOLDER + File.separator + "pomodoro_app.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    private DatabaseManager() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static File getDBDir() {
        return new File(DB_FOLDER);
    }

    public static File getDBFile() {
        return new File(DB_FILE);
    }
}
