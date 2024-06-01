package com.malone.dbms.desktopJavaFX.App;

import com.malone.dbms.utils.DatabaseUtilities;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailView;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailController;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.Pane;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.Vector;

public class AppController {
    private AppView view;
	private TableDetailController tableDetail = new TableDetailController();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String selectedDatabase = null;

    public AppController(AppView appView) {
        view = appView;
        view.setDatabaseTree(dbUtils.getDatabaseSystemMap());
        view.setDatabaseComboBox(dbUtils.getDatabases());
        view.setMainContentPanel(getTableDetailPane("schools.class"));

        // Action Listeners
        view.executeQueryButton.setOnAction(a -> {
            tableDetail.executeQuery();
        });
        view.databaseComboBox.setOnAction (a -> {
            String selectDatabase = view.databaseComboBox.getValue();

            if (selectDatabase != selectedDatabase) {
                selectedDatabase = selectDatabase;
                dbUtils.setDatabase(selectDatabase);
            }
        });
        // ObservableList<String> selectedItems = view.selectionModel.getSelectedItems();

        view.selectionModel.selectedItemProperty().addListener(
          new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("Selection changed: " + observable);
            }
        });
    }

    public TableDetailController getTableDetail() {
        return this.tableDetail;
    }

    public Pane getTableDetailPane(String tableName) {
        return this.tableDetail.getTableDetailPane(tableName);
    }

    public String getSelectedDatabase() {
        return this.selectedDatabase;
    }

    public void displayTableDetailFrame(String databaseName, AppView view) {
        Vector tables = this.dbUtils.getTables(databaseName);
        this.selectedDatabase = databaseName;

        if (tables.isEmpty()) {
            new Alert(
                Alert.AlertType.INFORMATION,
                "The " + databaseName + " database has no tables").showAndWait();
        } else {
            ChoiceDialog<String> dialog = new ChoiceDialog(
                tables.get(0),
                tables.toArray());
            dialog.setHeaderText("SELECT TABLE from '" + databaseName + "' Database.");
            dialog.setTitle("Choose table");
            dialog.setContentText("'" + databaseName + "' Tables: ");

            Object table = dialog.showAndWait();
            
            if (table != null) {
                String tableName = table.toString();
                Pane tableDetailView = tableDetail
                    .getTableDetailPane(databaseName + "." + tableName);
                // tableDetailFrame.tabbedPane.setSelectedIndex(1);

                view.setMainContentPanel(tableDetailView);
                view.executeQueryButton.setOnAction(a -> {
                    tableDetail.executeQuery();
                });
            }
        }
    }

}
