package org.beatfx.app.ui;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class EditorPane extends VBox {

    private final static int SURNUMERARY_COLUMNS = 3;

    private final BeatfxModel beatfxModel;

    private final TableView<PlayerRow> tableView = new TableView<>();

    public EditorPane(BeatfxModel beatfxModel) {
        super();
        this.beatfxModel = beatfxModel;
        buildUI();
    }

    private void buildUI() {
        beatfxModel.getCycles().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Cycle>> observableValue, ObservableList<Cycle> oldList, ObservableList<Cycle> newList) {
                EditorPane.this.refreshColumns();
            }
        });

        tableView.setEditable(true);

        TableColumn<PlayerRow, String> labelColumn = new TableColumn<>("Label");
        labelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        labelColumn.setEditable(true);
        labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        labelColumn.setOnEditCommit(e -> {
            PlayerRow playerRow = e.getRowValue();
            String newLabel = e.getNewValue();
            playerRow.getLabelProperty().setValue(newLabel);
        });

        TableColumn<PlayerRow, String> gotoColumn = new TableColumn<>("Renvoi");
        gotoColumn.setCellValueFactory(new PropertyValueFactory<>("gotoLabel"));
        gotoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gotoColumn.setEditable(true);
        gotoColumn.setOnEditCommit(e -> {
            PlayerRow playerRow = e.getRowValue();
            String newLabel = e.getNewValue();
            playerRow.getGotoLabelProperty().setValue(newLabel);
        });

        TableColumn<PlayerRow, Integer> gotoRepeat = new TableColumn<>("Répétition");
        gotoRepeat.setCellValueFactory(new PropertyValueFactory<>("gotoRepeat"));
        gotoRepeat.setEditable(true);
        gotoRepeat.setCellFactory(col -> new TableCell<>(){
            private final Spinner<Integer> spinner = new Spinner<>(0, 100, 0);
            {
                spinner.maxWidthProperty().setValue(75);
            }

            @Override
            public void updateItem(Integer item, boolean empty){
                super.updateItem(item, empty);

                if(item == null || empty){
                    setText(null);
                    setGraphic(null);
                }
                else{
                    spinner.getValueFactory().setValue(item);
                    spinner.valueProperty().addListener(new ChangeListener<>() {
                        @Override
                        public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                            PlayerRow playerRow = getTableRow().getItem();
                            playerRow.getGotoRepeatProperty().setValue(newInt);
                        }
                    });
                    setGraphic(spinner);
                }
            }
        });

        tableView.getColumns().add(labelColumn);
        tableView.getColumns().add(gotoColumn);
        tableView.getColumns().add(gotoRepeat);

        this.beatfxModel.getCycles().forEach(c -> {
            TableColumn<PlayerRow, Boolean> tc = buildCycleColumn(c);
            tableView.getColumns().add(tc);
        });

        tableView.getColumns().forEach(c -> c.setReorderable(false));

        tableView.setItems(this.beatfxModel.getPlayerRows());

        HBox tableMenu = new HBox();

        Spinner<Number> numberCtrl = new Spinner<>(1, 100, 1);
        numberCtrl.editableProperty().setValue(true);
        tableMenu.getChildren().add(numberCtrl);

        Button addBtn = new Button("Add rows");
        addBtn.setOnAction(e -> {
            List<PlayerRow> newPlayerRows = new ArrayList<>();
            int n = numberCtrl.getValueFactory().getValue().intValue();
            for (int i = 0; i < n; i++) {
                newPlayerRows.add(new PlayerRow());
            }
            this.beatfxModel.getPlayerRows().addAll(newPlayerRows);
        });
        tableMenu.getChildren().add(addBtn);

        this.getChildren().addAll(tableMenu, tableView);
    }

    private void refreshColumns() {
        List<TableColumn<PlayerRow, ?>> columnsCycle = tableView.getColumns().subList(SURNUMERARY_COLUMNS, tableView.getColumns().size());
        if (this.beatfxModel.getCycles().size() > (tableView.getColumns().size() - SURNUMERARY_COLUMNS)) {
            // A new cycle has been added
            List<String> columnsName = new ArrayList<>();
            for (TableColumn<PlayerRow, ?> c : columnsCycle) {
                columnsName.add(c.textProperty().get());
            }

            this.beatfxModel.getCycles().stream().filter(c -> !columnsName.contains(c.getId().get())).forEach(this::addColumn);
        } else {
            // A cycle has been deleted
            List<String> cyclesName = this.beatfxModel.getCycles().stream().map(c -> c.getId().get()).collect(Collectors.toList());
            List<TableColumn<PlayerRow, ?>> toRemove = columnsCycle.stream().filter(c -> !cyclesName.contains(c.textProperty().get())).collect(Collectors.toList());
            toRemove.forEach(this::removeColumn);
        }
    }

    private TableColumn<PlayerRow, Boolean> buildCycleColumn(Cycle c) {
        TableColumn<PlayerRow, Boolean> column = new TableColumn<>(c.getId().get());
        column.textProperty().bindBidirectional(c.getId());
        column.setReorderable(false);
        column.setEditable(false);

        column.setCellValueFactory(new Callback<>() {
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PlayerRow, Boolean> p) {
                boolean hasCycle = p.getValue().hasCycle(column.getText());
                return new SimpleBooleanProperty(hasCycle);
            }
        });

        column.setCellFactory(col -> {
            TableCell<PlayerRow, Boolean> cell = new TableCell<>() {
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
                if (cell.getIndex() >= cell.getTableView().getItems().size()) {
                    // Click on a non-active cell
                    return;
                }
                PlayerRow playerRow = cell.getTableView().getItems().get(cell.getIndex());
                Optional<Cycle> cycleInPlayerRowList = playerRow.getCycles().stream().filter(cc -> cc.getId().get().equals(column.getText())).findFirst();
                Optional<Cycle> columnCycle = beatfxModel.getCycleById(column.getText());

                if(columnCycle.isEmpty()){
                    // Cycle doesn't exist
                    return;
                }

                System.out.println("Label : " + playerRow.getLabel());
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
        TableColumn<PlayerRow, Boolean> tc = buildCycleColumn(c);
        tableView.getColumns().add(tc);
    }

    private void removeColumn(TableColumn<PlayerRow, ?> column) {
        tableView.getColumns().remove(column);
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
