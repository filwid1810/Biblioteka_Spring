package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.repositories.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Profile("jdbc")
public class BookJdbcRepository implements BookRepository {

    private final DataSource dataSource;

    public BookJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return Book.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .isbn(rs.getString("isbn"))
                .price(rs.getBigDecimal("price"))
                .build();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, price FROM books";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd odczytu książek", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return books;
    }

    @Override
    public Optional<Book> findById(String id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd szukania książki", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId(java.util.UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO books (id, title, author, isbn, price) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "title = EXCLUDED.title, author = EXCLUDED.author, isbn = EXCLUDED.isbn, price = EXCLUDED.price";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getIsbn());
            stmt.setBigDecimal(5, book.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu książki", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return book;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM books WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania książki", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}