package com.dpzstudio.timer.service;

import java.time.LocalDate;
import java.util.List;
import com.dpzstudio.timer.model.Statistic;
import com.dpzstudio.timer.model.PomodoroMode;
import com.dpzstudio.timer.model.Pomodoro;
import com.dpzstudio.timer.model.SessionModeType;
import com.dpzstudio.timer.repository.StatisticRepository;

/**
 * Service untuk manage statistics dari timer sessions.
 *
 * Core Responsibility:
 * - ENFORCE business rule: Only POMODORO mode sessions are recorded
 * - PROVIDE statistics queries untuk UI/reports
 * - ACT as bridge antara Controllers dan Repository
 *
 * Design Principle:
 * - Business Logic (filtering by mode) belongs di Service layer
 * - Data Access (queries) belongs di Repository layer
 * - Controllers should NOT decide yang sessions untuk record
 *
 * Important:
 * - Break sessions (SHORT_BREAK, LONG_BREAK) TIDAK di-record
 * - Ini enforced di recordSession() method
 * - Repository menerima semua mode types untuk data integrity & analytics
 */

public class StatisticService {

    private final StatisticRepository repository = new StatisticRepository();

    /**
     * Records a session completion, tapi HANYA untuk POMODORO mode.
     *
     * Business Logic:
     * - Detects session mode
     * - Only records jika POMODORO
     * - Break sessions silently ignored (per requirement)
     *
     * @param mode    PomodoroMode instance (Pomodoro, ShortBreak, atau LongBreak)
     * @param success true jika session completed, false jika interrupted/failed
     *
     * Example Usage:
     * <pre>
     * {@code
     * PomodoroMode currentMode = timerManager.getCurrentMode();
     * statisticService.recordSession(currentMode, true);
     * // If Pomodoro: saves ke database
     * // If ShortBreak/LongBreak: tidak saves (silent return)
     * }
     * </pre>
     *
     * @throws IllegalArgumentException jika mode adalah null
     */
    public void recordSession(PomodoroMode mode, boolean success) {
        if (mode == null)
            throw new IllegalArgumentException("PomodoroMode tidak boleh null");

        // ENFORCE: Only Pomodoro sessions are recorded
        if (!isPomodoro(mode))
            // Silent return: Break sessions tidak di-record
            // This adalah intentional - per business requirement
            return;


        // Convert mode interface ke enum
        SessionModeType modeType = SessionModeType.fromPomodoroMode(mode);

        // Save ke repository
        repository.saveSession(modeType, success);
    }

    /**
     * Records a successful POMODORO session.
     *
     * Convenience method - assumes Pomodoro mode.
     * USE: Hanya jika context sudah guaranteed Pomodoro
     * BETTER: Use recordSession(PomodoroMode mode, boolean success)
     *
     * @deprecated Use {@link #recordSession(PomodoroMode, boolean)} instead
     *             untuk explicit mode passing dan better clarity
     */
    @Deprecated(since = "1.1", forRemoval = false)
    public void recordSuccessfulSession() {
        // Default assumption: Pomodoro mode
        // WARNING: This assumes caller guarantees Pomodoro context
        repository.saveSession(SessionModeType.POMODORO, true);
    }

    /**
     * Records a failed POMODORO session.
     *
     * Convenience method - assumes Pomodoro mode.
     * USE: Hanya jika context sudah guaranteed Pomodoro
     * BETTER: Use recordSession(PomodoroMode mode, boolean success)
     *
     * @deprecated Use {@link #recordSession(PomodoroMode, boolean)} instead
     *             untuk explicit mode passing dan better clarity
     */
    @Deprecated(since = "1.1", forRemoval = false)
    public void recordFailedSession() {
        // Default assumption: Pomodoro mode
        // WARNING: This assumes caller guarantees Pomodoro context
        repository.saveSession(SessionModeType.POMODORO, false);
    }

    /**
     * Gets today's statistics.
     *
     * Returns:
     * - Total Pomodoro sessions completed today
     * - Failed Pomodoro sessions
     * - (Break sessions NOT included)
     *
     * @return Statistic object untuk today
     */
    public Statistic getTodayStatistic() {
        return repository.getStatisticForDate(LocalDate.now());
    }

    /**
     * Gets statistics untuk specific date.
     *
     * Returns:
     * - Total Pomodoro sessions completed on date
     * - Failed Pomodoro sessions
     * - (Break sessions NOT included)
     *
     * @param date LocalDate untuk retrieve
     * @return Statistic object untuk date
     * @throws IllegalArgumentException jika date adalah null
     */
    public Statistic getStatisticForDate(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date tidak boleh null");

        return repository.getStatisticForDate(date);
    }

    /**
     * Gets statistics untuk last N days.
     *
     * Returns:
     * - Statistics untuk setiap day dalam range
     * - Only Pomodoro sessions included
     * - Break sessions NOT included
     *
     * @param days Number of days (including today)
     * @return List<Statistic> dalam chronological order
     * @throws IllegalArgumentException jika days <= 0
     */
    public List<Statistic> getStatisticsForLastDays(int days) {
        if (days <= 0)
            throw new IllegalArgumentException("Days harus positive number");

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1);
        return repository.getStatisticsForPeriod(start, end);
    }

    /**
     * Gets statistics untuk custom period.
     *
     * @param start LocalDate untuk start (inclusive)
     * @param end   LocalDate untuk end (inclusive)
     * @return List<Statistic> untuk period
     * @throws IllegalArgumentException jika dates invalid
     */
    public List<Statistic> getStatisticsForPeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            throw new IllegalArgumentException("Start dan End dates tidak boleh null");

        if (start.isAfter(end))
            throw new IllegalArgumentException("Start date tidak boleh after end date");

        return repository.getStatisticsForPeriod(start, end);
    }

    /**
     * Helper method: Checks apakah mode adalah Pomodoro.
     *
     * Used internally untuk enforce business rule.
     *
     * @param mode PomodoroMode instance
     * @return true jika mode adalah Pomodoro, false otherwise
     */
    private boolean isPomodoro(PomodoroMode mode) {
        return mode instanceof Pomodoro;
    }

    /**
     * Helper method: Gets total Pomodoro sessions dalam period.
     *
     * Utility untuk analytics/reporting.
     *
     * @param start LocalDate untuk start
     * @param end   LocalDate untuk end
     * @return Total Pomodoro sessions dalam period
     */
    public int getTotalSessionsInPeriod(LocalDate start, LocalDate end) {
        List<Statistic> stats = getStatisticsForPeriod(start, end);
        return stats.stream().mapToInt(Statistic::getTotalSessions).sum();
    }

    /**
     * Helper method: Gets total SUCCESSFUL Pomodoro sessions dalam period.
     *
     * @param start LocalDate untuk start
     * @param end   LocalDate untuk end
     * @return Total successful Pomodoro sessions
     */
    public int getSuccessfulSessionsInPeriod(LocalDate start, LocalDate end) {
        List<Statistic> stats = getStatisticsForPeriod(start, end);
        return stats.stream().mapToInt(Statistic::getSuccessfulSessions).sum();
    }
}
