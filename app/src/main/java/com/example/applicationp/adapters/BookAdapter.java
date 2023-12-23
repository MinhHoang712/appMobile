package com.example.applicationp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.applicationp.BookDetailActivity;
import com.example.applicationp.model.Book;
import android.widget.Filter;
import android.widget.Filterable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.example.applicationp.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> implements Filterable {
    private Context context;
    private List<Book> bookList;
    private List<Book> bookListFull;
    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        bookListFull = new ArrayList<>(bookList);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());
        String baseUrl = context.getString(R.string.api_base_url);
        String coverImageUrl = baseUrl + book.getCoverImageUrl();
        Glide.with(context).load(coverImageUrl).into(holder.coverImageView);
        holder.textViewAuthor.setText(book.getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public Filter getFilter() {
        return bookFilter;
    }
    private Filter bookFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Book> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bookListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(new Locale("vi", "VN")).trim();

                for (Book item : bookListFull) {
                    String normalizedTitle = Normalizer.normalize(item.getTitle(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "").toLowerCase(new Locale("vi", "VN"));

                    if (normalizedTitle.contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookList.clear();
            bookList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView coverImageView;
        public TextView textViewAuthor;

        public BookViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.textViewTitle);
            coverImageView = view.findViewById(R.id.imageViewCover);
            textViewAuthor = view.findViewById(R.id.textViewAuthor);
        }
    }
}
