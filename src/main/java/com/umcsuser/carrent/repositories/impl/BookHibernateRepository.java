package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.repositories.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class BookHibernateRepository implements BookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("FROM Book", Book.class).getResultList();
    }

    @Override
    public Optional<Book> findById(String id) {
        return Optional.ofNullable(entityManager.find(Book.class, id));
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId(UUID.randomUUID().toString());
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(String id) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }
}