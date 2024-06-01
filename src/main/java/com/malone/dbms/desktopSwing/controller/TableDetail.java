package com.malone.dbms.desktopSwing.controller;

import com.malone.dbms.desktopSwing.views.TableDetailFrame;
import com.malone.dbms.utils.DatabaseUtilities;

import java.util.Vector;

class TableDetail {
    private TableDetailFrame tableDetailFrame = new TableDetailFrame();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();

	TableDetail() { }

	protected TableDetailFrame getTableDetailFrame(String tableName) {
        Vector colNames = this.dbUtils.getColumnNames(tableName);
        Vector metaData = this.dbUtils.describeTable(tableName);
        Vector metaDataColNames = this.dbUtils.getColumnNamesUsingQuery(
            "DESCRIBE " + tableName + ";");
        Vector rowData = this.dbUtils.selectTable(tableName);

        this.tableDetailFrame = new TableDetailFrame("QUERY: " + tableName);
        this.tableDetailFrame.setDescriptionTab(metaDataColNames, metaData);
        this.tableDetailFrame.setDataTab(colNames, rowData);
        this.tableDetailFrame.setQueryTab(colNames, new Vector());
        this.tableDetailFrame.tabbedPane.setSelectedIndex(1);

        this.tableDetailFrame.executeQueryButton.addActionListener(e -> executeQuery());

        return this.tableDetailFrame;
    }

	public void executeQuery() {
        String SQLQuery = this.tableDetailFrame.getSQLQuery();

        Vector colNames = this.dbUtils.getColumnNamesUsingQuery(SQLQuery);
        Vector dataSet = this.dbUtils.executeQuery(SQLQuery);

        this.tableDetailFrame.setQueryTab(colNames, dataSet);
    }

}
