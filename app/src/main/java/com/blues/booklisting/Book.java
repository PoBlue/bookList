package com.blues.booklisting;

/**
 * Created by blues on 2016/10/5.
 */

public class Book {
    private String mTitle;
    private String mAuthors;
    private String mDate;

    public Book(String title,String authors,String date){
        mTitle = title;
        mAuthors = authors;
        mDate = date;
    }

    public String getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }
}
