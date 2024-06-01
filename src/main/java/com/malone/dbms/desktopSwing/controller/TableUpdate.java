package com.malone.dbms.desktopSwing.controller;

import com.malone.dbms.desktopSwing.views.TableUpdateFrame;
import com.malone.dbms.utils.DatabaseUtilities;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.util.Vector;

public class TableUpdate {
    private TableUpdateFrame tableUpdateFrame = new TableUpdateFrame();
    private DatabaseUtilities dbUtils = new DatabaseUtilities();
    private Vector dataSet = new Vector();
    private Vector colNames = new Vector();
    private String tableName = "";
    private String SQLCommand[] = null;

	public TableUpdate() { }

	protected TableUpdateFrame getTableUpdateFrame(String tableName) {
        this.colNames = this.dbUtils.getColumnNames(tableName);
        this.dataSet = this.dbUtils.selectTable(tableName);
        TableChangeListener modelListener = new TableChangeListener();

        this.tableName = tableName;

        this.tableUpdateFrame = new TableUpdateFrame("UPDATE " + tableName);
        this.tableUpdateFrame.setTableContent(this.colNames, this.dataSet);
        this.tableUpdateFrame.getTable().getModel().addTableModelListener(modelListener);

        this.tableUpdateFrame.updateButton.addActionListener(e -> executeQuery());

        return this.tableUpdateFrame;
    }

	public void executeQuery() {
        boolean isUpdated = this.dbUtils.execute(SQLCommand);

        if (isUpdated) {
            JOptionPane.showMessageDialog(
                null,
                "Data in Table " + this.tableName + " Updated.");
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Error: Data not updated.");
        }
    }

    public Vector parseTable() {
        Vector tableValues = new Vector();

        Vector dataTypes = this.dbUtils.getDataTypes(this.tableName);

        int rows = this.tableUpdateFrame.getTable().getRowCount();
        int cols = this.tableUpdateFrame.getTable().getColumnCount();

        if (rows >= 0 && cols >= 0) {
            for (int i = 0; i < rows; i++) {
                Vector rowDataSet = (Vector) this.dataSet.elementAt(i);

                String rowData = "";
                String whereClauseData = "";

                for (int j = 0; j < cols; j++) {
                    String field = (String) this.tableUpdateFrame.getTable().getValueAt(i, j);

                    if (field.length() > 0) {
                        field = fixApostrophes(field);

                        if (j > 0) rowData += ", ";

                        if (this.dbUtils.getStringDataTypes().contains(
                            dataTypes.get(j).toString())) {
                            rowData += this.colNames.get(j) + " = '" + field + "'";
                        } else {
                            rowData += this.colNames.get(j) + " = " + field;
                        }

                        try {
                            String value = (rowDataSet.elementAt(j)).toString();

                            if (j > 0) whereClauseData += " AND "; 

                            if (this.dbUtils.getStringDataTypes().contains(
                                dataTypes.get(j).toString())) {
                                whereClauseData += this.colNames.get(j) + " = '"+ value +"'";
                            } else {
                                whereClauseData += this.colNames.get(j) + " = " + value;
                            }
                        } catch (NullPointerException npe) { }
                    }
                }

                if (rowData.length()==0) break;
                tableValues.addElement(" SET " + rowData + " WHERE " + whereClauseData + ";\n");
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
            String SQLCommandRoot = "UPDATE " + tableName;

            tableUpdateFrame.getSQLPane().setText("");

            for (int i=0; i < rowData.size(); i++) {
                if (rowData.elementAt(i) == null) break;

                SQLCommand[i] = SQLCommandRoot + (String)rowData.elementAt(i);
                tableUpdateFrame.getSQLPane().append(SQLCommand[i]);
            }
        }
    }

}
