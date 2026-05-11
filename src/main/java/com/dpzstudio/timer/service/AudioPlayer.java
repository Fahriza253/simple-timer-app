package com.dpzstudio.timer.service;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class AudioPlayer {

    private AudioClip audio;

    public AudioPlayer(String dir) {
        URL res = getClass().getResource(dir);
        if (res != null) audio = new AudioClip(res.toExternalForm());
    }

    public void play() {
        if (audio != null) audio.play();
    }

    public boolean isLoaded() {
        return audio != null;
    }

}
