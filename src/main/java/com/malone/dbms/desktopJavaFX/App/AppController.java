package com.malone.dbms.desktopJavaFX.App;

import com.malone.dbms.utils.DatabaseUtilities;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailView;
import com.malone.dbms.desktopJavaFX.TableDetail.TableDetailController;
import com.malone.dbms.desktopJavaFX.TableInsert.TableInsertController;
import com.malone.dbms.desktopJavaFX.TableUpdate.TableUpdateController;
import com.malone.dbms.desktopJavaFX.TableBuilder.TableBuilderController;

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
    private TableInsertController tableInsert = new TableInsertController();
    private TableUpdateController tableUpdate = new TableUpdateController();
    private TableBuilderController tableBuilder = new TableBuilderController();
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
        view.selectTableMenuItem.setOnAction(a -> displayTableDetailView());
        
        view.insertTableMenuItem.setOnAction(a -> displayTableInsertView());
        view.updateTableMenuItem.setOnAction(a -> displayTableUpdateView());
        view.deleteRowMenuItem.setOnAction(a -> {});
        
        view.newTableMenuItem.setOnAction(a -> displayBuilderView());
        view.dropTableMenuItem.setOnAction(a -> dropTable());
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

    public void displayTableDetailView() {
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

    public void displayTableInsertView() {
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
                        view.setMainContentPanel(
                            tableInsert.getTableInsertPane(
                            databaseName + "." + tableName));

                        view.executeQueryButton.setOnAction(a -> {
                            // tableInsert.executeQuery();
                            String[] SQLQueries = tableInsert.getSQLCommands();

                            if (SQLQueries.length > 0) {
                                boolean isInserted = this.dbUtils.execute(SQLQueries);
                                if (isInserted) {
                                    new Alert(
                                        Alert.AlertType.INFORMATION,
                                        "Data Inserted to " + tableName).show();

                                    view.setMainContentPanel(tableDetail.getTableDetailPane(
                                        databaseName + "." + tableName));

                                    tableDetail.setSelectedTab(1);

                                    initActionListeners();
                                } else {
                                    new Alert(
                                        Alert.AlertType.ERROR,
                                        "Error: Data not inserted to " + tableName).show();
                                }

                            } else {
                                new Alert(
                                    Alert.AlertType.ERROR,
                                    "Error: Can not issue empty query!").show();
                            }
                        });
                    }
                });
            }
        }
    }

    public void displayTableUpdateView() {
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
                        view.setMainContentPanel(
                            tableUpdate.getTableUpdatePane(
                            databaseName + "." + tableName));
                        view.executeQueryButton.setOnAction(a -> {
                            // tableUpdate.executeQuery();
                            String[] SQLQueries = tableUpdate.getSQLCommands();

                            if (SQLQueries.length > 0) {
                                boolean isUpdated = this.dbUtils.execute(SQLQueries);
                                if (isUpdated) {
                                    new Alert(
                                        Alert.AlertType.INFORMATION,
                                        "Data in Table " + tableName + " Updated.").show();
                                    view.setMainContentPanel(tableDetail.getTableDetailPane(
                                        databaseName + "." + tableName));

                                    tableDetail.setSelectedTab(1);

                                    initActionListeners();
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
                        });
                    }
                });
            }
        }
    }

    public void displayBuilderView() {
        if (selectedDatabase == null) {
            selectDatabase();
        } 
        String databaseName = selectedDatabase;
        
        if (databaseName != null ) {
            TextInputDialog dialog = new TextInputDialog();
            // dialog.setHeaderText("SELECT TABLE from '" + databaseName + "' Database.");
            dialog.setTitle("New " + databaseName + " Table.");
            dialog.setContentText("New Table:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(tableName -> {
                if (tableName != "") {
                    Vector tables = dbUtils.getTables(databaseName);

                    if (tables.contains(tableName)) {
                        new Alert(
                            Alert.AlertType.INFORMATION,
                            "The '" + tableName + "' table already exists in " + databaseName).show();
                    } else {
                        view.setMainContentPanel(
                            tableBuilder.getTableBuilderPane(
                                databaseName + "." + tableName));
                        view.executeQueryButton.setOnAction(a -> {
                            // tableBuilder.executeQuery();

                            String SQLQuery = tableBuilder.getSQLQuery();

                            if (SQLQuery != "") {
                                boolean isInserted = this.dbUtils.execute(SQLQuery);
                                if (isInserted) {
                                    new Alert(
                                        Alert.AlertType.INFORMATION,
                                        "Table " + tableName + " created.").show();
                                    refreshDatabase();

                                    view.setMainContentPanel(tableDetail.getTableDetailPane(
                                        databaseName + "." + tableName));
                                    initActionListeners();
                                } else {
                                    new Alert(
                                        Alert.AlertType.ERROR,
                                        "Error: " + tableName + " not created.").show();
                                }

                            } else {
                                new Alert(
                                    Alert.AlertType.ERROR,
                                    "Error: Can not issue empty query!").show();
                            }

                        });
                    }
                }
            });
        }
    }

    public void dropTable() {
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
                dialog.setHeaderText("DROP TABLE from '" + databaseName + "' Database.");
                dialog.setTitle("Drop table");
                dialog.setContentText("'" + databaseName + "' Tables: ");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(tableName -> {
                    if (tableName != "") {
                        Alert alert = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to DROP " + tableName + " table?");
                        alert.showAndWait()
                            .filter(response -> response == ButtonType.OK)
                            .ifPresent(response -> {
                                Vector res = this.dbUtils.dropTable(tableName);

                                if (Boolean.parseBoolean(res.firstElement().toString())) {
                                    new Alert(
                                        Alert.AlertType.INFORMATION,
                                        "TABLE " + tableName + " DROPPED!").show();

                                    refreshDatabase();
                                } else {
                                    this.reportException(res.lastElement().toString());
                                }
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
