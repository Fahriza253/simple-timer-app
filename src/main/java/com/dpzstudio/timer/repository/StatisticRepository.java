package com.dpzstudio.timer.repository;

import com.dpzstudio.timer.model.Statistic;
import com.dpzstudio.timer.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatisticRepository {

    public void saveSession(boolean success) {
        String sql = "INSERT INTO session_history (status, session_date, completed_at) VALUES (?, ?, ?)";
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowDateTime = LocalDateTime.now();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, success ? 1 : 0);
            stmt.setDate(2, Date.valueOf(nowDate));
            stmt.setTimestamp(3, Timestamp.valueOf(nowDateTime));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save session", e);
        }
    }

    public Statistic getStatisticForDate(LocalDate date) {
        String sql = "SELECT COUNT(*) as total, SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as failed FROM session_history WHERE session_date = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int failed = rs.getInt("failed");
                return new Statistic(date, total, failed);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get statistic", e);
        }
        return new Statistic(date, 0, 0);
    }

    public List<Statistic> getStatisticsForPeriod(LocalDate start, LocalDate end) {
        List<Statistic> stats = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            stats.add(getStatisticForDate(current));
            current = current.plusDays(1);
        }
        return stats;
    }

}
