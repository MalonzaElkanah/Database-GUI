package com.malone.dbms.desktopJavaFX.utils;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;


public class ViewUtils {

	public static TableView createTable(Vector colNames, Vector dataSet, boolean isEditable) {
		TableView tableView = new TableView();

        // Define table columns
        for (Object col : colNames) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(col.toString());
            tableColumn.setCellValueFactory(new MapValueFactory<>(col.toString()));

            if (isEditable) {
                tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                tableColumn.setOnEditCommit((TableColumn.CellEditEvent<Map, String> t) ->
                    (t.getTableView().getItems()
                        .get(t.getTablePosition().getRow())
                        ).put(t.getTableColumn().getText(), t.getNewValue())
                    );
            }

            tableView.getColumns().add(tableColumn);
        }
        
        // Convert the vector dataSet to maps
        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();
        int nRows = dataSet.size();

        for (int i = 0; i < nRows; i++) {
            Vector row = (Vector) dataSet.elementAt(i);
            
            Map<String, Object> data = new HashMap<>();

            for (int j = 0; j < row.size(); j++) {
                try {
                    data.put(
                        (colNames.elementAt(j)).toString(),
                        (row.elementAt(j)).toString());
                } catch (NullPointerException npe) {
                    data.put((colNames.elementAt(j)).toString(), " ");
                }
            }

            items.add(data);
        }

        tableView.getItems().addAll(items);

        return tableView;
	}

    public static TableView createEmptyTable(Vector colNames, int nRows) {
        TableView tableView = new TableView();
        tableView.setEditable(true);

        // Define table columns
        for (Object col : colNames) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(col.toString());
            tableColumn.setCellValueFactory(new MapValueFactory<>(col.toString()));

            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setOnEditCommit((TableColumn.CellEditEvent<Map, String> t) ->
                (t.getTableView().getItems()
                    .get(t.getTablePosition().getRow())
                    ).put(t.getTableColumn().getText(), t.getNewValue())
                );

            tableView.getColumns().add(tableColumn);
        }

        // Create maps with empty data
        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        for (int i = 0; i < nRows; i++) {            
            Map<String, Object> data = new HashMap<>();

            for (int j = 0; j < colNames.size(); j++) {
                data.put((colNames.elementAt(j)).toString(), "");
            }

            items.add(data);
        }

        tableView.getItems().addAll(items);

        return tableView;
    }
}
