package com.malone.dbms.desktopSwing.controller;

import com.malone.dbms.desktopSwing.views.AppFrame;
import com.malone.dbms.desktopSwing.views.TableDetailFrame;
import com.malone.dbms.desktopSwing.views.TableInsertFrame;
import com.malone.dbms.desktopSwing.views.TableUpdateFrame;
import com.malone.dbms.desktopSwing.views.TableBuilderFrame;
import com.malone.dbms.utils.DatabaseUtilities;

import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.tree.TreePath;

import java.util.Vector;

public class AppController {
    // TableDetailFrame tableDetailFrame = new TableDetailFrame();
    private TableDetail tableDetail = new TableDetail();
    private TableInsert tableInsert = new TableInsert();
    private TableUpdate tableUpdate = new TableUpdate();
    private TableBuilder tableBuilder = new TableBuilder();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String selectedDatabase = null;

    public TableDetail getTableDetail() {
        return this.tableDetail;
    }

    public TableDetailFrame getTableDetailFrame(String tableName) {
        return this.tableDetail.getTableDetailFrame(tableName);
    }

    public String getSelectedDatabase() {
        return this.selectedDatabase;
    }

    public void createDatabase() {
        String newDbName = JOptionPane.showInputDialog(
            null,
            "Database:",
            "New Database",
            JOptionPane.QUESTION_MESSAGE);

        if (newDbName != null) {
            Vector response = this.dbUtils.createDatabase(newDbName);

            if (Boolean.parseBoolean(response.firstElement().toString())) {
                JOptionPane.showMessageDialog(null, "DATABASE " + newDbName + " CREATED");
                this.selectedDatabase = newDbName;
                // this.updateDatabaseTreeNodes();
                // this.databaseComboBox.addItem(newDbName);
                // this.databaseComboBox.setSelectedItem(newDbName);
            } else {
                JOptionPane.showMessageDialog(null, response.lastElement().toString());
            }
        }
    }

    public void selectDatabase() {
        String database = JOptionPane.showInputDialog(
            null,
            "Database:",
            "Select database",
            JOptionPane.QUESTION_MESSAGE);

        if (database != null) {
            Vector response = this.dbUtils.setDatabase(database);

            if (Boolean.parseBoolean(response.firstElement().toString())) {
                // this.databaseComboBox.setSelectedItem(database);
                this.selectedDatabase = database;
            } else {
                JOptionPane.showMessageDialog(null, response.lastElement());
            }
        }
    }

    public void dropDatabase() {
        String deleteDbName = JOptionPane.showInputDialog(
            null,
            "Database:",
            "Select database",
            JOptionPane.QUESTION_MESSAGE);

        if (deleteDbName != null) {
            int option = JOptionPane.showConfirmDialog(
                null,
                "Dropping database " + deleteDbName,
                "DELETE Database " + deleteDbName,
                JOptionPane.OK_CANCEL_OPTION);

            if (option == 0) {
                Vector response = this.dbUtils.dropDatabase(deleteDbName);

                if (Boolean.parseBoolean(response.firstElement().toString())) {
                    JOptionPane.showMessageDialog(null, "DATABASE " + deleteDbName + " DROPPED.");
                    // this.updateDatabaseTreeNodes();
                    // this.databaseComboBox.removeItem(deleteDbName);
                } else {
                    this.reportException(response.lastElement().toString());
                }
            }
        }
    }

    public void displayTableDetailFrame(String databaseName, AppFrame view) {
        Vector tables = this.dbUtils.getTables(databaseName);
        this.selectedDatabase = databaseName;

        if (tables.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "The " + databaseName + " database has no tables");
        } else {
            Object table = JOptionPane.showInputDialog(
                null,
                "'" + databaseName + "' Tables:",
                "SELECT TABLE from '" + databaseName + "' Database.",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                tables.toArray(),
                tables.get(0)
            );
            
            if (table != null) {
                String tableName = table.toString();
                TableDetailFrame tableDetailFrame = this.tableDetail
                    .getTableDetailFrame(databaseName + "." + tableName);
                tableDetailFrame.tabbedPane.setSelectedIndex(1);

                view.setMainContentPanel(tableDetailFrame);
            }
        }
    }

    public void displayTableInsertFrame(String databaseName, AppFrame view) {
        Vector tables = this.dbUtils.getTables(databaseName);
        this.selectedDatabase = databaseName;

        if (tables.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "The " + databaseName + " database has no tables");
        } else {
            Object table = JOptionPane.showInputDialog(
                null,
                "'" + databaseName + "' Tables:",
                "SELECT TABLE from '" + databaseName + "' Database.",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                tables.toArray(),
                tables.get(0)
            );
            
            if (table != null) {
                String tableName = table.toString();
                TableInsertFrame tableInsertFrame = this.tableInsert
                    .getTableInsertFrame(databaseName + "." + tableName);

                view.setMainContentPanel(tableInsertFrame);
            }
        }
    }

    public void displayTableUpdateFrame(String databaseName, AppFrame view) {
        Vector tables = this.dbUtils.getTables(databaseName);
        this.selectedDatabase = databaseName;

        if (tables.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "The " + databaseName + " database has no tables");
        } else {
            Object table = JOptionPane.showInputDialog(
                null,
                "'" + databaseName + "' Tables:",
                "SELECT TABLE from '" + databaseName + "' Database.",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                tables.toArray(),
                tables.get(0)
            );
            
            if (table != null) {
                String tableName = table.toString();
                TableUpdateFrame tableUpdateFrame = this.tableUpdate
                    .getTableUpdateFrame(databaseName + "." + tableName);

                view.setMainContentPanel(tableUpdateFrame);
            }
        }
    }

    public void displayTableBuilderFrame(String databaseName, AppFrame view) {
        String newTable = JOptionPane.showInputDialog(
            null,
            "Table Name:",
            "New " + databaseName + " database Table.",
            JOptionPane.QUESTION_MESSAGE);

        Vector tables = this.dbUtils.getTables(databaseName);

        if (newTable != null && tables.contains(newTable)) {
            JOptionPane.showMessageDialog(
                null,
                "Error: Table " + newTable + " already exists!");
        } else {
            TableBuilderFrame tableBuilderFrame = this.tableBuilder
                    .getTableBuilderFrame(databaseName + "." + newTable);

            view.setMainContentPanel(tableBuilderFrame);
        }
    }

    public void dropTable(String databaseName) {
        // String databaseName = this.databaseComboBox.getSelectedItem().toString();
        Vector tables = this.dbUtils.getTables(databaseName);
        this.selectedDatabase = databaseName;

        if (tables.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "The " + databaseName + " database has no tables");
        } else {

            Object table = JOptionPane.showInputDialog(
                null,
                "'" + databaseName + "' Tables:",
                "SELECT TABLE from '" + databaseName + "' Database.",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                tables.toArray(),
                tables.get(0));
            
            if (table != null) {
                int option = JOptionPane.showConfirmDialog(
                    null,
                    "Dropping table " + table.toString(),
                    "Database " + databaseName,
                    JOptionPane.OK_CANCEL_OPTION);

                if (option == 0) {
                    Vector response = this.dbUtils.dropTable(table.toString());

                    if (Boolean.parseBoolean(response.firstElement().toString())) {
                        JOptionPane.showMessageDialog(
                            null,
                            "TABLE " + table.toString() + " DROPPED!");

                        // this.updateDatabaseTreeNodes();
                    } else {
                        this.reportException(response.lastElement().toString());
                    }
                }
            }
        }
    }

    public void setDatabaseTreeSelection(TreePath path, AppFrame view) {
        Object[] paths = path.getPath();

        if (paths.length > 1) {
            String database = paths[1].toString();
            String databaseTable = path.getLastPathComponent().toString();
            System.out.println("=> Database: " + database);

            Vector response = this.dbUtils.setDatabase(database);

            if (Boolean.parseBoolean(response.firstElement().toString())) {
                this.selectedDatabase = database;

                if (paths.length > 2) {
                    System.out.println("=> => Table: " + databaseTable);

                    TableDetailFrame tableDetailFrame = this.tableDetail
                        .getTableDetailFrame(database + "." + databaseTable);
                    tableDetailFrame.tabbedPane.setSelectedIndex(0);

                    view.setMainContentPanel(tableDetailFrame);
                }
            } else {
                System.err.println(response.lastElement().toString());
            }
        }
    }

    public void reportException(String exception) {
        JOptionPane.showMessageDialog(null, ""+exception);
        System.err.println(exception);
    }
}

