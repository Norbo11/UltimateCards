package com.github.Norbo11.methods;

import com.github.Norbo11.UltimatePoker;

public class MethodsMisc {

    UltimatePoker p;
    public MethodsMisc(UltimatePoker p) {
        this.p = p;
    }

    public boolean isInteger(String string)
    {
        try
        {
            int integer = Integer.parseInt(string);
            if (integer >= 0)
            {
                return true;
            } else return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
