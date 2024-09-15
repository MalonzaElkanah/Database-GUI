package com.malone.dbms.desktopJavaFX.TableUpdate;

import com.malone.dbms.desktopJavaFX.utils.ViewUtils;

import javafx.scene.control.TextArea;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.geometry.Orientation;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

class TableUpdateView {
	private BorderPane root = new BorderPane();
    private TextArea SQLPane = new TextArea();
    private TableView tableView = new TableView();
    private Pane tablePane = new Pane();
    private String[] SQLCommands = new String[0];

    public TableUpdateView() {
        this.init();
    }

    public void init() {
        root.setPrefSize(1100, 400);
        tablePane.setPrefSize(1100, 400);

        ScrollPane scrollPane = new ScrollPane(SQLPane);
        scrollPane.setPrefSize(1100, 400);
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

    public String[] getSQLCommands() {
        return SQLCommands;
    }

    public void setTable(String tableName, Vector colNames, Vector dataSet, Vector dataTypes) {
        tableView = createTable(tableName, colNames, dataSet, dataTypes);
        // tableView.setPrefSize(400, 400);
        tableView.setEditable(true);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setPrefSize(1100, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        tablePane.getChildren().add(tableView);
    }

    public TableView createTable(String tableName, Vector colNames, Vector dataSet, Vector dataTypes) {
        TableView tableView = new TableView();

        // Define table columns
        for (Object col : colNames) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(col.toString());
            tableColumn.setCellValueFactory(new MapValueFactory<>(col.toString()));
            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setOnEditCommit((TableColumn.CellEditEvent<Map, String> t) -> {
                (t.getTableView().getItems()
                    .get(t.getTablePosition().getRow())
                    ).put(t.getTableColumn().getText(), t.getNewValue());
                tableChanged(tableName, dataSet, colNames, dataTypes);
                });

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

    public void tableChanged(String tableName, Vector dataSet, Vector colNames, Vector dataTypes) {
        Vector rowData = parseTable(dataSet, colNames, dataTypes);
        SQLCommands = new String[rowData.size()];
        String SQLCommandRoot = "UPDATE " + tableName;

        SQLPane.setText("");

        for (int i=0; i < rowData.size(); i++) {
            if (rowData.elementAt(i) == null) break;

            SQLCommands[i] = SQLCommandRoot + (String)rowData.elementAt(i);
            SQLPane.appendText(SQLCommands[i]);
        }
    }

    public Vector parseTable(Vector dataSet, Vector colNames, Vector dataTypes) {
        int rowCount = tableView.getItems().size();
        int cols = tableView.getColumns().size();

        // Vector dataTypes = this.dbUtils.getDataTypes(this.tableName);

        Vector tableValues = new Vector();

        if (rowCount >= 0 && cols >= 0) {
            ObservableList<HashMap> rows = tableView.getItems();
            ObservableList<TableColumn> columns = tableView.getColumns();

            for (int i = 0; i < rowCount; i++) {
                String rowData = "";
                String whereClauseData = "";
                Vector rowDataSet = (Vector) dataSet.elementAt(i);

                HashMap row = rows.get(i);

                for (TableColumn column : columns) {
                    String column_name = column.getText();
                    String field = row.get(column_name).toString();

                    if (field.length() > 0) {
                        field = this.fixApostrophes(field);

                        if (columns.indexOf(column) > 0) rowData += ", ";

                        if (getStringDataTypes().contains(
                            dataTypes.get(columns.indexOf(column)).toString())) {
                            rowData += colNames.get(columns.indexOf(column)) + " = '" + field + "'";
                        } else {
                            rowData += colNames.get(columns.indexOf(column)) + " = " + field;
                        }

                        try {
                            String value = (rowDataSet.elementAt(columns.indexOf(column))).toString();

                            if (columns.indexOf(column) > 0) whereClauseData += " AND "; 

                            if (getStringDataTypes().contains(
                                dataTypes.get(columns.indexOf(column)).toString())) {
                                whereClauseData += colNames.get(columns.indexOf(column)) + " = '"+ value +"'";
                            } else {
                                whereClauseData += colNames.get(columns.indexOf(column)) + " = " + value;
                            }
                        } catch (NullPointerException npe) { }

                    }
                }
                if (rowData.length() == 0) break;
                tableValues.addElement(" SET " + rowData + " WHERE " + whereClauseData + ";\n");
            }
        }

        return tableValues;
    }

    private String fixApostrophes(String in) {
        int n = 0;

        while((n = in.indexOf("'", n)) >= 0) {
            in = in.substring(0,n) + "'" + in.substring(n);
            n += 2;
        }

        return in;
    }

    public Vector<String> getStringDataTypes() {
        String[] dataTypes = {
            "CHAR", "VARCHAR", "BINARY", "VARBINARY", "BLOB", "TEXT", "ENUM", "SET"}; 
        return new Vector<String>(Arrays.asList(dataTypes));
    }
}
