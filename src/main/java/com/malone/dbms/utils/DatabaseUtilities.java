package com.malone.dbms.utils;

import java.sql.*;
import java.util.Vector;
import java.util.Arrays;
import java.util.Hashtable;

public class DatabaseUtilities {
    private static Connection con = null;

    public DatabaseUtilities() {
        setConnection();
    }

    private void setConnection() {
        if (DatabaseUtilities.con == null) {
            try {
                // Class.forName("com.mysql.jdbc.Driver");
                Class.forName("com.mysql.cj.jdbc.Driver");

                System.out.println("creating connection...");
                // testHMS
                DatabaseUtilities.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306",
                    "malonza",
                    "Pass1234");

                System.out.println("SERVER STATUS: ONLINE");

            } catch (ClassNotFoundException cnf) {
                System.out.println("ERROR!!: Database Drivers Not found");
                cnf.getStackTrace();
            } catch(SQLException ex) {
                if (ex.getErrorCode() == 0){
                    System.out.println("SERVER STATUS: UNAVAILABLE");
                }else{
                    System.out.println("ERROR "+ex.getErrorCode()+": "+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    public Connection getConnection() {
        if (DatabaseUtilities.con == null) {
            setConnection();
        }

        return DatabaseUtilities.con;
    }

    private void closeConnection() {
        System.out.println("Closing connection...");
        
        try {
            if (DatabaseUtilities.con != null) {
                DatabaseUtilities.con.close();
                System.out.println("Connection Closed.");
            } else {
                System.out.println("Error: Connection was never established or closed unexpectedly");
            } 
        } catch (SQLException se) {
            System.out.println(se.getLocalizedMessage());
        }
    }

    public Hashtable<String, Vector> getDatabaseSystemMap() {
        Hashtable databaseMap = new Hashtable();
        Vector databases = this.getDatabases();
        int dbNum = databases.size();

        for (int i = 0; i < dbNum; i++) {
            String database = databases.get(i).toString();
            Vector tables = this.getTables(database);
            databaseMap.put(database, tables);
        }

        return databaseMap;
    } 

    public Vector setDatabase(String dbName) {
        Vector response = new Vector();

        try {
            String sql = "USE " + dbName;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            pst.execute();

            response.addElement(true);
        } catch (SQLException se){
            response.addElement(false);
            response.addElement(se.getMessage());
            System.err.println(se);
        }

        return response;
    }

    public Vector getDatabases() {
        Vector databases = new Vector();

        try {
            String sql = "SHOW DATABASES";
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                databases.addElement(rs.getString(1));
            }
        } catch (SQLException se){
            reportException(se.getMessage());
        }

        return databases;
    }

    public Vector createDatabase(String newDbName) {
        // todo: Check if db exists;
        Vector response = new Vector();

        try {
            String sql = "CREATE DATABASE " + newDbName;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            pst.execute();

            // todo: Check if db exists;
            response.addElement(true);
        } catch (SQLException se){
            response.addElement(false);
            response.addElement(se.getMessage());
            System.err.println(se);
        }

        return response;
    }

    public Vector dropDatabase(String deleteDbName) {
        Vector response = new Vector();

        // todo: Check if db exists;
        if (deleteDbName.equals("mysql") || 
            deleteDbName.equals("information_schema") ||
            deleteDbName.equals("performance_schema") || 
            deleteDbName.equals("phpmyadmin")) {

            response.addElement(false);
            response.addElement("DATABASE " + deleteDbName + " CANNOT BE DROPPED!!");
        } else {
            try {
                String sql = "DROP DATABASE " + deleteDbName;
                PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
                pst.execute();

                // todo: Check if db exists;
                response.addElement(true);
            } catch (SQLException se){
                response.addElement(false);
                response.addElement(se.getMessage());
                System.err.println(se);
            }
        }

        return response;
    }

    public Vector getTables(String database) {
        Vector tables = new Vector();

        try {
            String sql = "USE " + database;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            pst.execute();

            sql = "SHOW TABLES";
            pst = DatabaseUtilities.con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                tables.addElement(rs.getString(1));
            }
        } catch (SQLException se){
            reportException(se.getMessage());
        }

        return tables;
    }

    public Vector describeTable(String tableName) {
        Vector table = new Vector();

        try {
            String sql = "DESCRIBE " + tableName;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            int nColumns = md.getColumnCount();

            while (rs.next()) {
                Vector rowData = new Vector();

                for (int i = 1; i <= nColumns; i++) {
                    rowData.addElement(rs.getObject(i));
                }

                table.addElement(rowData);
            }
        } catch (SQLException se){
            reportException(se.getMessage());
        }

        return table;
    }

    public Vector selectTable(String tableName) {
        Vector table = new Vector();

        try {
            String sql = "SELECT * FROM " + tableName;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            int nColumns = md.getColumnCount();

            while (rs.next()) {
                Vector rowData = new Vector();

                for (int i = 1; i <= nColumns; i++) {
                    rowData.addElement(rs.getObject(i));
                }

                table.addElement(rowData);
            }
        } catch (SQLException se){
            reportException(se.getMessage());
        }

        return table;
    }

    public Vector dropTable(String deleteTableName) {
        Vector response = new Vector();

        try {
            String sql = "DROP TABLE " + deleteTableName;
            PreparedStatement pst = DatabaseUtilities.con.prepareStatement(sql);
            pst.execute();

            // Check if table exists;
            Vector tables = this.getTables(deleteTableName);

            if (!tables.contains(deleteTableName)) {
                response.addElement(true); 
            } else {
                response.addElement(false);
                response.addElement("Error: Something unexpected happened! " + 
                    "Try Again Later.");
            }
        } catch (SQLException se){
            response.addElement(false);
            response.addElement(se.getMessage());
            System.err.println(se);
        }

        return response;
    }

    public Vector getColumnNames(String tableName) {
        Vector dataSet = new Vector();
        // String[] columnNames = null;
        String SQLCommand = "SELECT * FROM " + tableName + ";";

        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            ResultSet rs = stmt.executeQuery(SQLCommand);
            ResultSetMetaData md = rs.getMetaData();

            // columnNames = new String[md.getColumnCount()];
            dataSet = new Vector(md.getColumnCount());

            for (int i = 0; i < md.getColumnCount(); i++) {
                // columnNames[i] = md.getColumnLabel(i+1);
                dataSet.add(i, md.getColumnLabel(i+1));
            }
        } catch(SQLException e) {
            reportException(e.getMessage());
        }

        return dataSet;
    }

    public Vector getColumnNamesUsingQuery(String SQLCommand) {
        Vector dataSet = new Vector();
        // String[] columnNames = null;

        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            ResultSet rs = stmt.executeQuery(SQLCommand);
            ResultSetMetaData md = rs.getMetaData();

            // columnNames = new String[md.getColumnCount()];
            dataSet = new Vector(md.getColumnCount());

            for (int i=0; i < md.getColumnCount(); i++) {
                // columnNames[i] = md.getColumnLabel(i+1);
                dataSet.add(i, md.getColumnLabel(i+1));
            }
        } catch(SQLException e){
            reportException(e.getMessage());
        }

        return dataSet;
    }

    public Vector getDataTypes(String tableName) {
        Vector dataTypes = new Vector();
        // String[] dataTypes = null;
        String SQLCommand = "SELECT * FROM "+tableName+";";

        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            ResultSet rs = stmt.executeQuery(SQLCommand);
            ResultSetMetaData md = rs.getMetaData();

            // dataTypes = new String[md.getColumnCount()];
            dataTypes = new Vector(md.getColumnCount());

            for (int i = 0; i < md.getColumnCount(); i++) {
                dataTypes.add(i, md.getColumnTypeName(i+1));
            }
        } catch(SQLException e){
            reportException(e.getMessage());
        }

        return dataTypes;
    }

    public boolean execute(String SQLCommand) {
        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            stmt.execute(SQLCommand);

            return true;
        } catch(SQLException e){
            reportException(e.getMessage());

            return false;
        }
    }

    public boolean execute(String[] SQLCommand) {
        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            stmt.execute("BEGIN;");

            for(int i=0;i<SQLCommand.length;i++){
                stmt.execute(SQLCommand[i]);
            }

            stmt.execute("COMMIT;");

            return true;
        } catch(SQLException e){
            reportException(e.getMessage());

            try {
                Statement stmt = DatabaseUtilities.con.createStatement();
                stmt.execute("ROLLBACK;");
            } catch(SQLException se){
                System.out.println("ROLLBACK FAILED: " + se.getMessage());
            }

            return false;
        }
    }

    public Vector executeQuery(String SQLQuery) {
        Vector dataSet = new Vector();

        try {
            Statement stmt = DatabaseUtilities.con.createStatement();
            ResultSet rs = stmt.executeQuery(SQLQuery);
            ResultSetMetaData md = rs.getMetaData();

            int nColumns = md.getColumnCount();

            while(rs.next()){
                Vector rowData = new Vector();
                for(int i=1;i<=nColumns;i++){
                    rowData.addElement(rs.getObject(i));
                }
                dataSet.addElement(rowData);
            }
        } catch(SQLException e){
            reportException(e.getMessage());
        }

        return dataSet;
    }

    public Vector<String> getStringDataTypes() {
        String[] dataTypes = {
            "CHAR", "VARCHAR", "BINARY", "VARBINARY", "BLOB", "TEXT", "ENUM", "SET"}; 
        return new Vector<String>(Arrays.asList(dataTypes));
    }

    void reportException(String exception) {
        System.err.println(exception);
    }
}
