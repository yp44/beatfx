package org.beatfx.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.util.Defaults;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BeatfxPane extends VBox {

    private final BeatfxModel beatfxModel;

    private TabPane cyclesPane = new TabPane();
    private Tab addTab = new Tab("+");

    public BeatfxPane(BeatfxModel beatfxModel) {
        this.beatfxModel = beatfxModel;
        buildUI();
    }

    private void buildUI() {
        addTab.setClosable(false);
        cyclesPane.getTabs().add(addTab);

        cyclesPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab oldTab, Tab newTab) {
                if (newTab == addTab) {
                    String newName = findName(beatfxModel.getCycles().stream().map(c -> c.getId().get()).collect(Collectors.toList()));
                    Cycle newCycle = new Cycle(newName);
                    beatfxModel.getCycles().add(newCycle);

                    CycleTab newCycleTab = new CycleTab(newCycle);
                    newCycleTab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            beatfxModel.getCycles().remove(newCycle);
                        }
                    });
                    cyclesPane.getTabs().addAll(cyclesPane.getTabs().size() - 1, Arrays.asList(newCycleTab));
                    cyclesPane.getSelectionModel().selectPrevious();
                    newCycleTab.redrawCycle(cyclesPane.getTabs().get(0).getContent().getBoundsInParent().getHeight());
                }
            }
        });

        this.buildCyclePane();

        this.getChildren().add(cyclesPane);

    }

    private void buildCyclePane() {
        cyclesPane.getTabs().clear();
        cyclesPane.getTabs().addAll(beatfxModel.getCycles().stream().map(c -> new CycleTab(c)).collect(Collectors.toList()));
        cyclesPane.getTabs().add(addTab);
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
