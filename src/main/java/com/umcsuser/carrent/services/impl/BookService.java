package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(String id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono książki"));
    }

    public Book addBook(Book book) {

        return bookRepository.save(book);
    }

    public void removeBook(String bookId) {
        bookRepository.deleteById(bookId);
    }
}