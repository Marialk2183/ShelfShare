package com.example.shelfshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shelfshare.R;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements BookAdapter.OnBookClickListener {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> books;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        books = new ArrayList<>();
        adapter = new BookAdapter(books, this);
        recyclerView.setAdapter(adapter);
        
        loadBooks();
        return view;
    }

    private void loadBooks() {
        FirebaseFirestore.getInstance()
                .collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    books.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        books.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    @Override
    public void onBookClick(Book book) {
        // Handle book click
    }
} 