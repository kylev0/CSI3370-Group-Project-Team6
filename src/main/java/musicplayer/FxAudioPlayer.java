/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: FxAudioPlayer.java
 *
 * The real audio player. It uses JavaFX's Media and MediaPlayer
 * classes to actually play the song files.
 *
 * Note: JavaFX can play .mp3 and .wav files, but not .ogg files.
 * If someone tries to play a file JavaFX can't handle, load()
 * throws an error so the screen can show a message.
 */
package musicplayer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class FxAudioPlayer implements AudioPlayer {

    // the JavaFX player for the song that's loaded (null if nothing is loaded)
    private MediaPlayer mediaPlayer;

    // remembered so each new song starts at the same volume
    private double volume = 0.65;

    private Runnable onFinished;

    @Override
    public void load(File song) {
        // get rid of the old song first so they don't play on top of each other
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        try {
            // JavaFX wants the file as a URI (like file:/Users/...)
            Media media = new Media(song.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        } catch (MediaException ex) {
            throw new IllegalArgumentException("Can't play " + song.getName()
                    + " (JavaFX only supports .mp3 and .wav files)");
        }

        mediaPlayer.setVolume(volume);

        // when the song ends, let PlayerController know so it can go to the next one
        mediaPlayer.setOnEndOfMedia(() -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
    }

    @Override
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void setVolume(double newVolume) {
        volume = newVolume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(newVolume);
        }
    }

    @Override
    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    @Override
    public double getCurrentSeconds() {
        if (mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getCurrentTime().toSeconds();
    }

    @Override
    public double getTotalSeconds() {
        if (mediaPlayer == null) {
            return 0;
        }
        // the length isn't known until JavaFX has read the start of the file
        Duration total = mediaPlayer.getTotalDuration();
        if (total == null || total.isUnknown() || total.isIndefinite()) {
            return 0;
        }
        return total.toSeconds();
    }

    @Override
    public void setOnFinished(Runnable action) {
        onFinished = action;
    }
}
