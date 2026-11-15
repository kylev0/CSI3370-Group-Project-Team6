/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: PlayerControllerTest.java
 *
 * Unit tests for the PlayerController class (Phase 4).
 * Each test creates a playlist of three fake songs in a temporary
 * folder that JUnit deletes after the test.
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

public class PlayerControllerTest {

    @TempDir Path tempDir;

    private Playlist playlist;
    private PlayerController player;

    // The volume uses double, so we allow a tiny margin
    private static final double DELTA = 0.0001;

    // Runs before each test: builds a playlist with song0, song1, song2
    @BeforeEach
    void setUp() throws IOException {
        Files.createFile(tempDir.resolve("song0.mp3"));
        Files.createFile(tempDir.resolve("song1.mp3"));
        Files.createFile(tempDir.resolve("song2.mp3"));

        playlist = new Playlist();
        playlist.loadFromFolder(tempDir.toFile());
        player = new PlayerController(playlist);
    }

    @Test
    @DisplayName("PC-01 a new player starts on the first song, paused, at volume 65")
    void startingState() {
        assertEquals(0, player.getCurrentIndex());
        assertFalse(player.isPlaying());
        assertEquals(65, player.getVolume(), DELTA);
        assertEquals("song0.mp3", player.getCurrentSong().getName());
    }

    @Test
    @DisplayName("PC-02 play starts playing when the playlist has songs")
    void playWithSongs() {
        assertTrue(player.play());
        assertTrue(player.isPlaying());
    }

    @Test
    @DisplayName("PC-03 play does nothing on an empty playlist")
    void playWithEmptyPlaylist() {
        PlayerController emptyPlayer = new PlayerController(new Playlist());

        assertFalse(emptyPlayer.play());
        assertFalse(emptyPlayer.isPlaying());
        assertNull(emptyPlayer.getCurrentSong());
    }

    @Test
    @DisplayName("PC-04 pause stops playing but stays on the same song")
    void pauseKeepsSong() {
        player.next();
        player.play();

        player.pause();

        assertFalse(player.isPlaying());
        assertEquals(1, player.getCurrentIndex());
    }

    @Test
    @DisplayName("PC-05 next moves forward and wraps from the last song to the first")
    void nextWrapsAround() {
        player.next();
        assertEquals(1, player.getCurrentIndex());
        player.next();
        assertEquals(2, player.getCurrentIndex());
        player.next();

        assertEquals(0, player.getCurrentIndex());
    }

    @Test
    @DisplayName("PC-06 previous on the first song wraps to the last song")
    void previousWrapsAround() {
        player.previous();
        assertEquals(2, player.getCurrentIndex());
        assertEquals("song2.mp3", player.getCurrentSong().getName());
    }

    @Test
    @DisplayName("PC-07 next and previous do nothing on an empty playlist")
    void nextPreviousEmptyPlaylist() {
        PlayerController emptyPlayer = new PlayerController(new Playlist());

        emptyPlayer.next();
        emptyPlayer.previous();

        assertEquals(0, emptyPlayer.getCurrentIndex());
    }

    @Test
    @DisplayName("PC-08 setVolume keeps values from 0 to 100 as they are")
    void setVolumeInRange() {
        player.setVolume(0);
        assertEquals(0, player.getVolume(), DELTA);
        player.setVolume(42.5);
        assertEquals(42.5, player.getVolume(), DELTA);
        player.setVolume(100);
        assertEquals(100, player.getVolume(), DELTA);
    }

    @Test
    @DisplayName("PC-09 setVolume below 0 becomes 0 and above 100 becomes 100")
    void setVolumeOutOfRange() {
        player.setVolume(-20);
        assertEquals(0, player.getVolume(), DELTA);
        player.setVolume(150);
        assertEquals(100, player.getVolume(), DELTA);
    }

    @Test
    @DisplayName("PC-10 selectSong jumps to a song, and a bad index throws an error")
    void selectSong() {
        player.selectSong(2);
        assertEquals("song2.mp3", player.getCurrentSong().getName());

        assertThrows(IndexOutOfBoundsException.class, () -> player.selectSong(3));
        assertThrows(IndexOutOfBoundsException.class, () -> player.selectSong(-1));
        assertEquals(2, player.getCurrentIndex(), "bad index shouldn't change the song");
    }

    @Test
    @DisplayName("PC-11 reset goes back to the first song and stops playing")
    void resetGoesBackToStart() {
        player.next();
        player.play();

        player.reset();

        assertEquals(0, player.getCurrentIndex());
        assertFalse(player.isPlaying());
    }

    @Test
    @DisplayName("PC-12 removing the current last song moves back to the new last song")
    void currentSongAfterRemove() throws IOException {
        player.selectSong(2);

        playlist.removeSong(2);

        assertEquals("song1.mp3", player.getCurrentSong().getName());
    }

    @Test
    @DisplayName("PC-13 making a player with no playlist throws an error")
    void nullPlaylist() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerController(null));
    }
}