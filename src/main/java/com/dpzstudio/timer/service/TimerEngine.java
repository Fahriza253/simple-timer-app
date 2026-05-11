package com.dpzstudio.timer.service;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TimerEngine {

    private Timeline timeline;
    private int totalSecond;
    private int remainingSecond;
    private Runnable onTick;
    private Runnable onFinish;

    public void setCallbacks(Runnable onTick, Runnable onFinish) {
        this.onTick     = onTick;
        this.onFinish   = onFinish;
    }

    public void setDuration(int total) {
        this.totalSecond        = total;
        this.remainingSecond    = total;
    }

    public void start() {
        if (remainingSecond <= 0) return;
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) return;
        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        timeline.play();
    }

    private void tick() {
        remainingSecond--;
        if (onTick != null) onTick.run();

        if (remainingSecond <= 0) {
            stop();
            if (onFinish != null) onFinish.run();
        }
    }

    public void pause() {
        if (timeline != null) timeline.pause();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    public void reset() {
        stop();
        remainingSecond = totalSecond;
    }

    public void hardReset() {
        stop();
        totalSecond     = 0;
        remainingSecond = 0;
    }

    public int getRemainingSecond() {
        return remainingSecond;
    }

    public int getTotalSecond() {
        return totalSecond;
    }

    public boolean isRunning() {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline != null && timeline.getStatus() == Animation.Status.PAUSED;
    }

}
