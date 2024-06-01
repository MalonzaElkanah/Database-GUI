package com.malone.dbms.desktopSwing.views;

import javax.swing.*;
import java.awt.BorderLayout;
// import java.util.Vector;

public class TableBuilderFrame extends JInternalFrame {
    private JTable table;
    private JTextArea SQLPane = new JTextArea();
    public JButton createButton = new JButton("Create Table");

	public TableBuilderFrame() {
		this.init();
	}

	public TableBuilderFrame(String title) {
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
        this.getContentPane().add(createButton, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void setTableContent(int rowNum) {
        table = createTable(rowNum);

        JScrollPane sqlScroller = new JScrollPane(SQLPane);
        JScrollPane tableScroller = new JScrollPane(table);

        JSplitPane splitter = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            sqlScroller,
            tableScroller);
        splitter.setDividerLocation(100);

        this.getContentPane().add(splitter, BorderLayout.CENTER);
    }

    private JTable createTable(int nRows) {
        String[] dataTypes = {"CHAR","VARCHAR","INT","FLOAT","DATE"};
        String[] defNull = {"","NULL","NOT NULL"};
        String[] defUnique = {"","UNIQUE"};
        String[] defPriKey = {"","PRIMARY KEY"};
        String[] colNames = {"ColumnName","DataType","SIZE","NULL","UNIQUE","PRIMARY KEY"};
        String[][] rowData = new String[nRows][colNames.length];

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < colNames.length; j++) {
                rowData[i][j] = "";
            }
        }

        JComboBox dTypes = new JComboBox(dataTypes);
        JComboBox nullDefs = new JComboBox(defNull);
        JComboBox uniqueDefs = new JComboBox(defUnique);
        JComboBox primaryKDefs = new JComboBox(defPriKey);

        JTable table = new JTable(rowData, colNames);
        table.getColumnModel().getColumn(1).
                setCellEditor(new DefaultCellEditor(dTypes));
        table.getColumnModel().getColumn(3).
                setCellEditor(new DefaultCellEditor(nullDefs));
        table.getColumnModel().getColumn(4).
                setCellEditor(new DefaultCellEditor(uniqueDefs));
        table.getColumnModel().getColumn(5).
                setCellEditor(new DefaultCellEditor(primaryKDefs));
        
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
