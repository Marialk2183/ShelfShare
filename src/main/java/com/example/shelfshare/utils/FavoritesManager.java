package com.example.shelfshare.utils;

import com.example.shelfshare.data.BookEntity;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static FavoritesManager instance;
    private List<BookEntity> favoriteBooks;

    private FavoritesManager() {
        favoriteBooks = new ArrayList<>();
    }

    public static FavoritesManager getInstance() {
        if (instance == null) {
            instance = new FavoritesManager();
        }
        return instance;
    }

    public void addToFavorites(BookEntity book) {
        if (!favoriteBooks.contains(book)) {
            favoriteBooks.add(book);
        }
    }

    public void removeFromFavorites(BookEntity book) {
        favoriteBooks.remove(book);
    }

    public List<BookEntity> getFavoriteBooks() {
        return favoriteBooks;
    }

    public boolean isFavorite(BookEntity book) {
        return favoriteBooks.contains(book);
    }
} 