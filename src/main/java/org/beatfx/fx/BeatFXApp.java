package org.beatfx.fx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.beatfx.fx.widgets.CycleTab;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BeatFXApp extends Application  {

    private final static String NEW_TAB_NAME = "New";

    public final static int PADDING = 5;

    private TabPane tabPane;

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        buildUI(stage);
    }

    private void buildUI(Stage stage) {
        this.stage = stage;
        this.tabPane = new TabPane();

        CycleTab firstTab = new CycleTab("First");
        firstTab.setClosable(false);

        Tab addTab = new CycleTab("+");
        addTab.setClosable(false);

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
                if(newTab == addTab){
                    String newName = findName(tabPane.getTabs().stream().map(t -> t.getText()).collect(Collectors.toList()));
                    CycleTab newCycleTab = new CycleTab(newName);
                    tabPane.getTabs().addAll(tabPane.getTabs().size() - 1, Arrays.asList(newCycleTab));
                    tabPane.getSelectionModel().selectPrevious();
                    newCycleTab.redrawCycle(stage);
                }
            }
        });

        tabPane.getTabs().addAll(firstTab, addTab);

        stage.setTitle("BeatFX");

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setScene(new Scene(tabPane, bounds.getWidth() / 2, bounds.getHeight() / 2));

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                redrawCurrentCyclePane();
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                redrawCurrentCyclePane();
            }
        });

        stage.show();
        firstTab.redrawCycle(stage);
    }

    private void redrawCurrentCyclePane(){
        ((CycleTab)this.tabPane.getSelectionModel().getSelectedItem()).redrawCycle(this.stage);
    }

    private String findName(Collection names){
        String current = NEW_TAB_NAME;
        int i = 1;
        while(names.contains(current)){
            current = NEW_TAB_NAME + " " + i++;
        }

        return current;
    }

    public static void main(String[] args) {
        launch();
    }

}
