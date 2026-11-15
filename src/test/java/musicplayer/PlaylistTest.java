/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: PlaylistTest.java
 *
 * Unit tests for the Playlist class (Phase 4).
 * Each test uses a temporary folder that JUnit creates and deletes,
 * so the tests never touch any actual music files.
 * The song files are blank files with music file names — Playlist only checks names, not contents.
 */
package musicplayer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PlaylistTest {

    // JUnit creates a new empty folder for each test and deletes it after
    @TempDir Path tempDir;

    private Playlist playlist;
    private File playlistFolder;   // acts as the playlist
    private File downloadsFolder;  // simulates the user's Downloads folder

    // This runs before each test, so every test starts fresh
    @BeforeEach
    void setUp() throws IOException {
        playlist = new Playlist();
        playlistFolder = Files.createDirectory(tempDir.resolve("MyPlaylist")).toFile();
        downloadsFolder = Files.createDirectory(tempDir.resolve("Downloads")).toFile();
    }

    // Helper that creates an empty file with the given name in a folder
    private File makeFile(File folder, String name) throws IOException {
        return Files.createFile(folder.toPath().resolve(name)).toFile();
    }

    @Test
    @DisplayName("PL-01 isMusicFile accepts .mp3, .wav and .ogg (any capitals)")
    void isMusicFileAcceptsSupportedTypes() {
        assertTrue(playlist.isMusicFile(new File("song.mp3")));
        assertTrue(playlist.isMusicFile(new File("song.wav")));
        assertTrue(playlist.isMusicFile(new File("SONG.OGG")));
    }

    @Test
    @DisplayName("PL-02 isMusicFile rejects other files and null")
    void isMusicFileRejectsOtherFiles() {
        assertFalse(playlist.isMusicFile(new File("notes.txt")));
        assertFalse(playlist.isMusicFile(new File("cover.jpg")));
        assertFalse(playlist.isMusicFile(new File("mp3")));
        assertFalse(playlist.isMusicFile(null));
    }

    @Test
    @DisplayName("PL-03 loadFromFolder only loads music files, sorted by name")
    void loadFromFolderLoadsOnlyMusicFiles() throws IOException {
        makeFile(playlistFolder, "b_song.wav");
        makeFile(playlistFolder, "a_song.mp3");
        makeFile(playlistFolder, "readme.txt");
        makeFile(playlistFolder, "cover.jpg");

        playlist.loadFromFolder(playlistFolder);

        assertEquals(2, playlist.size());
        assertEquals(List.of("a_song.mp3", "b_song.wav"), playlist.getSongNames());
        assertEquals(playlistFolder, playlist.getFolder());
    }

    @Test
    @DisplayName("PL-04 loadFromFolder on an empty folder gives an empty playlist")
    void loadFromEmptyFolder() {
        playlist.loadFromFolder(playlistFolder);

        assertTrue(playlist.hasFolder());
        assertEquals(0, playlist.size());
    }

    @Test
    @DisplayName("PL-05 loadFromFolder throws an error for a file or null")
    void loadFromFolderRejectsNonFolder() throws IOException {
        File notAFolder = makeFile(downloadsFolder, "song.mp3");

        assertThrows(IllegalArgumentException.class, () -> playlist.loadFromFolder(notAFolder));
        assertThrows(IllegalArgumentException.class, () -> playlist.loadFromFolder(null));
    }

    @Test
    @DisplayName("PL-06 addSong copies the file into the playlist folder and adds it")
    void addSongCopiesFileAndAddsIt() throws IOException {
        playlist.loadFromFolder(playlistFolder);
        File newSong = makeFile(downloadsFolder, "new_song.mp3");

        File copy = playlist.addSong(newSong);

        assertEquals(1, playlist.size());
        assertTrue(copy.exists(), "song should be copied into the playlist folder");
        assertEquals(playlistFolder, copy.getParentFile());
        assertTrue(newSong.exists(), "original song should still be there");
    }

    @Test
    @DisplayName("PL-07 addSong with the same song twice doesn't make a duplicate")
    void addSameSongTwiceNoDuplicate() throws IOException {
        playlist.loadFromFolder(playlistFolder);
        File newSong = makeFile(downloadsFolder, "new_song.mp3");

        playlist.addSong(newSong);
        playlist.addSong(newSong);

        assertEquals(1, playlist.size());
    }

    @Test
    @DisplayName("PL-08 addSong throws an error if no playlist is selected")
    void addSongWithNoPlaylist() throws IOException {
        File newSong = makeFile(downloadsFolder, "new_song.mp3");

        assertThrows(IllegalStateException.class, () -> playlist.addSong(newSong));
    }

    @Test
    @DisplayName("PL-09 addSong throws an error for a file that isn't music")
    void addSongRejectsNonMusic() throws IOException {
        playlist.loadFromFolder(playlistFolder);
        File textFile = makeFile(downloadsFolder, "notes.txt");

        assertThrows(IllegalArgumentException.class, () -> playlist.addSong(textFile));
        assertEquals(0, playlist.size());
    }

    @Test
    @DisplayName("PL-10 removeSong deletes the file and takes it out of the list")
    void removeSongDeletesFile() throws IOException {
        File song1 = makeFile(playlistFolder, "a_song.mp3");
        makeFile(playlistFolder, "b_song.mp3");
        playlist.loadFromFolder(playlistFolder);

        playlist.removeSong(0);

        assertEquals(1, playlist.size());
        assertFalse(song1.exists(), "removed song should be deleted from the folder");
        assertEquals(List.of("b_song.mp3"), playlist.getSongNames());
    }

    @Test
    @DisplayName("PL-11 removeSong throws an error for an index that doesn't exist")
    void removeSongBadIndex() throws IOException {
        makeFile(playlistFolder, "a_song.mp3");
        playlist.loadFromFolder(playlistFolder);

        assertThrows(IndexOutOfBoundsException.class, () -> playlist.removeSong(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> playlist.removeSong(1));
        assertEquals(1, playlist.size(), "nothing should be removed");
    }

    @Test
    @DisplayName("PL-12 getSongs can't be changed from outside the class")
    void getSongsIsReadOnly() {
        playlist.loadFromFolder(playlistFolder);

        assertThrows(UnsupportedOperationException.class, () -> playlist.getSongs().add(new File("sneaky.mp3")));
    }
}
