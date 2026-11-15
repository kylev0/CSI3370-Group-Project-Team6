/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: PlayerController.java
 *
 * This class keeps track of what the player is doing: which song is
 * picked, if it's playing or paused, and how loud the volume is.
 * The Play, Pause, Next, Previous buttons and the volume slider call
 * these methods.
 *
 * It doesn't play real audio yet. Keeping this logic separate from the
 * screen means it can be unit tested now, and the audio part can be
 * plugged in here later.
 */
package musicplayer;

import java.io.File;

public class PlayerController {

    public static final double MIN_VOLUME = 0;
    public static final double MAX_VOLUME = 100;

    // the playlist this player is playing from
    private final Playlist playlist;

    // spot in the playlist of the current song
    private int currentIndex = 0;
    private boolean playing = false;

    // starts at 65 to match the volume slider on the screen
    private double volume = 65;

    public PlayerController(Playlist playlist) {
        if (playlist == null) {
            throw new IllegalArgumentException("Playlist can't be null.");
        }
        this.playlist = playlist;
    }

    // starts playing the current song.
    // gives back false if there's nothing in the playlist to play.
    public boolean play() {
        if (playlist.size() == 0) {
            playing = false;
            return false;
        }
        playing = true;
        return true;
    }

    // pauses the song but remembers which song it was on
    public void pause() {
        playing = false;
    }

    // moves to the next song. after the last song it goes back to the first one.
    public void next() {
        if (playlist.size() == 0) {
            return;
        }
        currentIndex = (currentIndex + 1) % playlist.size();
    }

    // moves to the previous song. before the first song it goes to the last one.
    public void previous() {
        if (playlist.size() == 0) {
            return;
        }
        // adding size() first keeps the number from going negative
        currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
    }

    // jumps to a song when the user clicks it in the list
    public void selectSong(int index) {
        if (index < 0 || index >= playlist.size()) {
            throw new IndexOutOfBoundsException("No song at index " + index);
        }
        currentIndex = index;
    }

    // goes back to the first song and stops. used when a new playlist is opened.
    public void reset() {
        currentIndex = 0;
        playing = false;
    }

    // sets the volume. anything below 0 becomes 0 and anything above 100 becomes 100.
    public void setVolume(double newVolume) {
        if (newVolume < MIN_VOLUME) {
            volume = MIN_VOLUME;
        } else if (newVolume > MAX_VOLUME) {
            volume = MAX_VOLUME;
        } else {
            volume = newVolume;
        }
    }

    public double getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return playing;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    // the current song file, or null if the playlist is empty
    public File getCurrentSong() {
        if (playlist.size() == 0) {
            return null;
        }
        // a song may have been removed, so make sure the spot still exists
        if (currentIndex >= playlist.size()) {
            currentIndex = playlist.size() - 1;
        }
        return playlist.getSongs().get(currentIndex);
    }
}
