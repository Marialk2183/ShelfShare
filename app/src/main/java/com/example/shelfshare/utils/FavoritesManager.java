package com.example.shelfshare.utils;

import com.example.shelfshare.data.BookEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FavoritesManager {
    private static FavoritesManager instance;
    private final Map<String, BookEntity> favoriteBooks;

    private FavoritesManager() {
        favoriteBooks = new HashMap<>();
    }

    public static FavoritesManager getInstance() {
        if (instance == null) {
            instance = new FavoritesManager();
        }
        return instance;
    }

    public void addToFavorites(BookEntity book) {
        favoriteBooks.put(book.getId(), book);
    }

    public void removeFromFavorites(BookEntity book) {
        favoriteBooks.remove(book.getId());
    }

    public boolean isFavorite(String bookId) {
        return favoriteBooks.containsKey(bookId);
    }

    public List<BookEntity> getFavoriteBooks() {
        return new ArrayList<>(favoriteBooks.values());
    }
} 