package com.example.shelfshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shelfshare.databinding.FragmentBookDetailsBinding;
import com.example.shelfshare.viewmodels.BookDetailsViewModel;

public class BookDetailsFragment extends Fragment {
    private FragmentBookDetailsBinding binding;
    private BookDetailsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get book ID from arguments
        String bookId = getArguments() != null ? getArguments().getString("bookId") : null;
        if (bookId == null) {
            requireActivity().onBackPressed();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BookDetailsViewModel.class);

        // Load book details
        viewModel.loadBookDetails(bookId);

        // Observe book details
        viewModel.getBook().observe(getViewLifecycleOwner(), book -> {
            if (book != null) {
                binding.textBookTitle.setText(book.getTitle());
                binding.textBookAuthor.setText(book.getAuthor());
                binding.textBookPrice.setText(String.format("$%.2f/day", book.getPrice()));
                binding.textBookDescription.setText(book.getDescription());
            }
        });

        // Setup rent button
        binding.buttonRent.setOnClickListener(v -> {
            // TODO: Implement rent functionality
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 