package com.example.applicationp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationp.adapters.BookAdapter;
import com.example.applicationp.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LibraryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        booksRecyclerView = findViewById(R.id.recyclerViewBooks);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(this, bookList);
        booksRecyclerView.setAdapter(bookAdapter);
        fetchBooks();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_library) {
                    fetchBooks();
                    return true;
                } else if (itemId == R.id.navigation_settings) {
                    Intent intent = new Intent(LibraryActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchBooks(String query) {
        String url = getString(R.string.api_base_url) + "api/books_search/?q=" + query;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        bookList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject bookObject = response.getJSONObject(i);
                            int id = bookObject.getInt("id");
                            String title = bookObject.getString("title");
                            String author = bookObject.getString("author");
                            String coverImage = bookObject.getString("cover_image");
                            Book book = new Book(id, title, author, coverImage);
                            bookList.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(jsonArrayRequest);
    }
    private void fetchBooks() {
        String url = getString(R.string.api_base_url) + "api/books/";
        RequestQueue queue = Volley.newRequestQueue(this);

        bookList.clear();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject bookObject = response.getJSONObject(i);
                        int id = bookObject.getInt("id");
                        String author = bookObject.getString("author");
                        String title = bookObject.getString("title");
                        String coverImage = bookObject.getString("cover_image");
                        Book book = new Book(id, title, author, coverImage);
                        bookList.add(book);
                    }
                    bookAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                error.printStackTrace();
            }
        });

        queue.add(jsonArrayRequest);
    }


}
