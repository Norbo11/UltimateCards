/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: SQLColumn.java
 * -Simple class that holds a header and a data type which is needed when create the table in SQL.
 * ===================================================================================================
 */

package com.github.norbo11.database;

public class SQLColumn
{
    public SQLColumn(String header, String dataType)
    {
        this.header = header;
        this.dataType = dataType;
    }

    public String header;
    public String dataType; // This has to be exactly how it is in SQL, so like varchar (255) etc.
}
