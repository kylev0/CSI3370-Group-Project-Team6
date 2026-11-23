/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player
 * File: Playlist.java
 *
 * This class holds the songs in the playlist that's open right now.
 * A playlist is just a folder on the computer, and every music file
 * inside it is a song.
 *
 * This logic used to be inside the button code in MusicPlayerUI.
 * It was moved here so it can be unit tested on its own, without
 * having to open the window or click any buttons.
 */
package musicplayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Playlist {

    // the folder the playlist lives in (null until the user picks one)
    private File folder;

    // the song files in the playlist, in the order they show up in the list
    private final List<File> songs = new ArrayList<>();

    // checks if a file is a song we support by looking at the end of its name
    public boolean isMusicFile(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3")
                || name.endsWith(".wav")
                || name.endsWith(".ogg");
    }

    // opens a folder as the playlist and loads every music file in it
    public void loadFromFolder(File newFolder) {
        if (newFolder == null || !newFolder.isDirectory()) {
            throw new IllegalArgumentException("Playlist must be a folder.");
        }
        folder = newFolder;
        songs.clear();

        File[] files = folder.listFiles();
        if (files != null) {
            // sort by name so the songs always show up in the same order
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File file : files) {
                if (file.isFile() && isMusicFile(file)) {
                    songs.add(file);
                }
            }
        }
    }

    // copies a song into the playlist folder and adds it to the list.
    // gives back the new copy of the song inside the playlist folder.
    public File addSong(File songFile) throws IOException {
        // can't add a song if there's no playlist to put it in
        if (folder == null) {
            throw new IllegalStateException("No playlist selected.");
        }
        if (!isMusicFile(songFile)) {
            throw new IllegalArgumentException("Not a music file: " + songFile);
        }

        File destination = new File(folder, songFile.getName());
        Files.copy(songFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // if a song with the same name was already there it just got replaced,
        // so only add it to the list if it's actually new
        if (!songs.contains(destination)) {
            songs.add(destination);
        }
        return destination;
    }

    // deletes the song at this spot in the list from the playlist folder
    public void removeSong(int index) throws IOException {
        if (index < 0 || index >= songs.size()) {
            throw new IndexOutOfBoundsException("No song at index " + index);
        }
        Files.delete(songs.get(index).toPath());
        songs.remove(index);
    }

    // gives back the songs. it's read-only so other classes can't
    // change the list without going through add/remove above.
    public List<File> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    // just the file names, used to fill in the list on the screen
    public List<String> getSongNames() {
        List<String> names = new ArrayList<>();
        for (File song : songs) {
            names.add(song.getName());
        }
        return names;
    }

    // how many songs are in the playlist
    public int size() {
        return songs.size();
    }

    // true if a playlist folder has been picked
    public boolean hasFolder() {
        return folder != null;
    }

    public File getFolder() {
        return folder;
    }
}
