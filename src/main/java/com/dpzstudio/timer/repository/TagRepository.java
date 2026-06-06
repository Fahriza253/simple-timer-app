package com.dpzstudio.timer.repository;

import com.dpzstudio.timer.model.Tag;
import com.dpzstudio.timer.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public List<Tag> findAll() throws SQLException {
        String sql = "SELECT * FROM tag ORDER BY name ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Tag> tags = new ArrayList<>();

            while (rs.next()) {
                tags.add(mapRow(rs));
            }

            return tags;
        }
    }

    public Tag findByName(String name) throws SQLException {
        String sql = "SELECT * FROM tag WHERE name = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }
        }
    }

    public void save(Tag tag) throws SQLException {
        String sql = "INSERT INTO tag (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tag.getName());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tag.setId(rs.getInt(1));
                }
            }
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM tag WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsByName(String name) throws SQLException {
        return findByName(name) != null;
    }

    private Tag mapRow(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
        tag.setCreatedAt(rs.getString("created_at"));
        tag.setUpdatedAt(rs.getString("updated_at"));
        return tag;
    }
}
