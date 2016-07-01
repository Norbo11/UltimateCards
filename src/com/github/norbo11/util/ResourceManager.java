package com.github.norbo11.util;

import java.io.InputStream;

import com.github.norbo11.UltimateCards;

public class ResourceManager {
    public static UltimateCards p;

    public static InputStream getResource(String filename) {
        return p.getResource(filename);
    }
}
