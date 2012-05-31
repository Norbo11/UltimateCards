/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: SQLValue.java
 * -Simple class which holds an SQL value. 
 * -It can be  string, integer or double at the moment. Is constructed by a string which is then
 * converted into one of these.
 * -When accessing "value" make sure to cast.
 * ===================================================================================================
 */

package com.github.norbo11.database;

import com.github.norbo11.UltimatePoker;

public class SQLValue
{

    public SQLValue(UltimatePoker p, String value)
    {
        this.p = p;

        if (p.methodsCheck.isDouble(value))
        {
            if (p.methodsCheck.isInteger(value))
            {
                this.value = Integer.parseInt(value);
                datatype = "integer";
            } else
            {
                this.value = Double.parseDouble(value);
                datatype = "double";
            }
            return;
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
        {
            this.value = Boolean.parseBoolean(value);
            datatype = "boolean";
            return;
        }
        this.value = value;
        datatype = "string";
    }

    UltimatePoker p;

    public Object value;
    public String datatype;
}
