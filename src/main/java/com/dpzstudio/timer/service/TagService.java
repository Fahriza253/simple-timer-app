package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.Tag;
import com.dpzstudio.timer.repository.TagRepository;
import com.dpzstudio.timer.util.InputValidation;

import java.sql.SQLException;
import java.util.List;

public class TagService {

    private final TagRepository repo = new TagRepository();

    public List<Tag> listTags() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tags", e);
        }
    }

    public void createTag(String rawName) {
        String normalized = InputValidation.normalizeName(rawName);

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }

        if (!InputValidation.isValidTagName(normalized)) {
            throw new IllegalArgumentException("Tag name must contain only letters, numbers, and spaces");
        }

        try {
            if (repo.existsByName(normalized)) {
                throw new IllegalArgumentException("Tag '" + normalized + "' already exists");
            }

            Tag tag = new Tag(normalized);
            repo.save(tag);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save tag", e);
        }
    }

    public void deleteTag(int tagId) {
        try {
            repo.deleteById(tagId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag", e);
        }
    }
}
