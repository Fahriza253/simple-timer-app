package com.dpzstudio.timer.repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.dpzstudio.timer.model.Statistic;
import com.dpzstudio.timer.model.SessionModeType;
import com.dpzstudio.timer.util.DatabaseManager;

/**
 * Repository untuk akses data session history dan statistics.
 *
 * Responsibilities:
 * - Save session data ke database dengan mode tracking
 * - Retrieve statistics dari database
 * - Filter queries untuk ONLY include POMODORO sessions
 *
 * Design Pattern: Repository Pattern (DAO)
 * - Decouples business logic dari data access
 * - Provides clean API untuk session persistence
 * - Automatic filtering ensures data consistency
 *
 * Database Constraint:
 * - session_history.mode_type ONLY accepts: 'POMODORO', 'SHORT_BREAK', 'LONG_BREAK'
 * - All statistic queries FILTER BY mode_type = 'POMODORO' automatically
 */
public class StatisticRepository {

    /**
     * Saves a session record ke database.
     *
     * Parameters:
     * @param modeType     Type of session (POMODORO, SHORT_BREAK, LONG_BREAK)
     * @param success      true if session completed successfully, false if interrupted/failed
     *
     * Important:
     * - Repository responsibility: Save data dengan proper mode tracking
     * - Service layer responsibility: Enforce yang hanya POMODORO di-record
     * - This method saves ALL mode types, filtering happens at Service/Query level
     *
     * @throws RuntimeException jika database error terjadi
     */
    public void saveSession(SessionModeType modeType, boolean success) {
        if (modeType == null)
            throw new IllegalArgumentException("SessionModeType tidak boleh null");

        String sql = "INSERT INTO session_history (status, session_date, completed_at, mode_type) " +
                     "VALUES (?, ?, ?, ?)";
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowDateTime = LocalDateTime.now();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, success ? 1 : 0);
            stmt.setDate(2, Date.valueOf(nowDate));
            stmt.setTimestamp(3, Timestamp.valueOf(nowDateTime));
            stmt.setString(4, modeType.getDbValue());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save session: " + e.getMessage(), e);
        }
    }

    /**
     * Gets statistics untuk specific date.
     *
     * IMPORTANT:
     * - Query automatically filters WHERE mode_type = 'POMODORO'
     * - Ini ensures consistency dan memenuhi business requirement
     * - Semua queries ke statistics ONLY include Pomodoro sessions
     *
     * @param date LocalDate untuk retrieve statistics
     * @return Statistic object dengan total dan failed sessions (Pomodoro only)
     */
    public Statistic getStatisticForDate(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date tidak boleh null");

        String sql = "SELECT COUNT(*) as total, " +
                     "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as failed " +
                     "FROM session_history " +
                     "WHERE session_date = ? AND mode_type = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setString(2, SessionModeType.POMODORO.getDbValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int failed = rs.getInt("failed");
                return new Statistic(date, total, failed);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get statistic for date " + date + ": " + e.getMessage(), e);
        }

        // Return empty statistic untuk date dengan no sessions
        return new Statistic(date, 0, 0);
    }

    /**
     * Gets statistics untuk range of dates.
     *
     * IMPORTANT:
     * - Fetches statistics untuk EVERY date dalam range (inclusive)
     * - ONLY includes Pomodoro sessions
     * - Returns Statistic object untuk setiap date (0 sessions jika no records)
     *
     * Performance Consideration:
     * - Loop-based approach (N queries untuk N days)
     * - Alternative: Single query dengan date range + group by
     * - Current approach: Simpler, provides 0-session data, O(N) complexity acceptable untuk reasonable date ranges
     *
     * @param start LocalDate untuk start of range (inclusive)
     * @param end   LocalDate untuk end of range (inclusive)
     * @return List<Statistic> untuk each date dalam range (ordered by date)
     * @throws IllegalArgumentException jika dates invalid
     */
    public List<Statistic> getStatisticsForPeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            throw new IllegalArgumentException("Start dan End dates tidak boleh null");

        if (start.isAfter(end))
            throw new IllegalArgumentException("Start date tidak boleh after end date");

        List<Statistic> stats = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            stats.add(getStatisticForDate(current));
            current = current.plusDays(1);
        }

        return stats;
    }

    /**
     * Gets session count BY mode type untuk analysis purposes.
     * Utility method untuk understand session distribution.
     *
     * @param date     LocalDate untuk query
     * @param modeType SessionModeType untuk filter
     * @return Number of sessions untuk given mode pada date
     */
    public int getSessionCountByMode(LocalDate date, SessionModeType modeType) {
        if (date == null || modeType == null)
            throw new IllegalArgumentException("Date dan ModeType tidak boleh null");

        String sql = "SELECT COUNT(*) as count FROM session_history " +
                     "WHERE session_date = ? AND mode_type = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setString(2, modeType.getDbValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("count");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get session count: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Gets success rate untuk POMODORO sessions pada specific date.
     * Helper method untuk statistics calculation.
     *
     * @param date LocalDate untuk query
     * @return Double representing success rate (0.0 - 1.0), or 0 if no sessions
     */
    public double getSuccessRateForDate(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date tidak boleh null");


        Statistic stat = getStatisticForDate(date);
        if (stat.getTotalSessions() == 0)
            return 0.0;

        return (double) stat.getSuccessfulSessions() / stat.getTotalSessions();
    }
}
