package com.malone.dbms.desktopSwing.controller;

import com.malone.dbms.desktopSwing.views.TableInsertFrame;
import com.malone.dbms.utils.DatabaseUtilities;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.util.Vector;

public class TableInsert {
    private TableInsertFrame tableInsertFrame = new TableInsertFrame();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private String tableName = "";
    private String SQLCommand[] = null;

	public TableInsert() { }

	protected TableInsertFrame getTableInsertFrame(String tableName) {
        Vector colNames = this.dbUtils.getColumnNames(tableName);
        TableChangeListener modelListener = new TableChangeListener();

        this.tableName = tableName;

        this.tableInsertFrame = new TableInsertFrame("INSERT INTO " + tableName);
        this.tableInsertFrame.setTableContent(colNames, 15);
        this.tableInsertFrame.getTable().getModel().addTableModelListener(modelListener);

        this.tableInsertFrame.insertButton.addActionListener(e -> executeQuery());

        return this.tableInsertFrame;
    }

	public void executeQuery() {
        // String SQLCommand = this.tableInsertFrame.getSQLQuery();
        boolean isInserted = this.dbUtils.execute(SQLCommand);

        if (isInserted) {
            JOptionPane.showMessageDialog(
                null,
                "Data Inserted to " + this.tableName);
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Error: Data not inserted to " + this.tableName);
        }
    }

    public Vector parseTable() {
        int rows = this.tableInsertFrame.getTable().getRowCount();
        int cols = this.tableInsertFrame.getTable().getColumnCount();

        Vector dataTypes = this.dbUtils.getDataTypes(this.tableName);

        Vector tableValues = new Vector();

        if (rows >= 0 && cols >= 0) {
            for (int i = 0; i < rows; i++) {
                String rowData = "";

                for (int j = 0; j < cols; j++) {
                    String field = (String)this.tableInsertFrame.getTable().getValueAt(i, j);

                    if (field.length() > 0) {
                        field = this.fixApostrophes(field);

                        if (j > 0) rowData += ", ";

                        if (this.dbUtils.getStringDataTypes().contains(
                            dataTypes.get(j).toString())) {
                            rowData += "'"+field+"'";
                        } else {
                            rowData += field;
                        }
                    }
                }
                if (rowData.length() == 0) break;
                tableValues.addElement(" ( " + rowData + " );\n");
            }
        }

        return tableValues;
    }

    private String fixApostrophes(String in) {
        int n = 0;

        while((n = in.indexOf("'", n)) >= 0) {
            in = in.substring(0,n) + "'" + in.substring(n);
            n += 2;
        }

        return in;
    }

    class TableChangeListener implements TableModelListener {
        // Listener for Edit events on the JTable
        public TableChangeListener() { }

        public void tableChanged(TableModelEvent event) {
            Vector rowData = parseTable();
            SQLCommand = new String[rowData.size()];
            String SQLCommandRoot = "INSERT INTO " + tableName + " VALUES ";

            tableInsertFrame.getSQLPane().setText("");

            for (int i=0; i < rowData.size(); i++) {
                if (rowData.elementAt(i) == null) break;

                SQLCommand[i] = SQLCommandRoot + (String)rowData.elementAt(i);
                tableInsertFrame.getSQLPane().append(SQLCommand[i]);
            }
        }
    }

}
