/*
* Name: Jacob Wroblewski
* Project: Java Music Player (UI prototype)
* File: MusicPlayerUI.java
*
* This class builds everything you see on the music player screen.
* The playlist logic lives in Playlist.java and the play/pause/next/
* previous/volume logic lives in PlayerController.java. This class
* just builds the screen and calls those classes when buttons are clicked.
* No real audio plays yet.
*/
package musicplayer;
import java.io.File;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class MusicPlayerUI {

// the playlist and the player logic (these are the classes the unit tests check)
private final Playlist playlist = new Playlist();
private final PlayerController player = new PlayerController(playlist);

// kept up here so the buttons can update them
private Label songTitle;
private ListView<String> songList;

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

// shows the current song name once a playlist is picked
songTitle = new Label("No song selected");
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

// each button tells the PlayerController what to do, then updates the screen
playButton.setOnAction(e -> {
if (!player.play()) {
showMessage("The playlist is empty, add a song first.");
return;
}
System.out.println("Playing: " + player.getCurrentSong().getName());
showCurrentSong();
});
pauseButton.setOnAction(e -> {
player.pause();
System.out.println("Paused");
});
nextButton.setOnAction(e -> {
player.next();
showCurrentSong();
});
previousButton.setOnAction(e -> {
player.previous();
showCurrentSong();
});

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
Slider volumeSlider = new Slider(0, 100, player.getVolume());
volumeSlider.setPrefWidth(180);

// whenever the slider moves, send the new volume to the player
volumeSlider.valueProperty().addListener((obs, oldValue, newValue) ->
player.setVolume(newValue.doubleValue()));

HBox volumeRow = new HBox(12, volumeLabel, volumeSlider);
volumeRow.setAlignment(Pos.CENTER);
return volumeRow;
}

// Updates the playlist song list on the screen
private void updateSongList() {
songList.getItems().setAll(playlist.getSongNames());
}

// puts the current song's name at the top and highlights it in the list
private void showCurrentSong() {
File current = player.getCurrentSong();
if (current == null) {
songTitle.setText("No song selected");
songList.getSelectionModel().clearSelection();
return;
}
songTitle.setText(current.getName());
songList.getSelectionModel().select(player.getCurrentIndex());
}

// right side: the playlist and the add/remove buttons
private VBox createPlaylist() {
Label playlistTitle = new Label("Playlist");
playlistTitle.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");

// ListView shows a list you can click on and scroll through
// You can add a playlist folder and song by clicking "Select Playlist" and choosing the folder you want to use.
songList = new ListView<>();
songList.setPrefWidth(260);
songList.setPrefHeight(300);
Label emptyMessage = new Label("No playlist selected");
emptyMessage.setStyle("-fx-text-fill: #000000;");
songList.setPlaceholder(emptyMessage);

// clicking a song in the list makes it the current song
songList.setOnMouseClicked(e -> {
int index = songList.getSelectionModel().getSelectedIndex();
if (index >= 0) {
player.selectSong(index);
songTitle.setText(player.getCurrentSong().getName());
}
});

// let the list get taller if the window gets taller
VBox.setVgrow(songList, Priority.ALWAYS);
// Adds chosen song to playlist using Java's file explorer.

Button addButton = makeButton("Add Song", "Add Song button clicked");
addButton.setOnAction(e -> {
// If there is no playlist for a song to be added to, an error message shows up.
if (!playlist.hasFolder()) {
showMessage("No playlist selected, please select one.");
return;
}
// JavaFX file picker (the Swing one froze the app on Mac)
FileChooser songFileChooser = new FileChooser();
songFileChooser.setTitle("Add Song");
songFileChooser.getExtensionFilters().add(
new FileChooser.ExtensionFilter("Song Files", "*.mp3", "*.wav", "*.ogg"));
File selectedSongFile = songFileChooser.showOpenDialog(getWindow());
// selectedSongFile is null if the user clicked Cancel
if (selectedSongFile != null) {
try {
// Playlist copies the file into the playlist folder and adds it to the list
playlist.addSong(selectedSongFile);
updateSongList();
}
catch (IOException | IllegalArgumentException ex) {
showMessage("Could not add song: " + ex.getMessage());
}
}
});
// Removes/deletes song from the chosen playlist folder (without warning/confirmation prompt).
Button removeButton = makeButton("Remove Song", "Remove Song button clicked");
removeButton.setOnAction(e -> {
int selectedSongFile = songList.getSelectionModel().getSelectedIndex();
// If there is no song selected from the playlist, an error prompt shows on the screen.
if (selectedSongFile == -1) {
showMessage("No song selected, please select one.");
return;
}
try {
// Playlist deletes the file and takes it out of the list
playlist.removeSong(selectedSongFile);
updateSongList();
showCurrentSong();
}
catch (IOException ex) {
showMessage("Could not remove song: " + ex.getMessage());
}
});
// Lets user change the chosen playlist using Java's file explorer.
Button changePlaylistButton = makeButton("Select Playlist", "Select Playlist button clicked");
changePlaylistButton.setOnAction(e -> {
// JavaFX folder picker (the Swing one froze the app on Mac)
DirectoryChooser playlistFolderChooser = new DirectoryChooser();
playlistFolderChooser.setTitle("Select Playlist Folder");
File selectedFolder = playlistFolderChooser.showDialog(getWindow());
// selectedFolder is null if the user clicked Cancel
if (selectedFolder != null) {
// Playlist loads every music file in the folder
playlist.loadFromFolder(selectedFolder);
// new playlist, so start back at the first song
player.reset();
updateSongList();
showCurrentSong();
}
});

HBox playlistHeader = new HBox(10, playlistTitle, changePlaylistButton);
playlistHeader.setAlignment(Pos.CENTER_LEFT);
HBox playlistButtons = new HBox(8, addButton, removeButton);

VBox playlistBox = new VBox(12, playlistHeader, songList, playlistButtons);
playlistBox.setPrefWidth(280);
playlistBox.setPadding(new Insets(16));
playlistBox.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 14;");
return playlistBox;
}

// shows a pop-up message using JavaFX
private void showMessage(String message) {
Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
alert.setHeaderText(null);
alert.initOwner(getWindow());
alert.showAndWait();
}

// the app window, so pop-ups open on top of it
private Window getWindow() {
return songList.getScene().getWindow();
}

// helper method so I don't have to write the same button code over and over.
// it makes a button and when you click it, it prints the message.
private Button makeButton(String text, String message) {
Button button = new Button(text);
button.setOnAction(e -> System.out.println(message));
return button;
}
}
