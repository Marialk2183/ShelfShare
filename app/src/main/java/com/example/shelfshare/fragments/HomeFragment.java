package com.example.shelfshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shelfshare.R;
import com.example.shelfshare.adapters.BookAdapter;
import com.example.shelfshare.data.BookEntity;
import com.example.shelfshare.databinding.FragmentHomeBinding;
import com.example.shelfshare.viewmodels.BookListViewModel;

public class HomeFragment extends Fragment implements BookAdapter.OnBookClickListener {
    private FragmentHomeBinding binding;
    private BookListViewModel viewModel;
    private BookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // Setup RecyclerView
        adapter = new BookAdapter(this);
        binding.recyclerViewBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewBooks.setAdapter(adapter);

        // Observe books
        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            if (books != null) {
                adapter.submitList(books);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Load books
        viewModel.loadBooks();
    }

    @Override
    public void onBookClick(BookEntity book) {
        // Navigate to book details using Bundle
        Bundle args = new Bundle();
        args.putString("bookId", book.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_bookDetailsFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 