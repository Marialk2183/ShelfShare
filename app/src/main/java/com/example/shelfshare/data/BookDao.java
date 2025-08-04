package com.example.shelfshare.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    LiveData<List<BookEntity>> getAllBooks();

    @Query("SELECT * FROM books WHERE available = 1")
    LiveData<List<BookEntity>> getAvailableBooks();

    @Query("SELECT * FROM books WHERE isFavorite = 1")
    LiveData<List<BookEntity>> getFavoriteBooks();

    @Insert
    void insert(BookEntity book);

    @Insert
    void insertAll(List<BookEntity> books);

    @Update
    void update(BookEntity book);

    @Delete
    void delete(BookEntity book);

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    void updateFavoriteStatus(String bookId, boolean isFavorite);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    LiveData<List<BookEntity>> searchBooks(String query);
} 