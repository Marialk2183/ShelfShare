package com.example.shelfshare.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    LiveData<List<BookEntity>> getAllBooks();

    @Query("SELECT * FROM books WHERE isAvailable = 1")
    LiveData<List<BookEntity>> getAvailableBooks();

    @Query("SELECT * FROM books WHERE isFavorite = 1")
    LiveData<List<BookEntity>> getFavoriteBooks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookEntity book);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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