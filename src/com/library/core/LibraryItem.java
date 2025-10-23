package com.library.core;

public abstract class LibraryItem {
    protected final int id;
    protected final String title;
    protected boolean isBorrowed;

    public LibraryItem(int id, String title) { 
        this.id = id;
        this.title = title;
        this.isBorrowed = false;
    }
    public abstract String getDetails();
    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isBorrowed() { return isBorrowed; }
    public void setBorrowed(boolean borrowed) { this.isBorrowed = borrowed; }
}