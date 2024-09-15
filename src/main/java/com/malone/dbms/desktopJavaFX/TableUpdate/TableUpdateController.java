package com.malone.dbms.desktopJavaFX.TableUpdate;

import com.malone.dbms.utils.DatabaseUtilities;

import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import javafx.collections.ObservableList;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class TableUpdateController {
    private TableUpdateView view = new TableUpdateView();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private Vector dataSet = new Vector();
    private Vector colNames = new Vector();
    private String tableName = "";
    private String SQLCommand[] = null;

	public TitledPane getTableUpdatePane(String tableName) {
		this.tableName = tableName;
        this.colNames = this.dbUtils.getColumnNames(tableName);
        this.dataSet = this.dbUtils.selectTable(tableName);
        Vector dataTypes = this.dbUtils.getDataTypes(tableName);

        view = new TableUpdateView();
        view.setTable(tableName, colNames, this.dataSet, dataTypes); // tableName, colNames, dataSet, dataTypes

        TitledPane titledPane = new TitledPane(
            "UPDATE TABLE '" + tableName + "'",
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
        	boolean isUpdated = this.dbUtils.execute(SQLQueries);
        	if (isUpdated) {
	            new Alert(
                	Alert.AlertType.INFORMATION,
	                "Data in Table " + this.tableName + " Updated.").show();
	        } else {
	            new Alert(
                	Alert.AlertType.ERROR,
	                "Error: Data not updated.").show();
	        }

        } else {
            new Alert(
                Alert.AlertType.ERROR,
                "Error: Can not issue empty query!").show();
        }        
    }
}
