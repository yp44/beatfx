package org.beatfx.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.util.Defaults;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class BeatfxPane extends SplitPane {

    private final BeatfxModel beatfxModel;

    private final TabPane cyclesPane = new TabPane();

    private EditorPane editorPane;
    private final Tab addTab = new Tab("+");

    public BeatfxPane(BeatfxModel beatfxModel) {
        this.beatfxModel = beatfxModel;
        buildUI();
    }

    private void buildUI() {
        this.setOrientation(Orientation.VERTICAL);
        addTab.setClosable(false);
        cyclesPane.getTabs().add(addTab);

        cyclesPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
                if (newTab == addTab) {
                    String newName = findName(beatfxModel.getCycles().stream().map(c -> c.getId().get()).collect(Collectors.toList()));
                    Cycle newCycle = new Cycle(newName);
                    beatfxModel.getCycles().add(newCycle);

                    CycleTab newCycleTab = new CycleTab(newCycle);
                    newCycleTab.setOnClosed(new EventHandler<>() {
                        @Override
                        public void handle(Event event) {
                            beatfxModel.getCycles().remove(newCycle);
                        }
                    });
                    cyclesPane.getTabs().addAll(cyclesPane.getTabs().size() - 1, Collections.singleton(newCycleTab));
                    cyclesPane.getSelectionModel().selectPrevious();
                    newCycleTab.redrawCycle(cyclesPane.getTabs().get(0).getContent().getBoundsInParent().getHeight());
                }
            }
        });

        this.buildCyclePane();
        this.buildEditorPane();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(editorPane);
        editorPane.prefWidthProperty().bind(scrollPane.widthProperty());

        this.getItems().addAll(cyclesPane, scrollPane);

        this.getDividers().get(0).positionProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                ((CycleTab)cyclesPane.getSelectionModel().getSelectedItem()).redrawCycle(cyclesPane.getTabs().get(0).getContent().getBoundsInParent().getHeight());
            }
        });

    }

    private void buildCyclePane() {
        cyclesPane.getTabs().clear();
        cyclesPane.getTabs().addAll(beatfxModel.getCycles().stream().map(CycleTab::new).collect(Collectors.toList()));
        cyclesPane.getTabs().add(addTab);
    }

    private void buildEditorPane() {
        this.editorPane = new EditorPane(this.beatfxModel);
    }

    public void stageResized(Stage stage) {
        ((CycleTab) cyclesPane.getSelectionModel().getSelectedItem()).stageResized(stage);
    }

    private String findName(Collection<String> names) {
        String current = Defaults.NEW_TAB_NAME;
        int i = 1;
        while (names.contains(current)) {
            current = Defaults.NEW_TAB_NAME + i++;
        }

        return current;
    }

}
