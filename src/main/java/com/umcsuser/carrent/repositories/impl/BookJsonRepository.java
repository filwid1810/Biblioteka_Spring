package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.repositories.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("json")
public class BookJsonRepository implements BookRepository {

    private final JsonFileStorage<Book> storage;
    private List<Book> books;

    public BookJsonRepository() {
        this.storage = new JsonFileStorage<>("books.json", new TypeToken<List<Book>>(){}.getType());
        this.books = storage.load();
    }

    @Override
    public List<Book> findAll() {
        return books.stream().map(Book::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(String id) {
        return books.stream()
                .filter(v -> v.getId().equals(id))
                .map(Book::copy)
                .findFirst();
    }

    @Override
    public Book save(Book book) {
        deleteById(book.getId());
        books.add(book.copy());
        storage.save(books);
        return book;
    }

    @Override
    public void deleteById(String id) {
        books.removeIf(v -> v.getId().equals(id));
        storage.save(books);
    }
}
