package com.malone.dbms.desktopJavaFX.App;

import com.malone.dbms.utils.DatabaseUtilities;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailView;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailController;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ButtonType;
// import javafx.scene.layout.Pane;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;



import java.util.Vector;
import java.util.Optional;

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

        initActionListeners();
    }

    private void initActionListeners() {
        // Action Listeners
        view.executeQueryButton.setOnAction(a -> {
            tableDetail.executeQuery();
        });
        view.databaseComboBox.setOnAction (a -> {
            String selectDatabase = view.databaseComboBox.getValue();

            if (selectDatabase != selectedDatabase || selectDatabase != "null") {
                selectedDatabase = selectDatabase;
                dbUtils.setDatabase(selectDatabase);
            }
        });

        view.refreshButton.setOnAction(a -> {
            // new AppController(appView);
            view.setDatabaseTree(dbUtils.getDatabaseSystemMap());
            view.setDatabaseComboBox(dbUtils.getDatabases());
            view.setMainContentPanel(getTableDetailPane("schools.class"));

            initActionListeners();
        });

        view.selectionModel.selectedIndexProperty().addListener(
          new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                TreeItem selectedTreeItem = view.selectionModel.getSelectedItem();

                if (selectedTreeItem != view.databaseRootNode) {
                    if (selectedTreeItem.getParent() != view.databaseRootNode) {
                        // System.out.println("Database Node: " + selectedTreeItem.getValue());
                        // setDatabase(selectedTreeItem.getValue().toString());
                        if (selectedTreeItem.getParent().getParent() == view.databaseRootNode) {
                            // System.out.println("Table Node: " + selectedTreeItem.getValue());
                            setDatabase(selectedTreeItem.getParent().getValue().toString());
                            view.setMainContentPanel(getTableDetailPane(
                                selectedTreeItem.getParent().getValue().toString() + "." +
                                selectedTreeItem.getValue().toString()
                            ));
                        }
                    }
                }
            }
        });

        // Database MenuItem listeners
        view.newDbMenuItem.setOnAction(a -> createDatabase());
        view.selectDbMenuItem.setOnAction(a -> selectDatabase());
        view.dropDbMenuItem.setOnAction(a -> dropDatabase());

        // Table MenuItems Listeners
        view.selectTableMenuItem.setOnAction(a -> displayTableDetailFrame());
        
        view.insertTableMenuItem.setOnAction(a -> {});
        view.updateTableMenuItem.setOnAction(a -> {});
        view.deleteRowMenuItem.setOnAction(a -> {});
        
        view.newTableMenuItem.setOnAction(a -> {});
        view.dropTableMenuItem.setOnAction(a -> {});
    }

    private void refreshDatabase() {
        view.setDatabaseTree(dbUtils.getDatabaseSystemMap());
        view.setDatabaseComboBox(dbUtils.getDatabases());
        // view.setMainContentPanel(getTableDetailPane("schools.class"));

        initActionListeners();
    }

    private void setDatabase(String databaseName) {
        String selectDatabase = view.databaseComboBox.getValue();

        if (databaseName != selectedDatabase) {
            selectedDatabase = databaseName;
            dbUtils.setDatabase(databaseName);
            view.databaseComboBox.setValue(databaseName);
        } else if (databaseName != selectDatabase) {
            view.databaseComboBox.setValue(databaseName);
        }
    }

    public String getDatabase() {
        return this.selectedDatabase;
    }

    public void createDatabase() {
        TextInputDialog dialog = new TextInputDialog();
        // dialog.setHeaderText("SELECT TABLE from '" + databaseName + "' Database.");
        dialog.setTitle("New Database");
        dialog.setContentText("Database:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newDbName -> {
            if (newDbName != "") {
                Vector response = this.dbUtils.createDatabase(newDbName);

                if (Boolean.parseBoolean(response.firstElement().toString())) {
                    new Alert(
                        Alert.AlertType.INFORMATION,
                        "DATABASE " + newDbName + " CREATED").show();

                    refreshDatabase();
                    setDatabase(newDbName);
                } else {
                    reportException(response.lastElement().toString());
                }
            }
        });
    }

    public void selectDatabase() {
        Vector<String> databases = dbUtils.getDatabases();
        ChoiceDialog<String> dialog = new ChoiceDialog(
            databases.get(0),
            databases.toArray());
        dialog.setHeaderText("SELECT Database.");
        dialog.setTitle("Choose Database");
        dialog.setContentText("Databases: ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(database -> {
            if (database != "") {
                Vector response = this.dbUtils.setDatabase(database);

                if (Boolean.parseBoolean(response.firstElement().toString())) {
                    setDatabase(database);
                } else {
                    reportException(response.lastElement().toString());
                }
            }
        });
    }

    public void dropDatabase() {
        Vector<String> databases = dbUtils.getDatabases();
        ChoiceDialog<String> dialog = new ChoiceDialog(
            databases.get(0),
            databases.toArray());
        dialog.setHeaderText("Select Database to DELETE.");
        dialog.setTitle("Choose Database");
        dialog.setContentText("Databases: ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(database -> {
            if (database != "") {
                // TODO: Confirm delete dialog
                Vector response = this.dbUtils.dropDatabase(database);

                if (Boolean.parseBoolean(response.firstElement().toString())) {
                    new Alert(
                        Alert.AlertType.INFORMATION, "DATABASE " + database + " DROPPED.").show();
                    refreshDatabase();
                } else {
                    reportException(response.lastElement().toString());
                }
            }
        });
    }

    public TableDetailController getTableDetail() {
        return this.tableDetail;
    }

    public TitledPane getTableDetailPane(String tableName) {
        return this.tableDetail.getTableDetailPane(tableName);
    }

    public void displayTableDetailFrame() {
        if (selectedDatabase == null) {
            selectDatabase();
        } 
        String databaseName = selectedDatabase;
        
        if (databaseName != null ) {
            Vector tables = dbUtils.getTables(databaseName);

            if (tables.isEmpty()) {
                new Alert(
                    Alert.AlertType.INFORMATION,
                    "The " + databaseName + " database has no tables").show();
            } else {
                ChoiceDialog<String> dialog = new ChoiceDialog(
                    tables.get(0),
                    tables.toArray());
                dialog.setHeaderText("SELECT TABLE from '" + databaseName + "' Database.");
                dialog.setTitle("Choose table");
                dialog.setContentText("'" + databaseName + "' Tables: ");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(tableName -> {
                    if (tableName != "") {
                        view.setMainContentPanel(tableDetail.getTableDetailPane(
                            databaseName + "." + tableName));
                        view.executeQueryButton.setOnAction(a -> {
                            tableDetail.executeQuery();
                        });
                    }
                });
            }
        }
    }

    public void reportException(String exception) {
        new Alert(Alert.AlertType.ERROR, exception).show();
        System.err.println(exception);
    }

}
