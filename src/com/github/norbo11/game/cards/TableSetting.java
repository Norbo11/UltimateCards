package com.github.norbo11.game.cards;

public abstract class TableSetting<T> {
    private T value = null;
    private String name = "";
    
    public TableSetting(String name) {
        this(null, name);
    }
    
    public TableSetting(T value, String name) {
        this.value = value;
        this.name = name;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract String toString();
    
    public abstract void setValueUsingInput(String value);

    public abstract String getHelpString();
}
