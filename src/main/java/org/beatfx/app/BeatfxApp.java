package org.beatfx.app;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.service.Player;
import org.beatfx.app.ui.BeatfxPane;

public class BeatfxApp extends Application {

    private final BeatfxModel beatfxModel = new BeatfxModel();

    @Override
    public void start(Stage stage) {

        buildUI(stage);

    }

    private void buildUI(Stage stage) {

        BeatfxPane beatfxPane = new BeatfxPane(beatfxModel);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        VBox vbox = buildMenuBar(beatfxPane);
        Scene scene = new Scene(vbox, bounds.getWidth() / 2, bounds.getHeight() / 2);

        stage.widthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
                beatfxPane.stageResized(stage);
            }
        });

        stage.heightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
                beatfxPane.stageResized(stage);
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private VBox buildMenuBar(BeatfxPane pane) {
        MenuBar menuBar = new MenuBar();
        Menu actions = new Menu("Actions");
        MenuItem play = new MenuItem("Play");
        actions.getItems().add(play);
        MenuItem stop = new MenuItem("Stop");
        actions.getItems().add(stop);
        menuBar.getMenus().add(actions);

        play.setOnAction(e -> play());

        stop.setOnAction(e -> {
            System.out.println("Stop !");
        });

        return new VBox(menuBar, pane);
    }

    private void play(){
        Task<Void> playTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Player player = new Player(BeatfxApp.this.beatfxModel);
                    player.play();
                }
                catch (Exception e){
                    System.err.println("Exception in play thread : " + e.getMessage());
                    e.printStackTrace(System.err);
                }
                return null;
            }
        };
        new Thread(playTask).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
