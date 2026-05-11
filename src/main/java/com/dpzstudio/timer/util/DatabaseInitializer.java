package com.dpzstudio.timer.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String DB_SCHEMA = "/db/database.sql";

    private DatabaseInitializer() { }

    public static void init() {
        createAppDir();
        createDatabase();
    }

    private static void createDatabase() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stm = conn.createStatement()) {
            String schema = ResourceReader.readResource(DB_SCHEMA);
            stm.executeUpdate(schema);
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize database, " + e);
        }
    }

    private static void createAppDir() {
        File dir = DatabaseManager.getDBDir();
        if (!dir.exists()) dir.mkdirs();
    }

}
