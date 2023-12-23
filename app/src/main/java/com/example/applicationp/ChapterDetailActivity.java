package com.example.applicationp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChapterDetailActivity extends AppCompatActivity {
    private ArrayList<Integer> chapterIds;
    private int currentChapterIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int chapterId = getIntent().getIntExtra("CHAPTER_ID", -1);
        chapterIds = getIntent().getIntegerArrayListExtra("CHAPTER_IDS");
        currentChapterIndex = chapterIds.indexOf(chapterId);

        if (chapterId != -1) {
            loadChapterContent(chapterId);
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        updateChapterNavigationButtons();

    }

    private void updateChapterNavigationButtons() {
        Button btnPrevious = findViewById(R.id.btnPrevious);
        Button btnNext = findViewById(R.id.btnNext);

        btnPrevious.setEnabled(currentChapterIndex > 0);
        btnNext.setEnabled(currentChapterIndex < chapterIds.size() - 1);

        btnPrevious.setOnClickListener(v -> loadChapterContent(chapterIds.get(currentChapterIndex - 1)));
        btnNext.setOnClickListener(v -> loadChapterContent(chapterIds.get(currentChapterIndex + 1)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadChapterContent(int chapterId) {
        currentChapterIndex = chapterIds.indexOf(chapterId);
        String url = getString(R.string.api_base_url) + "api/chapters/" + chapterId;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            byte[] u = response.getBytes("ISO-8859-1");
                            response = new String(u, "UTF-8");
                            JSONObject jsonResponse = new JSONObject(response);
                            String chapterTitle = jsonResponse.getString("title");
                            String chapterContent = jsonResponse.getString("content");
                            updateChapterUI(chapterTitle, chapterContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Xử lý lỗi JSON
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi Volley
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);

        updateChapterNavigationButtons();

    }

    private void updateChapterUI(String chapterTitle, String chapterContent) {
        TextView chapterTitleTextView = findViewById(R.id.chapterTitle);
        TextView chapterContentTextView = findViewById(R.id.chapterContent);
        ScrollView scrollView = findViewById(R.id.chapterScrollView);
        chapterTitleTextView.setText(chapterTitle);
        chapterContentTextView.setText(chapterContent);
        scrollView.scrollTo(0, 0);

    }

}

