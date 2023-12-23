package com.example.applicationp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView imageViewExpandMore;
    private TextView textViewBookSummary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            loadBookDetails(bookId);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageViewExpandMore = findViewById(R.id.imageViewExpandMore);
        textViewBookSummary = findViewById(R.id.textViewBookSummary);

        imageViewExpandMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textViewBookSummary.getMaxLines() <= 3) {
                    textViewBookSummary.setMaxLines(Integer.MAX_VALUE);
                    imageViewExpandMore.setImageResource(R.drawable.ic_expand_less_24dp);
                } else {
                    textViewBookSummary.setMaxLines(3);
                    imageViewExpandMore.setImageResource(R.drawable.ic_expand_more_24dp);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadBookDetails(int bookId) {
        String url = getString(R.string.api_base_url) + "/api/books/" + bookId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String title = response.getString("title");
                            String coverImageUrl = response.getString("cover_image");
                            String author = response.getString("author");
                            String summary = response.getString("summary");
                            JSONArray chapters = response.getJSONArray("chapters");

                            updateUI(title, coverImageUrl, author, summary, chapters);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
    private void updateUI(String title, String coverImageUrl, String author, String summary, JSONArray chapters) {

        TextView titleTextView = findViewById(R.id.textViewBookTitle);
        ImageView coverImageView = findViewById(R.id.imageViewBookCover);
        TextView authorTextView = findViewById(R.id.textViewBookAuthor);
        TextView summaryTextView = findViewById(R.id.textViewBookSummary);
        ArrayList<Integer> chapterIds = new ArrayList<>();

        titleTextView.setText(title);
        authorTextView.setText(author);
        summaryTextView.setText(summary);


        Glide.with(this).load(getString(R.string.api_base_url) + coverImageUrl).into(coverImageView);


        LinearLayout chaptersLayout = findViewById(R.id.linearLayoutChapters);
        chaptersLayout.removeAllViews();
        for (int i = 0; i < chapters.length(); i++) {
            try {
                JSONObject chapter = chapters.getJSONObject(i);
                String chapterTitle = chapter.getString("title");

                int chapterId = chapter.getInt("id");
                chapterIds.add(chapterId);
                TextView chapterTextView = new TextView(this);
                chapterTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                chapterTextView.setText(chapterTitle);
                chapterTextView.setPadding(16, 16, 16, 16);
                chapterTextView.setBackgroundResource(R.drawable.divider);
                chapterTextView.setClickable(true);
                chapterTextView.setFocusable(true);
                chapterTextView.setTag(chapterId);

                if (i > 0) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) chapterTextView.getLayoutParams();
                    layoutParams.topMargin = 8;
                    chapterTextView.setLayoutParams(layoutParams);
                }
                chaptersLayout.addView(chapterTextView);

                chapterTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedChapterId = (int) v.getTag();
                        Intent intent = new Intent(BookDetailActivity.this, ChapterDetailActivity.class);
                        intent.putExtra("CHAPTER_ID", selectedChapterId);
                        intent.putIntegerArrayListExtra("CHAPTER_IDS", chapterIds);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

