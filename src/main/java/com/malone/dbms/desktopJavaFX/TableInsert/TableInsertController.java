package com.malone.dbms.desktopJavaFX.TableInsert;

import com.malone.dbms.utils.DatabaseUtilities;

import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import javafx.collections.ObservableList;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class TableInsertController {
    private TableInsertView view = new TableInsertView();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String tableName = "";
    private String SQLCommand[] = null;

	public TitledPane getTableInsertPane(String tableName) {
		this.tableName = tableName;
        Vector colNames = this.dbUtils.getColumnNames(tableName);
        Vector dataTypes = this.dbUtils.getDataTypes(tableName);

        view = new TableInsertView();
        view.setTable(colNames, 15, tableName, dataTypes);

        TitledPane titledPane = new TitledPane(
            "INSERT TABLE '" + tableName + "'",
            view.getPane());
        titledPane.setCollapsible(false);
        titledPane.setPrefSize(1100, 700);

        return titledPane;
    }

    public String[] getSQLCommands() {
        return view.getSQLCommands();
    }

	public void executeQuery() {
        String[] SQLQueries = view.getSQLCommands();

        if (SQLQueries.length > 0) {
        	boolean isInserted = this.dbUtils.execute(SQLQueries);
        	if (isInserted) {
	            new Alert(
                	Alert.AlertType.INFORMATION,
	                "Data Inserted to " + this.tableName).show();
	        } else {
	            new Alert(
                	Alert.AlertType.ERROR,
	                "Error: Data not inserted to " + this.tableName).show();
	        }

        } else {
            new Alert(
                Alert.AlertType.ERROR,
                "Error: Can not issue empty query!").show();
        }        
    }
}
