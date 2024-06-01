package com.malone.dbms.desktopSwing.views;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Vector;

public class TableInsertFrame extends JInternalFrame {
    private JTable table;
    private JTextArea SQLPane = new JTextArea();
    public JButton insertButton = new JButton("Insert Data");

    public TableInsertFrame() {
        this.init();
    }

    public TableInsertFrame(String title) {
        this.setTitle(title);
        this.init();
    }

    public void init() {
        this.setSize(600, 400);
        this.setLocation(10, 10);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setResizable(true);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(insertButton, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void setTableContent(Vector colNames, int rowNum) {
        table = createTable(colNames, rowNum);

        JScrollPane sqlScroller = new JScrollPane(SQLPane);
        JScrollPane tableScroller = new JScrollPane(table);

        JSplitPane splitter = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            sqlScroller,
            tableScroller);
        splitter.setDividerLocation(100);

        this.getContentPane().add(splitter, BorderLayout.CENTER);
    }

    private JTable createTable(Vector colNames, int nRows) {
        // String[][] rowData = new String[nRows][colNames.size()];
        Vector rowData = new Vector(nRows);

        for (int i = 0; i < nRows; i++) {
            Vector colData = new Vector(colNames.size());

            for (int j = 0; j < colNames.size(); j++) {
                colData.add(j, "");
            }

            rowData.add(i, colData);
        }

        JTable table = new JTable(rowData, colNames);

        return table;
    }

    public JTable getTable() {
        return this.table;
    }

    public JTextArea getSQLPane() {
        return this.SQLPane;
    }

    public String getSQLQuery() {
        return this.SQLPane.getText();
    }
}
