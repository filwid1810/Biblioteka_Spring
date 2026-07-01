package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.CartItem;
import com.umcsuser.carrent.repositories.CartItemRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class CartItemJdbcRepository implements CartItemRepository {

    private final DataSource dataSource;

    public CartItemJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        return CartItem.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("user_id"))
                .bookId(rs.getString("book_id"))
                .quantity(rs.getInt("quantity"))
                .build();
    }

    @Override
    public List<CartItem> findByUserId(String userId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE user_id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania koszyka", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return items;
    }

    @Override
    public Optional<CartItem> findByUserIdAndBookId(String userId, String bookId) {
        String sql = "SELECT * FROM cart_items WHERE user_id = ? AND book_id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd szukania w koszyku", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem.getId() == null) {
            cartItem.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO cart_items (id, user_id, book_id, quantity) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET quantity = EXCLUDED.quantity";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cartItem.getId());
            stmt.setString(2, cartItem.getUserId());
            stmt.setString(3, cartItem.getBookId());
            stmt.setInt(4, cartItem.getQuantity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu do koszyka", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return cartItem;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania elementu koszyka", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void deleteByUserId(String userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd czyszczenia koszyka", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}