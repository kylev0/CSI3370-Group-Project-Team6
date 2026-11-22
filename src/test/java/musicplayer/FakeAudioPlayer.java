/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: FakeAudioPlayer.java
 *
 * A fake (stub) audio player that's only used by the tests.
 * It doesn't make any sound. It just writes down what PlayerController
 * asked it to do, so the tests can check that the right things happened.
 */
package musicplayer;

import java.io.File;

public class FakeAudioPlayer implements AudioPlayer {

    // what the fake remembers so the tests can check it
    File loadedSong;
    int loadCount = 0;
    boolean playing = false;
    int stopCount = 0;
    double volume = -1;
    Runnable onFinished;

    // if set, load() acts like the file can't be played
    boolean failOnLoad = false;

    @Override
    public void load(File song) {
        if (failOnLoad) {
            throw new IllegalArgumentException("Can't play " + song.getName());
        }
        loadedSong = song;
        loadCount++;
    }

    @Override
    public void play() {
        playing = true;
    }

    @Override
    public void pause() {
        playing = false;
    }

    @Override
    public void stop() {
        playing = false;
        stopCount++;
    }

    @Override
    public void setVolume(double newVolume) {
        volume = newVolume;
    }

    @Override
    public void seek(double seconds) {
    }

    @Override
    public double getCurrentSeconds() {
        return 0;
    }

    @Override
    public double getTotalSeconds() {
        return 0;
    }

    @Override
    public void setOnFinished(Runnable action) {
        onFinished = action;
    }

    // pretends the current song just played all the way to the end
    void finishSong() {
        onFinished.run();
    }
}
