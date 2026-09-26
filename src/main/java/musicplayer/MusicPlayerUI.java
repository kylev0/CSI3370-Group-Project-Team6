/*
 * Name: Jacob Wroblewski
 * Project: Java Music Player (UI prototype)
 * File: MusicPlayerUI.java
 *
 * This class builds everything you see on the music player screen.
 * Right now it's just the layout. The buttons print a message to the
 * console but don't actually play any music yet.
 */
package musicplayer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MusicPlayerUI {

    // builds the whole screen and gives it back to Main
    public Parent createContent() {

        // BorderPane splits the screen into top, center, left, right, and bottom
        // I'm using top for the song info, center for the player, and right for the playlist
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: #111827;"); // dark background

        root.setTop(createHeader());
        root.setCenter(createPlayerArea());
        root.setRight(createPlaylist());

        // space between the player and the playlist
        BorderPane.setMargin(root.getRight(), new Insets(18, 0, 0, 24));

        return root;
    }

    // top section: app name, "now playing", song name, and artist
    private VBox createHeader() {
        Label appTitle = new Label("Java Music Player");
        appTitle.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label nowPlaying = new Label("NOW PLAYING");
        nowPlaying.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");

        // placeholder song info for now
        Label songTitle = new Label("Example Song");
        songTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label artist = new Label("Example Artist");
        artist.setStyle("-fx-font-size: 14px; -fx-text-fill: #cbd5e1;");

        // VBox stacks things on top of each other (5 = space between them)
        VBox header = new VBox(5, appTitle, nowPlaying, songTitle, artist);
        header.setPadding(new Insets(0, 0, 18, 0));
        return header;
    }

    // middle section: album art, time bar, play buttons, and volume
    private VBox createPlayerArea() {

        // StackPane puts things on top of each other, so the text sits
        // on top of the gray box. This is just a fake album cover for now.
        StackPane albumArt = new StackPane();
        albumArt.setMinSize(280, 230);
        albumArt.setMaxSize(440, 300);
        albumArt.setStyle("-fx-background-color: #263449; -fx-background-radius: 16;");

        Label albumArtText = new Label("Album Art");
        albumArtText.setStyle("-fx-font-size: 24px; -fx-text-fill: #cbd5e1;");
        albumArt.getChildren().add(albumArtText);

        HBox timeline = createTimeline();
        HBox buttons = createPlaybackButtons();
        HBox volume = createVolumeControl();

        VBox playerArea = new VBox(22, albumArt, timeline, buttons, volume);
        playerArea.setAlignment(Pos.CENTER);
        playerArea.setPadding(new Insets(12, 4, 12, 4));

        // lets the album art get bigger when the window is made bigger
        VBox.setVgrow(albumArt, Priority.ALWAYS);

        return playerArea;
    }

    // the song progress bar with the times on each side
    private HBox createTimeline() {
        // these times are hard coded, they'll need to update from the song later
        Label currentTime = new Label("0:00");
        currentTime.setStyle("-fx-text-fill: #cbd5e1;");

        // slider goes from 0 to 100 and starts at 0
        Slider progressBar = new Slider(0, 100, 0);

        // make the slider stretch to fill the space in the row
        progressBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        Label totalTime = new Label("3:45");
        totalTime.setStyle("-fx-text-fill: #cbd5e1;");

        // HBox puts things side by side in a row
        HBox timeline = new HBox(10, currentTime, progressBar, totalTime);
        timeline.setAlignment(Pos.CENTER);
        return timeline;
    }

    // previous, play, pause, next
    private HBox createPlaybackButtons() {
        Button previousButton = makeButton("Previous", "Previous button clicked");
        Button playButton = makeButton("Play", "Play button clicked");
        Button pauseButton = makeButton("Pause", "Pause button clicked");
        Button nextButton = makeButton("Next", "Next button clicked");

        // made the play button blue so it stands out
        playButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #082f49; -fx-font-weight: bold;");

        HBox buttonRow = new HBox(12, previousButton, playButton, pauseButton, nextButton);
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    // volume label and slider
    private HBox createVolumeControl() {
        Label volumeLabel = new Label("Volume");
        volumeLabel.setStyle("-fx-text-fill: #cbd5e1;");

        // 0 to 100, starts at 65
        // TODO: hook this up to the actual audio later
        Slider volumeSlider = new Slider(0, 100, 65);
        volumeSlider.setPrefWidth(180);

        HBox volumeRow = new HBox(12, volumeLabel, volumeSlider);
        volumeRow.setAlignment(Pos.CENTER);
        return volumeRow;
    }

    // right side: the playlist and the add/remove buttons
    private VBox createPlaylist() {
        Label playlistTitle = new Label("Playlist");
        playlistTitle.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");

        // ListView shows a list you can click on and scroll through
        // these are just sample songs for testing
        ListView<String> songList = new ListView<>();
        songList.getItems().addAll(
                "Song 1  -  Artist 1",
                "Song 2  -  Artist 2",
                "Song 3  -  Artist 3",
                "Song 4  -  Artist 4",
                "Song 5  -  Artist 5"
        );
        songList.setPrefWidth(260);
        songList.setPrefHeight(300);

        // let the list get taller if the window gets taller
        VBox.setVgrow(songList, Priority.ALWAYS);

        // TODO: these don't actually add or remove songs yet
        Button addButton = makeButton("Add Song", "Add Song button clicked");
        Button removeButton = makeButton("Remove Song", "Remove Song button clicked");

        HBox playlistButtons = new HBox(8, addButton, removeButton);

        VBox playlist = new VBox(12, playlistTitle, songList, playlistButtons);
        playlist.setPrefWidth(280);
        playlist.setPadding(new Insets(16));
        playlist.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 14;");
        return playlist;
    }

    // helper method so I don't have to write the same button code over and over.
    // it makes a button and when you click it, it prints the message.
    private Button makeButton(String text, String message) {
        Button button = new Button(text);
        button.setOnAction(e -> System.out.println(message));
        return button;
    }
}
