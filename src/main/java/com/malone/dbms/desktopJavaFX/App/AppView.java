package com.malone.dbms.desktopJavaFX.App;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SelectionMode;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

public class AppView {
    private BorderPane root = new BorderPane();
    private HBox statusbar = new HBox();

    private ToolBar toolBar = new ToolBar();
    protected ComboBox<String> databaseComboBox = new ComboBox();
    protected Button refreshButton = new Button("Refresh Database");
    protected Button executeQueryButton = new Button("Execute Query");
    private TreeView databaseTree = new TreeView();
    protected TreeItem databaseRootNode = new TreeItem("Databases");
    protected SelectionModel<TreeItem> selectionModel;

    protected MenuItem newDbMenuItem = new MenuItem("New DataBase");
    protected MenuItem selectDbMenuItem = new MenuItem("Select DataBase");
    protected MenuItem dropDbMenuItem = new MenuItem("Drop DataBase");

    protected MenuItem selectTableMenuItem = new MenuItem("Select Table");
    protected MenuItem insertTableMenuItem = new MenuItem("Insert Into Table");
    protected MenuItem updateTableMenuItem = new MenuItem("Update VALUES");
    protected MenuItem deleteRowMenuItem = new MenuItem("Delete Row");
    protected MenuItem newTableMenuItem = new MenuItem("New Table");
    protected MenuItem dropTableMenuItem = new MenuItem("Drop Table");

    public AppView() {
        root.setPrefSize(1450, 730);
        // - setTop
        root.setTop(headerPanel());
        // - setBottom,
        root.setBottom(statusbar);
        // - setLeft, 
        // - setRight and 
        // - setCenter
    }

    public Pane getRoot() {
        return root;
    }
    private void setMenuBar() {

    }

    private void setToolBar() {
        databaseComboBox.getItems().addAll("<none selected>", " ");
        databaseComboBox.setValue("<Select Database>");
        Label comboLabel = new Label("Database:");
        HBox pane = new HBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(comboLabel, databaseComboBox);

        toolBar.getItems().addAll(/*new Separator(),*/ pane, /*new Separator(),*/ 
            refreshButton, executeQueryButton);
        toolBar.setPadding(new Insets(10, 10, 10, 10));
    }

    private Pane headerPanel() {
        // Set Menubar
        Menu databaseMenu = new Menu("Database");
        Menu tableMenu = new Menu("Table");
        databaseMenu.getItems().addAll(
            newDbMenuItem,
            selectDbMenuItem,
            dropDbMenuItem
        );

        tableMenu.getItems().addAll(
            selectTableMenuItem,
            new SeparatorMenuItem(),
            insertTableMenuItem,
            updateTableMenuItem,
            deleteRowMenuItem,
            new SeparatorMenuItem(),
            newTableMenuItem,
            dropTableMenuItem
        );

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(databaseMenu, tableMenu);

        // Set Toolbar
        databaseComboBox.setValue("<Select Database>");
        toolBar.getItems().addAll(
            new Separator(),
            databaseComboBox,
            new Separator(),
            refreshButton,
            executeQueryButton);
        toolBar.setPadding(new Insets(10, 10, 10, 10));

        VBox headerPane = new VBox(menuBar);
        headerPane.setPadding(new Insets(0, 0, 10, 0));
        // headerPane.setAlignment(Pos.CENTER);
        headerPane.getChildren().addAll(toolBar);

        return headerPane;
    }

    public void setMainContentPanel(TitledPane pane) {
        TitledPane titledPane = new TitledPane("QUERY DATABASE", pane);
        titledPane.setCollapsible(false);
        titledPane.setPrefSize(1100, 700);
        titledPane.setPadding(new Insets(0, 0, 10, 0));
        root.setCenter(pane);
    }

    public void setDatabaseTree(Hashtable<String, Vector> dataMap) {
        databaseRootNode = new TreeItem("Databases");
        databaseRootNode.setExpanded(true);

        for (Enumeration <String> e = dataMap.keys(); e.hasMoreElements();) {
            String key = e.nextElement();
            TreeItem databaseNode = new TreeItem(key);

            Vector tables = dataMap.get(key);

            int tableNum = 0;

            for (Object table : tables) {
                TreeItem tableNode = new TreeItem(table.toString());
                databaseNode.getChildren().add(tableNum, tableNode);
                tableNum++;
            }

            databaseRootNode.getChildren().add(0, databaseNode);
        }

        databaseTree = new TreeView(databaseRootNode);
        databaseTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectionModel = databaseTree.getSelectionModel();

        ScrollPane scrollPane = new ScrollPane(databaseTree);
        scrollPane.setPrefSize(300, 700);
        scrollPane.setFitToWidth(true); //Adapt the content to the width of ScrollPane
        scrollPane.setFitToHeight(true); //Adapt the content to the height of ScrollPane
         //Control the visibility of the Horizontal &  Vertical ScrollBar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        BorderPane pane = new BorderPane();
        pane.setCenter(scrollPane);
        root.setLeft(pane);
    }

    public void setDatabaseComboBox(Vector<String> items) {
        databaseComboBox.getItems().setAll(items);
    }

}
