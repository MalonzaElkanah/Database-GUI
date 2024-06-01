package com.malone.dbms.desktopJavaFX.TableDetail;

import com.malone.dbms.utils.DatabaseUtilities;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;

import java.util.Vector;

public class TableDetailController {
    private TableDetailView view = new TableDetailView();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();

	public Pane getTableDetailPane(String tableName) {
        Vector colNames = this.dbUtils.getColumnNames(tableName);
        Vector metaData = this.dbUtils.describeTable(tableName);
        Vector metaDataColNames = this.dbUtils.getColumnNamesUsingQuery(
            "DESCRIBE " + tableName + ";");
        Vector rowData = this.dbUtils.selectTable(tableName);

        view = new TableDetailView();
        view.setDescriptionTab(metaDataColNames, metaData);
        view.setDataTab(colNames, rowData);
        view.setQueryTab(colNames, new Vector());
        // this.tableDetailFrame.tabbedPane.setSelectedIndex(1);

        // this.tableDetailFrame.executeQueryButton.addActionListener(e -> executeQuery());

        return view.getPane();
    }

	public void executeQuery() {
        String SQLQuery = view.getSQLQuery();
        System.out.println(SQLQuery);
        if (SQLQuery != "") {
            Vector colNames = this.dbUtils.getColumnNamesUsingQuery(SQLQuery);
            Vector dataSet = this.dbUtils.executeQuery(SQLQuery);

            view.setQueryTab(colNames, dataSet);
        } else {
            new Alert(
                Alert.AlertType.ERROR,
                "Error: Can not issue empty query!").showAndWait();
        }
    }

}
