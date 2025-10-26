package com.library.core;

public class Book extends LibraryItem {
    private final String author;
    
    public Book(int id, String title, String author) {
        super(id, title);
        this.author = author;
    }
    @Override
    public String getDetails() {
        return String.format("Title: %s, Author: %s, Status: %s", 
            title, author, isBorrowed() ? "Borrowed" : "Available");
    }
    public String getAuthor() { return author; }
}