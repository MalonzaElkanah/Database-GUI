package com.malone.dbms.desktopJavaFX.TableBuilder;

import javafx.scene.control.TextArea;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.geometry.Orientation;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

class TableBuilderView {
	private BorderPane root = new BorderPane();
    private TextArea SQLPane = new TextArea();
    private TableView tableView = new TableView();
    private Pane tablePane = new Pane();

    public TableBuilderView() {
        this.init();
    }

    public void init() {
        root.setPrefSize(1100, 400);
        tablePane.setPrefSize(1100, 400);

        ScrollPane scrollPane = new ScrollPane(SQLPane);
        scrollPane.setPrefSize(100, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        SplitPane splitter = new SplitPane(scrollPane, tablePane);
        splitter.setOrientation(Orientation.VERTICAL);
        splitter.setDividerPositions(0.3);

        root.setCenter(splitter);
    }

    public Pane getPane() {
        return root;
    }

    public TableView getTable() {
        return this.tableView;
    }

    public TextArea getSQLPane() {
        return this.SQLPane;
    }

    public String getSQLQuery() {
        return SQLPane.getText();
    }

    public void setTable(int nRows, String tableName) {
        tableView = createTable(nRows, tableName);
        tableView.setPrefSize(1100, 400);
        tableView.setEditable(true);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setPrefSize(1100, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        tablePane.getChildren().add(tableView);
    }

    private TableView createTable(int nRows, String tableName) {
        String[] dataTypes = {"CHAR", "VARCHAR", "INT", "FLOAT", "DATE"};
        String[] defNull = {"", "NULL", "NOT NULL"};
        String[] defUnique = {"", "UNIQUE"};
        String[] defPriKey = {"", "PRIMARY KEY"};
        String[] colNames = {"ColumnName", "DataType", "SIZE", "NULL", "UNIQUE", "PRIMARY KEY"};
        String[][] rowData = new String[nRows][colNames.length];
        TableView tableView = new TableView();
        tableView.setEditable(true);

        // Define table columns
        for (int j = 0; j < colNames.length; j++) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(colNames[j]);
            tableColumn.setCellValueFactory(new MapValueFactory<>(colNames[j]));

            if (colNames[j] == "ColumnName" || colNames[j] == "SIZE") {
                tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            } else if (colNames[j] == "DataType") {
                tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                    FXCollections.observableArrayList(dataTypes)));
            } else if (colNames[j] == "NULL") {
                tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                    FXCollections.observableArrayList(defNull)));
            } else if (colNames[j] == "UNIQUE") {
                tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                    FXCollections.observableArrayList(defUnique)));
            } else if (colNames[j] == "PRIMARY KEY") {
                tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                    FXCollections.observableArrayList(defPriKey)));
            }
                
            tableColumn.setOnEditCommit((TableColumn.CellEditEvent<Map, String> t) -> {
                (t.getTableView().getItems()
                    .get(t.getTablePosition().getRow())
                    ).put(t.getTableColumn().getText(), t.getNewValue());
                tableChanged(tableName);
            });

            tableView.getColumns().add(tableColumn);
        }
        
        // Create maps with empty data
        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        for (int i = 0; i < nRows; i++) {            
            Map<String, Object> data = new HashMap<>();

            for (int j = 0; j < colNames.length; j++) {
                data.put(colNames[j], "");
            }

            items.add(data);
        }

        tableView.getItems().addAll(items);
        
        return tableView;
    }

    public String parseTable() {
        String tableValues = "";

        int rows_count = tableView.getItems().size();
        int cols_count = tableView.getColumns().size();

        if (rows_count >= 0 && cols_count >= 0) {
            tableValues += "\n( ";
            ObservableList<HashMap> rows = tableView.getItems();
            ObservableList<TableColumn> columns = tableView.getColumns();

            for (int i = 0; i < rows_count; i++) {
                String rowData = "";
                // Map<String, Object> row = new HashMap<>();
                HashMap row = rows.get(i);

                for (TableColumn column : columns) {
                    String column_name = column.getText();
                    String field = row.get(column_name).toString();

                    if (field != null) {
                        if (field.length() == 0) {
                            break;
                        }

                        if (column_name == "SIZE") {
                            rowData += "(";
                        } else if (i > 0 || columns.indexOf(column) > 0) {
                            rowData += " ";
                        }

                        rowData += field;

                        if (column_name == "SIZE") {
                            rowData+=")";
                        }
                    }
                }

                if (rowData.length() == 0) {
                    break;
                }
                tableValues += rowData+",\n";
            }
        }

        if (tableValues.endsWith(",\n")) {
            int tvLen = tableValues.length() - 2;
            if (tvLen > 0) tableValues = tableValues.substring(0, tvLen);
        }

        tableValues += " );";

        return tableValues;
    }

    public void tableChanged(String tableName) {
        String SQLCommandRoot = "CREATE TABLE " + tableName;
        String SQLCommand = SQLCommandRoot+parseTable();
        // System.out.println(SQLCommand);

        this.SQLPane.setText(SQLCommand);
    }
}
