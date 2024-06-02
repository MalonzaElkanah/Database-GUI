package com.malone.dbms.desktopJavaFX.TableDetail;

import com.malone.dbms.desktopJavaFX.utils.ViewUtils;

import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.geometry.Orientation;

import java.util.Vector;

public class TableDetailView {
    private BorderPane root = new BorderPane();
    public TabPane tabPane = new TabPane();
    private TextArea SQLPane = new TextArea();
    public Button executeQueryButton = new Button("Execute Query");

    public TableDetailView() {
        root.setPrefSize(600, 400);
        tabPane.setPrefSize(500, 400);

        ScrollPane scrollPane = new ScrollPane(SQLPane);
        scrollPane.setPrefSize(100, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        SplitPane splitter = new SplitPane(scrollPane, tabPane);
        splitter.setOrientation(Orientation.VERTICAL);
        // splitter.setDividerLocation(100);

        root.setCenter(splitter);
        // root.setBottom(executeQueryButton);
    }

    public Pane getPane() {
        return root;
    }

    public void setDescriptionTab(Vector colNames, Vector dataSet) {
        TableView tableView = ViewUtils.createTable(colNames, dataSet); 

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setPrefSize(400, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Tab tab = new Tab();
        tab.setText("DESCRIBE TABLE");
        tab.setContent(scrollPane);
        tab.setClosable(false);

        tabPane.getTabs().add(tab);
    }

    public void setDataTab(Vector colNames, Vector rowData) {
        TableView table = ViewUtils.createTable(colNames, rowData);

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setPrefSize(400, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Tab tab = new Tab();
        tab.setText("TABLE DATA");
        tab.setContent(scrollPane);
        tab.setClosable(false);

        tabPane.getTabs().add(tab);
    }

    public void setQueryTab(Vector colNames, Vector rowData) {
        TableView queryTable = ViewUtils.createTable(colNames, rowData);
        String tabTitle = "QUERY DATA";

        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText() == tabTitle) {
                tabPane.getTabs().removeAll(tab);
                break;
            }
        }

        ScrollPane scrollPane = new ScrollPane(queryTable);
        scrollPane.setPrefSize(400, 400);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Tab tab = new Tab();
        tab.setText(tabTitle);
        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public String getSQLQuery() {
        return SQLPane.getText();
    }
}
