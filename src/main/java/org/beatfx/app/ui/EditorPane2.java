package org.beatfx.app.ui;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.model.PlayerRow;
import org.beatfx.app.util.Defaults;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
            TableColumn tc = buildCycleColumn(c);
            this.getColumns().add(tc);
        });

        this.getColumns().stream().forEach(c -> ((TableColumn) c).setReorderable(false));

        this.setItems(this.beatfxModel.getPlayerRows());
    }

    private void refreshColumns() {
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

    private TableColumn<PlayerRow, Boolean> buildCycleColumn(Cycle c) {
        TableColumn<PlayerRow, Boolean> column = new TableColumn(c.getId().get());
        column.textProperty().bindBidirectional(c.getId());
        column.setReorderable(false);
        column.setEditable(false);

        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PlayerRow, Boolean>, ObservableValue<Boolean>>() {
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PlayerRow, Boolean> p) {
                boolean hasCycle = p.getValue().hasCycle(column.getText());
                return new SimpleBooleanProperty(hasCycle);
            }
        });

        column.setCellFactory(col -> {
            TableCell<PlayerRow, Boolean> cell = new TableCell<PlayerRow, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        this.setStyle("-fx-background-color: " + Defaults.NULL_CELL_COLOR);
                    } else {
                        if (item) {
                            this.setStyle("-fx-background-color: " + Defaults.ACTIVE_CYCLE_COLOR);
                        } else {
                            this.setStyle("-fx-background-color: " + Defaults.NOTACTIVE_CYCLE_COLOR);
                        }
                    }
                }

            };

            cell.setOnMouseClicked(e -> {
                if(cell.getIndex() >= cell.getTableView().getItems().size()){
                    // Click on a non-active cell
                    return;
                }
                PlayerRow playerRow = cell.getTableView().getItems().get(cell.getIndex());
                Optional<Cycle> cycleInPlayerRowList = playerRow.getCycles().stream().filter(cc -> cc.getId().get().equals(column.getText())).findFirst();
                Optional<Cycle> columnCycle = beatfxModel.getCycleById(column.getText());

                if (cycleInPlayerRowList.isPresent()) {
                    playerRow.getCycles().remove(columnCycle.get());
                } else {
                    playerRow.getCycles().add(columnCycle.get());
                }

                setCellStyle(cell);
            });

            setCellStyle(cell);
            return cell;
        });

        return column;
    }

    private void setCellStyle(TableCell<PlayerRow, Boolean> cell) {
        if (cell.getTableView() == null || cell.getTableColumn() == null) {
            // not yet in a table
            return;
        }

        PlayerRow playerRow = cell.getTableView().getItems().get(cell.getIndex());
        Optional<Cycle> cycleInPlayerRowList = playerRow.getCycles().stream().filter(cc -> cc.getId().get().equals(cell.getTableColumn().getText())).findFirst();

        if (cycleInPlayerRowList.isPresent()) {
            cell.setStyle("-fx-background-color: " + Defaults.ACTIVE_CYCLE_COLOR);
        } else {
            cell.setStyle("-fx-background-color: " + Defaults.NOTACTIVE_CYCLE_COLOR);
        }
    }

    private void addColumn(Cycle c) {
        TableColumn tc = buildCycleColumn(c);
        this.getColumns().add(tc);
    }

    private void removeColumn(TableColumn column) {
        this.getColumns().remove(column);
        for (PlayerRow playerRow : this.beatfxModel.getPlayerRows()) {
            Iterator<Cycle> cycleIterator = playerRow.getCycles().iterator();
            Cycle cycle = null;
            boolean toRemove = false;
            while (cycleIterator.hasNext()) {
                cycle = cycleIterator.next();
                if (cycle.getId().get().equals(column.getText())) {
                    toRemove = true;
                    break;
                }
            }
            if (toRemove) {
                playerRow.getCycles().remove(cycle);
            }
        }
    }

}
