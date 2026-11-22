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
 * The actual sound comes from an AudioPlayer. The app gives it the real
 * one (FxAudioPlayer). The unit tests can leave it out, or give it a
 * fake one, so the logic can be tested without playing real music.
 */
package musicplayer;

import java.io.File;

public class PlayerController {

    public static final double MIN_VOLUME = 0;
    public static final double MAX_VOLUME = 100;

    // the playlist this player is playing from
    private final Playlist playlist;

    // what actually plays the sound (can be null, then it just tracks the state)
    private final AudioPlayer audio;

    // the song file that's loaded into the audio player right now
    private File loadedSong;

    // spot in the playlist of the current song
    private int currentIndex = 0;
    private boolean playing = false;

    // starts at 65 to match the volume slider on the screen
    private double volume = 65;

    // player with no sound, only keeps track of the state
    public PlayerController(Playlist playlist) {
        this(playlist, null);
    }

    // player that plays real sound through the given AudioPlayer
    public PlayerController(Playlist playlist, AudioPlayer audio) {
        if (playlist == null) {
            throw new IllegalArgumentException("Playlist can't be null.");
        }
        this.playlist = playlist;
        this.audio = audio;

        if (audio != null) {
            // the audio player uses 0.0 to 1.0, the slider uses 0 to 100
            audio.setVolume(volume / 100);
            // when a song ends, move on to the next one
            audio.setOnFinished(this::songFinished);
        }
    }

    // starts playing the current song.
    // gives back false if there's nothing in the playlist to play.
    // throws IllegalArgumentException if the song file can't be played.
    public boolean play() {
        if (playlist.size() == 0) {
            playing = false;
            return false;
        }

        if (audio != null) {
            File song = getCurrentSong();
            // only load the file if it's a different song,
            // otherwise just resume from where it was paused
            if (!song.equals(loadedSong)) {
                try {
                    audio.load(song);
                    loadedSong = song;
                } catch (IllegalArgumentException ex) {
                    loadedSong = null;
                    playing = false;
                    throw ex;
                }
            }
            audio.play();
        }
        playing = true;
        return true;
    }

    // pauses the song but remembers which song it was on
    public void pause() {
        playing = false;
        if (audio != null) {
            audio.pause();
        }
    }

    // stops the song completely (next play starts the song from the beginning)
    public void stop() {
        playing = false;
        loadedSong = null;
        if (audio != null) {
            audio.stop();
        }
    }

    // moves to the next song. after the last song it goes back to the first one.
    // if a song was playing, the new song starts playing too.
    public void next() {
        if (playlist.size() == 0) {
            return;
        }
        currentIndex = (currentIndex + 1) % playlist.size();
        if (playing) {
            play();
        }
    }

    // moves to the previous song. before the first song it goes to the last one.
    public void previous() {
        if (playlist.size() == 0) {
            return;
        }
        // adding size() first keeps the number from going negative
        currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        if (playing) {
            play();
        }
    }

    // jumps to a song when the user clicks it in the list
    public void selectSong(int index) {
        if (index < 0 || index >= playlist.size()) {
            throw new IndexOutOfBoundsException("No song at index " + index);
        }
        currentIndex = index;
        if (playing) {
            play();
        }
    }

    // call this right BEFORE a song is removed from the playlist.
    // it keeps the current song the same, and stops the music if the
    // song being removed is the one that's playing.
    public void songRemoved(int removedIndex) {
        if (removedIndex == currentIndex) {
            stop();
        } else if (removedIndex < currentIndex) {
            // everything after the removed song moves up one spot
            currentIndex--;
        }
    }

    // goes back to the first song and stops. used when a new playlist is opened.
    public void reset() {
        stop();
        currentIndex = 0;
    }

    // called by the AudioPlayer when a song plays all the way through
    void songFinished() {
        // forget the finished song so a 1-song playlist starts over from the top
        loadedSong = null;
        try {
            next();
        } catch (IllegalArgumentException ex) {
            // the next song can't be played, so just stop
            stop();
        }
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
        if (audio != null) {
            audio.setVolume(volume / 100);
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
