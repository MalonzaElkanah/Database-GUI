package com.malone.dbms.desktopSwing.views;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Vector;

public class TableDetailFrame extends JInternalFrame {
    public JTabbedPane tabbedPane = new JTabbedPane();
    private JTextArea SQLPane = new JTextArea();
    public JButton executeQueryButton = new JButton("Execute Query");

    public TableDetailFrame() {
        init();
    }

    public TableDetailFrame(String title) {
        this.setTitle(title);
        init();
    }

    private void init() {
        this.setSize(600,400);
        this.setLocation(10,10);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setResizable(true);

        JSplitPane splitter = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(this.SQLPane), 
            this.tabbedPane
        );
        splitter.setDividerLocation(100);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(splitter, BorderLayout.CENTER);
        this.getContentPane().add(this.executeQueryButton, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void setDescriptionTab(Vector colNames, Vector rowData) {
        JTable descriptionTable = new JTable(rowData, colNames);
        this.tabbedPane.addTab("DESCRIBE TABLE", new JScrollPane(descriptionTable));
    }

    public void setDataTab(Vector colNames, Vector rowData) {
        JTable dataTable = new JTable(rowData, colNames);
        this.tabbedPane.addTab("TABLE DATA", new JScrollPane(dataTable));
    }

    public void setQueryTab(Vector colNames, Vector rowData) {
        JTable queryTable = new JTable(rowData, colNames);
        String tabTitle = "QUERY DATA";
        // getTabCount() // getTitleAt(int index)

        if (this.tabbedPane.indexOfTab(tabTitle) >= 0) {
            this.tabbedPane.removeTabAt(this.tabbedPane.indexOfTab(tabTitle));
        }

        this.tabbedPane.addTab(tabTitle, new JScrollPane(queryTable));
        this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab(tabTitle));
    }

    public String getSQLQuery() {
        return this.SQLPane.getText();
    }
}
