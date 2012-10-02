package com.github.norbo11.util;

import java.io.InputStream;

import com.github.norbo11.UltimateCards;

public class ResourceManager
{
    UltimateCards p;

    public ResourceManager(UltimateCards p)
    {
        this.p = p;
    }

    public InputStream getResource(String filename)
    {
        return p.getResource(filename);
    }
}
