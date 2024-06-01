package com.malone.dbms.desktopSwing.controller;

import com.malone.dbms.desktopSwing.views.TableBuilderFrame;
import com.malone.dbms.utils.DatabaseUtilities;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.util.Vector;

public class TableBuilder {
    private TableBuilderFrame tableBuilderFrame = new TableBuilderFrame();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String tableName = "";
    private String SQLCommand = null;

	public TableBuilder() { }

	protected TableBuilderFrame getTableBuilderFrame(String tableName) {
        TableChangeListener modelListener = new TableChangeListener();

        this.tableName = tableName;

        this.tableBuilderFrame = new TableBuilderFrame("CREATE TABLE " + tableName);
        this.tableBuilderFrame.setTableContent(15);
        this.tableBuilderFrame.getTable().getModel().addTableModelListener(modelListener);

        this.tableBuilderFrame.createButton.addActionListener(e -> executeQuery());

        return this.tableBuilderFrame;
    }

    public void executeQuery() {
        // String SQLCommand = this.tableInsertFrame.getSQLQuery();
        boolean isCreated = this.dbUtils.execute(SQLCommand);

        if (isCreated) {
            JOptionPane.showMessageDialog(
                null,
                "Table " + this.tableName + " created.");
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Error: " + this.tableName + " not created.");
        }
    }

    public String parseTable() {
        String tableValues = "";

        int rows = this.tableBuilderFrame.getTable().getRowCount();
        int cols = this.tableBuilderFrame.getTable().getColumnCount();

        if (rows >= 0 && cols >= 0) {
            tableValues += "\n( ";

            for (int i = 0; i < rows; i++) {
                String rowData = "";

                for (int j = 0; j < cols; j++) {
                    String field = (String)this.tableBuilderFrame.getTable().getValueAt(i, j);

                    if (field != null) {
                        if (field.length() == 0) break;

                        if (j == 2) rowData += "(";
                        else if (i > 0 || j > 0) rowData += " ";

                        rowData += field;

                        if (j == 2) rowData+=")";
                    }
                }

                if (rowData.length()==0) break;

                tableValues += rowData+",\n";
            }
        }

        if (tableValues.endsWith(",\n")) {
            int tvLen = tableValues.length() - 2;
            if (tvLen > 0) tableValues = tableValues.substring(0, tvLen);
        }

        tableValues += " );";

        return tableValues;
    }

    class TableChangeListener implements TableModelListener {
        // Listener for Edit events on the JTable
        public TableChangeListener (){
        }
        public void tableChanged(TableModelEvent event){
            String SQLCommandRoot = "CREATE TABLE "+tableName;
            SQLCommand = SQLCommandRoot+parseTable();
            tableBuilderFrame.getSQLPane().setText(SQLCommand);
        }
    }
}
