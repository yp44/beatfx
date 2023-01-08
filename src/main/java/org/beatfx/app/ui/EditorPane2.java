package org.beatfx.app.ui;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.model.PlayerRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class EditorPane2 extends TableView {

    private final static int SURNUMERARY_COLUMNS = 3;

    private BeatfxModel beatfxModel;

    public EditorPane2(BeatfxModel beatfxModel) {
        super();
        this.beatfxModel = beatfxModel;

        buildUI();
    }

    private void buildUI() {

        beatfxModel.getCycles().addListener(new ChangeListener<ObservableList<Cycle>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Cycle>> observableValue, ObservableList<Cycle> oldList, ObservableList<Cycle> newList) {
                System.out.println("Change cycles list, size : " + newList.size() + "/ " + EditorPane2.this.beatfxModel.getCycles().size());
                System.out.println("OLD : " + oldList.stream().map(c -> c.getId().get()).collect(Collectors.joining(",")));
                System.out.println("NEW : " + newList.stream().map(c -> c.getId().get()).collect(Collectors.joining(",")));
                System.out.println("Observable : " + observableValue.getValue().stream().map(e -> String.valueOf(e)).collect(Collectors.joining(",")));
                EditorPane2.this.refreshColumns();
            }
        });

        this.setEditable(true);

        TableColumn labelColumn = new TableColumn("Label");
        TableColumn gotoColumn = new TableColumn("Goto");
        TableColumn gotoRepeat = new TableColumn("Repeat");

        this.getColumns().add(labelColumn);
        this.getColumns().add(gotoColumn);
        this.getColumns().add(gotoRepeat);

        this.beatfxModel.getCycles().stream().forEach(c -> {
            TableColumn column = new TableColumn(c.getId().get());
            column.textProperty().bindBidirectional(c.getId());
            this.getColumns().add(column);
        });

        this.getColumns().stream().forEach(c -> ((TableColumn) c).setReorderable(false));

    }

    private void refreshColumns() {
        System.out.println("Refresh columns");
        List<TableColumn> columnsCycle = this.getColumns().subList(SURNUMERARY_COLUMNS, this.getColumns().size());
        if (this.beatfxModel.getCycles().size() > (this.getColumns().size() - SURNUMERARY_COLUMNS)) {
            // A new cycle has been added
            List<String> columnsName = new ArrayList<>();
            for (TableColumn c : columnsCycle) {
                columnsName.add(c.textProperty().get());
            }

            this.beatfxModel.getCycles().stream().filter(c -> !columnsName.contains(c.getId().get())).forEach(this::addColumn);
        } else {
            // A cycle has been deleted
            List<String> cyclesName = this.beatfxModel.getCycles().stream().map(c -> c.getId().get()).collect(Collectors.toList());
            List<TableColumn> toRemove = columnsCycle.stream().filter(c -> !cyclesName.contains(c.textProperty().get())).collect(Collectors.toList());
            toRemove.stream().forEach(this::removeColumn);
        }
    }

    private void addColumn(Cycle c) {
        TableColumn column = new TableColumn(c.getId().get());
        column.textProperty().bindBidirectional(c.getId());
        column.setReorderable(false);
        this.getColumns().add(column);
    }

    private void removeColumn(TableColumn column){
        this.getColumns().remove(column);
        for(PlayerRow playerRow : this.beatfxModel.getPlayerRows()){
            Iterator<Cycle> cycleIterator = playerRow.getCycles().iterator();
            while (cycleIterator.hasNext()){
                Cycle cycle = cycleIterator.next();
                if(cycle.getId().get().equals(column.textProperty().get())){
                    this.beatfxModel.getCycles().remove(cycle);
                }
            }
        }
    }

}
