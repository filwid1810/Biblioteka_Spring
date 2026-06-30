package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.services.impl.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{id}")
    public Book get(@PathVariable String id) {
        return bookService.findById(id);
    }

    @PostMapping
    public Book create(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.removeBook(id);
        return ResponseEntity.noContent().build();
    }
}