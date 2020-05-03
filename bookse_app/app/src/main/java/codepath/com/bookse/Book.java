package codepath.com.bookse;

import java.util.ArrayList;

public class Book {
    private String title;
    private String author;
    private int rating;
    private String description;

    public Book(String mTitle, String mAuthor, int mRating, String mDescription) {
        title = mTitle;
        author = mAuthor;
        rating = mRating;
        description = mDescription;
    }

    public Book()
    {
        title = "Title";
        author = "Mr. Author";
        rating = 3;
        description = "This is a very good book!";
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public int getRating() {
        return rating;
    }
    public String getDescription() {
        return description;
    }

    public static ArrayList<Book> createContactsList(int numBooks) {
        ArrayList<Book> books = new ArrayList<Book>();

        for (int i = 1; i <= numBooks; i++) {
            books.add(new Book());
        }
        return books;
    }
}

