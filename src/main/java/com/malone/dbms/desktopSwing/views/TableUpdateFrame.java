package com.malone.dbms.desktopSwing.views;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Vector;

public class TableUpdateFrame extends JInternalFrame {
    private JTable table;
    private JTextArea SQLPane = new JTextArea();
    public JButton updateButton = new JButton("Update Data");

    public TableUpdateFrame() {
        this.init();
    }

    public TableUpdateFrame(String title) {
        this.setTitle(title);
        this.init();
    }

    public void init() {
        this.setSize(600,400);
        this.setLocation(10,10);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setResizable(true);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(updateButton, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void setTableContent(Vector colNames, Vector dataSet) {
        table = createTable(colNames, dataSet);

        JScrollPane sqlScroller = new JScrollPane(SQLPane);
        JScrollPane tableScroller = new JScrollPane(table);

        JSplitPane splitter = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            sqlScroller,
            tableScroller);
        splitter.setDividerLocation(100);

        this.getContentPane().add(splitter, BorderLayout.CENTER);
    }

    protected JTable createTable(Vector colNames, Vector dataSet) {
        int nRows = dataSet.size();
        Vector rowData = new Vector(nRows);

        for (int i = 0; i < nRows; i++) {
            Vector row = (Vector) dataSet.elementAt(i);
            Vector data = new Vector(row.size());

            for (int j = 0; j < row.size(); j++) {
                try {
                    data.add(j, (row.elementAt(j)).toString());
                } catch (NullPointerException npe) {
                    data.add(j, " ");
                }
            }

            rowData.add(i, data);
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
