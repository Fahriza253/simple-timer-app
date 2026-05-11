package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.Statistic;
import com.dpzstudio.timer.repository.StatisticRepository;

import java.time.LocalDate;
import java.util.List;

public class StatisticService {

    private final StatisticRepository repository = new StatisticRepository();

    public void recordSuccessfulSession() {
        repository.saveSession(true);
    }

    public void recordFailedSession() {
        repository.saveSession(false);
    }

    public Statistic getTodayStatistic() {
        return repository.getStatisticForDate(LocalDate.now());
    }

    public Statistic getStatisticForDate(LocalDate date) {
        return repository.getStatisticForDate(date);
    }

    public List<Statistic> getStatisticsForLastDays(int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1);
        return repository.getStatisticsForPeriod(start, end);
    }

}
