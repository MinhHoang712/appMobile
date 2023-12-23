package com.example.applicationp.model;

import java.util.List;

public class Book {
    private int id;
    private String title;
    private String author;
    private String coverImageUrl;
//    private List<Category> categories;

    // Constructor
    public Book(int id, String title, String author, String coverImageUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverImageUrl = coverImageUrl;
//        this.categories = categories;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

//    public List<Category> getCategories() {
//        return categories;
//    }
//
//    public void setCategories(List<Category> categories) {
//        this.categories = categories;
//    }
}
