package com.dpzstudio.timer.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.util.DatabaseManager;

public class AppConfigRepository {

    public AppConfig find() throws SQLException {
        String sql = "SELECT * FROM app_config WHERE id = 1";

        try (Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (!rs.next()) return null;

            AppConfig cfg = new AppConfig();

            cfg.setPomodoroMinute(rs.getInt("pomodoro_minute"));
            cfg.setShortBreakMinute(rs.getInt("short_break_minute"));
            cfg.setLongBreakMinute(rs.getInt("long_break_minute"));

            cfg.setAutoStartPomodoro(rs.getBoolean("auto_start_pomodoro"));
            cfg.setAutoStartBreak(rs.getBoolean("auto_start_break"));

            cfg.setLongBreakInterval(rs.getInt("long_break_interval"));

            return cfg;
        }
    }

    public void update(AppConfig config) throws SQLException {
        String sql = "UPDATE app_config SET pomodoro_minute = ?, short_break_minute = ?, long_break_minute = ?, auto_start_pomodoro = ?, auto_start_break = ?, long_break_interval = ? WHERE id = 1";

        try (
            Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, config.getPomodoroMinute());
            ps.setInt(2, config.getShortBreakMinute());
            ps.setInt(3, config.getLongBreakMinute());

            ps.setBoolean(4, config.isAutoStartPomodoro());
            ps.setBoolean(5, config.isAutoStartBreak());

            ps.setInt(6, config.getLongBreakInterval());

            ps.executeUpdate();
        }
    }

}
