package com.library.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseDAO implements IDatabaseDAO {
    private final String jdbcUrl;
    private final String user;
    private final String pass;

    public DatabaseDAO(String jdbcUrl, String user, String pass) {
        this.jdbcUrl = "jdbc:mysql://localhost:3306/library_db";
        this.user = user;
        this.pass = pass;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, pass);
    }

    @Override
    public void saveItem(LibraryItem item) {
        String sql = "INSERT INTO items (id, title, author, is_borrowed, item_type) " +
                     "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, author=?, is_borrowed=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String author = (item instanceof Book) ? ((Book) item).getAuthor() : null;
            
            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, author);
            pstmt.setBoolean(4, item.isBorrowed());
            pstmt.setString(5, "BOOK"); 

            pstmt.setString(6, item.getTitle());
            pstmt.setString(7, author);
            pstmt.setBoolean(8, item.isBorrowed());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<LibraryItem> findItemById(int itemId) {
        String sql = "SELECT id, title, author, is_borrowed FROM items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                    book.setBorrowed(rs.getBoolean("is_borrowed"));
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<LibraryItem> getAllItems() {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT id, title, author, is_borrowed FROM items";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                items.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    @Override
    public boolean isItemIdDuplicate(int itemId) {
        String sql = "SELECT COUNT(*) FROM items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public void saveMember(Person member) {
        String sql = "INSERT INTO members (id, name, member_type, student_id) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=?, member_type=?, student_id=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String memberType = (member instanceof Student) ? "STUDENT" : "GENERIC_MEMBER";
            String studentId = (member instanceof Student) ? ((Student) member).getStudentId() : null;

            pstmt.setInt(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, memberType);
            pstmt.setString(4, studentId);

            pstmt.setString(5, member.getName());
            pstmt.setString(6, memberType);
            pstmt.setString(7, studentId);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Optional<Person> findMemberById(int memberId) {
        String sql = "SELECT id, name, member_type, student_id FROM members WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("member_type");
                    if (type.equals("STUDENT")) {
                        return Optional.of(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("student_id")));
                    } else {
                        return Optional.of(new Member(rs.getInt("id"), rs.getString("name")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Person> getAllMembers() {
        List<Person> members = new ArrayList<>();
        String sql = "SELECT id, name, member_type, student_id FROM members";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("member_type");
                if (type.equals("STUDENT")) {
                    members.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("student_id")));
                } else {
                    members.add(new Member(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    
    @Override
    public boolean isMemberIdDuplicate(int memberId) {
        String sql = "SELECT COUNT(*) FROM members WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}