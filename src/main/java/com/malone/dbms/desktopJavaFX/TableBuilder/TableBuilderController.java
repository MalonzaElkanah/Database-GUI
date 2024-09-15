package com.malone.dbms.desktopJavaFX.TableBuilder;

import com.malone.dbms.utils.DatabaseUtilities;

import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;

import javafx.collections.ObservableList;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;


import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import javafx.scene.control.TableColumn.CellEditEvent;

public class TableBuilderController {
	private TableBuilderView view = new TableBuilderView();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String tableName = "";

    public TitledPane getTableBuilderPane(String tableName) {
        this.tableName = tableName;

        view = new TableBuilderView();
        view.setTable(15, tableName);

        /*this.view.getTable().getSelectionModel().selectedIndexProperty().addListener(
          new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                    System.out.print("change: " + observable);
                    // tableChanged();
                }
            });
        */

        TitledPane titledPane = new TitledPane(
            "BUILD TABLE '" + tableName + "'",
            view.getPane());
        titledPane.setCollapsible(false);
        titledPane.setPrefSize(1100, 700);

        return titledPane;
    }

    public String getSQLQuery() {
        return view.getSQLQuery();
    } 


    public void executeQuery() {
        String SQLQuery = view.getSQLQuery();

        if (SQLQuery != "") {
            boolean isInserted = this.dbUtils.execute(SQLQuery);
            if (isInserted) {
                new Alert(
                    Alert.AlertType.INFORMATION,
                    "Table " + this.tableName + " created.").show();
            } else {
                new Alert(
                    Alert.AlertType.ERROR,
                    "Error: " + this.tableName + " not created.").show();
            }

        } else {
            new Alert(
                Alert.AlertType.ERROR,
                "Error: Can not issue empty query!").show();
        }        
    }

}