package codepath.com.bookse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Book> books;
    Button newBookCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Lookup the recyclerview in activity layout
        RecyclerView rvBooks = (RecyclerView) findViewById(R.id.rvBooks);

        // Initialize books
        books = Book.createContactsList(30);
        // Create adapter passing in the sample user data
        BooksAdapter adapter = new BooksAdapter(books);
        // Attach the adapter to the recyclerview to populate items
        rvBooks.setAdapter(adapter);
        // Set layout manager to position the items
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        newBookCamera = (Button) findViewById(R.id.newBookCamera);
        newBookCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
