/*
* Name: Jacob Wroblewski
* Project: Java Music Player (UI prototype)
* File: Main.java
*
* This is the starting point of the program. It opens the window
* and puts the music player screen inside it.
*/
package musicplayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

// JavaFX calls start() on its own after launch() runs.
// The Stage is the actual window.
@Override
public void start(Stage stage) {

// make the UI object (this class builds all the buttons, labels, etc.)
MusicPlayerUI ui = new MusicPlayerUI();

// the Scene holds everything that shows up in the window
// 1000 x 650 is the starting size of the window
Scene scene = new Scene(ui.createContent(), 1000, 650);

stage.setTitle("Java Music Player");

// don't let the window get too small or the layout gets squished
stage.setMinWidth(760);
stage.setMinHeight(520);

// put the scene in the window and show it
stage.setScene(scene);
stage.show();
}

public static void main(String[] args) {
// this starts JavaFX, which then calls start() above
launch(args);
}
}
