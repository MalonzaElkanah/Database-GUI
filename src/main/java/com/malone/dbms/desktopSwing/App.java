package com.malone.dbms.desktopSwing;

import com.malone.dbms.desktopSwing.views.AppFrame;
import com.malone.dbms.desktopSwing.controller.AppController;
import com.malone.dbms.utils.DatabaseUtilities;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.event.ItemEvent;

public class App {
    AppFrame view = new AppFrame("Database Management System");
    AppController controller = new AppController();
    DatabaseUtilities dbUtils = new DatabaseUtilities();

    public App() {
        this.view.setDatabaseTree(this.dbUtils.getDatabaseSystemMap());
        this.view.setMainContentPanel(
            this.controller.getTableDetailFrame("schools.class"));
        
        // setUp comboBox
        this.view.setDatabaseComboBox(this.dbUtils.getDatabases());

        this.view.setVisible(true);

        // Action Listeners
        this.view.newDbMenuItem.addActionListener(e -> {
            this.controller.createDatabase();
            this.refreshDatabaseTree();
            this.view.setDatabaseComboBox(this.dbUtils.getDatabases());
            this.view.databaseComboBox.setSelectedItem(this.controller.getSelectedDatabase());
        });
        this.view.selectDbMenuItem.addActionListener(e -> {
            this.controller.selectDatabase();
            this.view.databaseComboBox.setSelectedItem(this.controller.getSelectedDatabase());
        });
        this.view.dropDbMenuItem.addActionListener(e -> {
            this.controller.dropDatabase();
            this.refreshDatabaseTree();
            this.view.setDatabaseComboBox(this.dbUtils.getDatabases());
        });

        this.view.selectTableMenuItem.addActionListener(e -> {
            if (this.controller.getSelectedDatabase() == null) {
                this.controller.selectDatabase();
            }

            this.controller.displayTableDetailFrame(
                this.controller.getSelectedDatabase(),
                this.view);
        });
        this.view.insertTableMenuItem.addActionListener(e -> {
            if (this.controller.getSelectedDatabase() == null) {
                this.controller.selectDatabase();
            }
            
            this.controller.displayTableInsertFrame(
                this.controller.getSelectedDatabase(),
                this.view);
        });
        this.view.updateTableMenuItem.addActionListener(e -> {
            if (this.controller.getSelectedDatabase() == null) {
                this.controller.selectDatabase();
            }
            
            this.controller.displayTableUpdateFrame(
                this.controller.getSelectedDatabase(),
                this.view);
        });
        this.view.deleteRowMenuItem.addActionListener(e -> {});
        this.view.newTableMenuItem.addActionListener(e -> {
            if (this.controller.getSelectedDatabase() == null) {
                this.controller.selectDatabase();
            }
            
            this.controller.displayTableBuilderFrame(
                this.controller.getSelectedDatabase(),
                this.view);
            this.refreshDatabaseTree();
        });
        this.view.dropTableMenuItem.addActionListener(e -> {
            if (this.controller.getSelectedDatabase() == null) {
                this.controller.selectDatabase();
            }
            
            this.controller.dropTable(this.controller.getSelectedDatabase());
            this.refreshDatabaseTree();
        });

        this.view.databaseComboBox.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectDatabase = this.view.databaseComboBox.getSelectedItem().toString();

                if (selectDatabase != this.controller.getSelectedDatabase()) {
                    this.dbUtils.setDatabase(selectDatabase);
                }
                
            }
        });

        this.view.refreshButton.addActionListener(e -> refreshDatabaseTree());
        this.view.databaseTree.addTreeSelectionListener((TreeSelectionEvent event) -> {
            TreePath[] paths = event.getPaths();
            for (TreePath path : paths) {
                if (this.view.selectionModel.isPathSelected(path)) { 
                    this.controller.setDatabaseTreeSelection(path, this.view);
                }
            }
        });
    }

    public void refreshDatabaseTree() {
        this.view.setDatabaseTree(this.dbUtils.getDatabaseSystemMap());
        this.view.databaseTree.addTreeSelectionListener((TreeSelectionEvent event) -> {
            TreePath[] paths = event.getPaths();
            for (TreePath path : paths) {
                if (this.view.selectionModel.isPathSelected(path)) { 
                    this.controller.setDatabaseTreeSelection(path, this.view);
                }
            }
        });
    }

    public static void main( String[] args ) {
        System.out.println("Swing Application Initializing...!");
        new App();
    }
}
