/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsDatabase.java
 * -Provides methods for manipulating the database
 * -This includes reading rows, reading values, adding rows, adding values, reading the whole table,
 * create tables, etc.
 * ===================================================================================================
 */

package com.github.norbo11.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.norbo11.UltimatePoker;

public class MethodsDatabase
{
    public MethodsDatabase(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    public void addRow(List<SQLColumn> columns, SQLValue value)
    {
        String query = "INSERT INTO " + p.DATABASE_TABLE_NAME;

        if (columns != null)
        {
            query = query + " (";
            for (SQLColumn column : columns)
                query = query + column.header + ", ";
            query = query.substring(0, query.length() - 2);           // Remove the comma at the end if any columns were specified
            query = query + ")";                                      // Close our list of columns with a bracket, if any were specified
        }

        if (value.datatype.equalsIgnoreCase("string")) query = query + " VALUES ('" + value.value + "', ";
        else query = query + " VALUES (" + value.value + ", ";
        for (int i = 0; i < columns.size() - 1; i++)
            query = query + 0 + ", ";
        query = query.substring(0, query.length() - 2);           // Remove the comma at the end if any columns were specified
        query = query + ")";
        execute(query);
    }

    public void addRow(SQLColumn column, SQLValue value)
    {
        String query = "INSERT INTO " + p.DATABASE_TABLE_NAME;

        if (column != null) query = query + " (" + column.header + ")";

        if (value.datatype.equalsIgnoreCase("string")) query = query + " VALUES (" + "'" + value.value + "'" + ")";
        else query = query + " VALUES (" + value.value + ")";
        execute(query);

    }

    // Creates a table in the database
    public void createTable(String tableName, List<SQLColumn> columns)
    {
        String query = "CREATE TABLE " + tableName + " (";
        for (SQLColumn column : columns)
            // Iterate through the given columns, and form the query
            query = query + column.header + " " + column.dataType + ", ";

        query = query.substring(0, query.length() - 2);                         // Remove the comma at the end
        query = query + ")";

        try
        {
            p.database.prepareStatement(query).execute();
        } catch (SQLException e)
        {
        }
    }

    public void deleteAllRows(String table)
    {
        String query = "DELETE FROM " + table;
        execute(query);
    }

    public void execute(String query)
    {
        try
        {
            p.database.prepareStatement(query).execute();
        } catch (SQLException e)
        {
            p.methodsMisc.catchException(e);
        }
    }

    public ResultSet executeQuery(String query)
    {
        try
        {
            return p.database.prepareStatement(query).executeQuery();
        } catch (SQLException e)
        {
            p.methodsMisc.catchException(e);
            return null;
        }
    }

    public ResultSet getColumn(String column, boolean desc)
    {
        String query = "";
        if (!desc) query = "SELECT " + column + " FROM " + p.DATABASE_TABLE_NAME + " ORDER BY " + column + " ASC";
        if (desc) query = "SELECT " + column + " FROM " + p.DATABASE_TABLE_NAME + " ORDER BY " + column + " DESC";
        return executeQuery(query);
    }

    public ResultSet getColumn(String[] columns, boolean desc)
    {
        String query = "SELECT ";

        for (String temp : columns)
            query = query + temp + ", ";

        query = query.substring(0, query.length() - 2);                         // Remove the comma at the end
        query = query + " FROM " + p.DATABASE_TABLE_NAME + " ORDER BY " + columns[0];

        if (desc) query = query + " DESC";
        if (!desc) query = query + " ASC";
        return executeQuery(query);
    }

    public ResultSet getRow(String column, String value)
    {
        String query = "SELECT * FROM " + p.DATABASE_TABLE_NAME + " WHERE " + column + " like '" + value + "'";
        return executeQuery(query);
    }

    public ResultSet getTable(String tableName)
    {
        String query = "SELECT * FROM " + tableName;
        return executeQuery(query);
    }

    public double getValue(String column, String playername)
    {
        try
        {
            String query = "SELECT " + column + " FROM " + p.DATABASE_TABLE_NAME + " WHERE playerName like '" + playername + "'";
            ResultSet rs = executeQuery(query);
            rs.next();
            return rs.getDouble(column);
        } catch (Exception e)
        {
            p.methodsMisc.catchException(e);
            return -1;
        }
    }

    // Sets a value in the database
    public void modifyCell(String column, String row, SQLValue value)
    {
        String query = "UPDATE " + p.DATABASE_TABLE_NAME + " SET " + column + " = " + value.value + " WHERE playerName" + " like " + row;
        execute(query);
    }

    public void modifyRow(String row, List<SQLColumn> columns, List<SQLValue> values)
    {
        String query = "UPDATE " + p.DATABASE_TABLE_NAME + " SET ";

        int i = 1;
        for (SQLValue value : values)
        {
            query = query + columns.get(i).header + " = " + value.value + ", ";
            i++;
        }
        query = query.substring(0, query.length() - 2);                         // Remove the comma at the end
        query = query + " WHERE playerName like '" + row + "'";

        execute(query);
    }
}
