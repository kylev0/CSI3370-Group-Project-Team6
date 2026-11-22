/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: PlayerControllerAudioTest.java
 *
 * Unit tests for how PlayerController controls the audio.
 * Instead of real sound, the player is given a FakeAudioPlayer (a stub)
 * that just records what it was told to do.
 * Each test builds a playlist of 3 fake songs in a temporary folder.
 */
package musicplayer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PlayerControllerAudioTest {

    @TempDir
    Path tempDir;

    private Playlist playlist;
    private FakeAudioPlayer audio;
    private PlayerController player;

    // the volume is a double, so we allow a tiny margin
    private static final double DELTA = 0.0001;

    // runs before each test: playlist with song0, song1, song2 and a fake audio player
    @BeforeEach
    void setUp() throws IOException {
        Files.createFile(tempDir.resolve("song0.mp3"));
        Files.createFile(tempDir.resolve("song1.mp3"));
        Files.createFile(tempDir.resolve("song2.mp3"));

        playlist = new Playlist();
        playlist.loadFromFolder(tempDir.toFile());
        audio = new FakeAudioPlayer();
        player = new PlayerController(playlist, audio);
    }

    @Test
    @DisplayName("PA-01 play loads the current song and starts the audio")
    void playLoadsAndStartsSong() {
        assertTrue(player.play());

        assertEquals("song0.mp3", audio.loadedSong.getName());
        assertTrue(audio.playing);
        assertTrue(player.isPlaying());
    }

    @Test
    @DisplayName("PA-02 pause, then play again resumes without reloading the song")
    void pauseThenResume() {
        player.play();
        player.pause();
        assertFalse(audio.playing);

        player.play();

        assertTrue(audio.playing);
        assertEquals(1, audio.loadCount, "same song shouldn't be loaded twice");
    }

    @Test
    @DisplayName("PA-03 next while playing loads and plays the next song")
    void nextWhilePlaying() {
        player.play();

        player.next();

        assertEquals("song1.mp3", audio.loadedSong.getName());
        assertTrue(audio.playing);
    }

    @Test
    @DisplayName("PA-04 next while paused only moves to the next song, no audio")
    void nextWhilePaused() {
        player.next();

        assertEquals(1, player.getCurrentIndex());
        assertEquals(0, audio.loadCount);
        assertFalse(audio.playing);
    }

    @Test
    @DisplayName("PA-05 volume is sent to the audio as 0.0 to 1.0")
    void volumeSentToAudio() {
        // starts at 65 on the slider
        assertEquals(0.65, audio.volume, DELTA);

        player.setVolume(30);
        assertEquals(0.30, audio.volume, DELTA);

        player.setVolume(150);
        assertEquals(1.0, audio.volume, DELTA);
    }

    @Test
    @DisplayName("PA-06 when a song ends, the next song plays on its own")
    void songEndPlaysNext() {
        player.play();

        audio.finishSong();

        assertEquals(1, player.getCurrentIndex());
        assertEquals("song1.mp3", audio.loadedSong.getName());
        assertTrue(audio.playing);
    }

    @Test
    @DisplayName("PA-07 a song that can't be played throws an error and stops playing")
    void unplayableSong() {
        audio.failOnLoad = true;

        assertThrows(IllegalArgumentException.class, () -> player.play());
        assertFalse(player.isPlaying());
    }

    @Test
    @DisplayName("PA-08 removing a song before the current one keeps the same song selected")
    void removeEarlierSong() throws IOException {
        player.selectSong(2);

        player.songRemoved(0);
        playlist.removeSong(0);

        assertEquals("song2.mp3", player.getCurrentSong().getName());
    }

    @Test
    @DisplayName("PA-09 removing the song that's playing stops the audio")
    void removePlayingSong() throws IOException {
        player.play();

        player.songRemoved(0);
        playlist.removeSong(0);

        assertFalse(player.isPlaying());
        assertEquals(1, audio.stopCount);
    }

    @Test
    @DisplayName("PA-10 reset stops the audio and goes back to the first song")
    void resetStopsAudio() {
        player.next();
        player.play();

        player.reset();

        assertFalse(audio.playing);
        assertEquals(0, player.getCurrentIndex());
    }
}
