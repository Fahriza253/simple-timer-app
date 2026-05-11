package com.dpzstudio.timer.model;

import java.time.LocalDate;

public class Statistic {

    private LocalDate date;
    private int totalSessions;
    private int failedSessions;

    public Statistic(LocalDate date, int totalSessions, int failedSessions) {
        this.date = date;
        this.totalSessions = totalSessions;
        this.failedSessions = failedSessions;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getFailedSessions() {
        return failedSessions;
    }

    public void setFailedSessions(int failedSessions) {
        this.failedSessions = failedSessions;
    }

    public int getSuccessfulSessions() {
        return totalSessions - failedSessions;
    }

}
