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
            // Parse and execute each SQL statement
            executeSqlScript(stm, schema);
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize database, " + e);
        }
    }

    /**
     * Execute SQL script statements. Handles comments and multi-line statements.
     */
    private static void executeSqlScript(Statement stm, String script) throws SQLException {
        StringBuilder statement = new StringBuilder();
        boolean inLineComment = false;
        boolean inBlockComment = false;
        
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            char next = (i + 1 < script.length()) ? script.charAt(i + 1) : '\0';
            
            // Handle line comments
            if (!inBlockComment && c == '-' && next == '-') {
                inLineComment = true;
                i++; // Skip next dash
                continue;
            }
            
            // Handle block comments
            if (!inLineComment && c == '/' && next == '*') {
                inBlockComment = true;
                i++; // Skip asterisk
                continue;
            }
            
            if (inBlockComment && c == '*' && next == '/') {
                inBlockComment = false;
                i++; // Skip slash
                continue;
            }
            
            // Handle newlines in line comments
            if (inLineComment && (c == '\n' || c == '\r')) {
                inLineComment = false;
                continue;
            }
            
            // Skip characters in comments
            if (inLineComment || inBlockComment) {
                continue;
            }
            
            // Detect statement terminator
            if (c == ';') {
                String sql = statement.toString().trim();
                if (!sql.isEmpty()) {
                    stm.execute(sql);
                }
                statement = new StringBuilder();
            } else if (!Character.isWhitespace(c) || statement.length() > 0) {
                // Don't add leading whitespace
                if (!(Character.isWhitespace(c) && statement.length() == 0)) {
                    statement.append(c);
                }
            }
        }
        
        // Execute any remaining statement
        String sql = statement.toString().trim();
        if (!sql.isEmpty()) {
            stm.execute(sql);
        }
    }

    private static void createAppDir() {
        File dir = DatabaseManager.getDBDir();
        if (!dir.exists()) dir.mkdirs();
    }

}