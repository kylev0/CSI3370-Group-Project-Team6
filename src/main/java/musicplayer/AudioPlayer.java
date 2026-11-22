/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: AudioPlayer.java
 *
 * This interface lists what the music player needs from whatever
 * actually makes the sound. FxAudioPlayer is the real one (it uses
 * JavaFX's MediaPlayer). The unit tests use a fake one instead
 * (FakeAudioPlayer) so they can check PlayerController without
 * playing any real audio.
 */
package musicplayer;

import java.io.File;

public interface AudioPlayer {

    // gets a song file ready to play.
    // throws IllegalArgumentException if the file can't be played.
    void load(File song);

    // starts or resumes the loaded song
    void play();

    // pauses the song where it is
    void pause();

    // stops the song and goes back to the start
    void stop();

    // sets how loud it is, from 0.0 (silent) to 1.0 (full volume)
    void setVolume(double volume);

    // jumps to a spot in the song
    void seek(double seconds);

    // how far into the song it is right now
    double getCurrentSeconds();

    // how long the whole song is (0 if it isn't known yet)
    double getTotalSeconds();

    // code to run when a song plays all the way to the end
    void setOnFinished(Runnable action);
}
