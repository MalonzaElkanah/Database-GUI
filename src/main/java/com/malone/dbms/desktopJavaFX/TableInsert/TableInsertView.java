package com.malone.dbms.desktopJavaFX.TableInsert;

import com.malone.dbms.desktopJavaFX.utils.ViewUtils;

import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.geometry.Orientation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class TableInsertView {
    private BorderPane root = new BorderPane();
    private TextArea SQLPane = new TextArea();
    private TableView tableView = new TableView();
    private Pane tablePane = new Pane();
    private String[] SQLCommands = new String[0];
    // public JButton insertButton = new JButton("Insert Data");

    public TableInsertView() {
        this.init();
    }

    public void init() {
        root.setPrefSize(1100, 400);
        // tablePane.setPrefSize(500, 400);

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

    public void setTable(Vector colNames, int nRows, String tableName, Vector dataTypes) {
        tableView = createEmptyTable(colNames, nRows, tableName, dataTypes);
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

    public TableView createEmptyTable(Vector colNames, int nRows, String tableName, Vector dataTypes) {
        TableView tableView = new TableView();
        tableView.setEditable(true);

        // Define table columns
        for (Object col : colNames) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(col.toString());
            tableColumn.setCellValueFactory(new MapValueFactory<>(col.toString()));

            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setOnEditCommit((TableColumn.CellEditEvent<Map, String> t) -> {
                            (t.getTableView().getItems()
                                .get(t.getTablePosition().getRow())
                                ).put(t.getTableColumn().getText(), t.getNewValue());
                            tableChanged(tableName, dataTypes);
                            });

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

    public Vector parseTable(Vector dataTypes) {
        int rowCount = tableView.getItems().size();
        int cols = tableView.getColumns().size();

        // Vector dataTypes = this.dbUtils.getDataTypes();

        Vector tableValues = new Vector();

        if (rowCount >= 0 && cols >= 0) {
            ObservableList<HashMap> rows = tableView.getItems();
            ObservableList<TableColumn> columns = tableView.getColumns();
            for (int i = 0; i < rowCount; i++) {
                String rowData = "";
                // Map<String, Object> row = new HashMap<>();
                // ObservableList row = tableView.getItems();
                HashMap row = rows.get(i);

                for (TableColumn column: columns) {
                    String column_name = column.getText();
                    String field = row.get(column_name).toString();

                    if (field.length() > 0) {
                        field = fixApostrophes(field);

                        if (columns.indexOf(column) > 0) rowData += ", ";

                        if (getStringDataTypes().contains(
                            dataTypes.get(columns.indexOf(column)).toString())) {
                            rowData += "'"+field+"'";
                        } else {
                            rowData += field;
                        }
                    }
                }
                if (rowData.length() == 0) break;
                tableValues.addElement(" ( " + rowData + " );\n");
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

    public void tableChanged(String tableName, Vector dataTypes) {
        Vector rowData = parseTable(dataTypes);
        SQLCommands = new String[rowData.size()];
        String SQLCommandRoot = "INSERT INTO " + tableName + " VALUES ";

        this.SQLPane.setText("");

        for (int i=0; i < rowData.size(); i++) {
            if (rowData.elementAt(i) == null) break;

            SQLCommands[i] = SQLCommandRoot + (String)rowData.elementAt(i);
            this.SQLPane.appendText(SQLCommands[i]);
        }
    }

    public Vector<String> getStringDataTypes() {
        String[] dataTypes = {
            "CHAR", "VARCHAR", "BINARY", "VARBINARY", "BLOB", "TEXT", "ENUM", "SET"}; 
        return new Vector<String>(Arrays.asList(dataTypes));
    }
}
