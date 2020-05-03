package codepath.com.bookse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BooksAdapter extends
        RecyclerView.Adapter<BooksAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView nameImageView;
        public TextView nameTitleView;
        public TextView nameAuthorView;
        public RatingBar nameRatingBarView;
        public TextView nameDescriptionView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameImageView = (ImageView) itemView.findViewById(R.id.bookCover);
            nameTitleView = (TextView) itemView.findViewById(R.id.title);
            nameAuthorView = (TextView) itemView.findViewById(R.id.author);
            nameRatingBarView = (RatingBar) itemView.findViewById(R.id.ratingBar);
            nameDescriptionView = (TextView) itemView.findViewById(R.id.description);
        }
    }

    // Store a member variable for the contacts
    private List<Book> mBooks;

    // Pass in the contact array into the constructor
    public BooksAdapter(List<Book> books) {
        mBooks = books;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_book, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BooksAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Book book = mBooks.get(position);

        // Set item views based on your views and data model
        //ImageView imageView = viewHolder.nameImageView;

        TextView titleView = viewHolder.nameTitleView;
        titleView.setText(book.getTitle());

        TextView authorView = viewHolder.nameAuthorView;
        authorView.setText(book.getAuthor());

        RatingBar ratingBarView = viewHolder.nameRatingBarView;
        ratingBarView.setNumStars(book.getRating());

        TextView descriptionView = viewHolder.nameDescriptionView;
        descriptionView.setText(book.getDescription());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}
