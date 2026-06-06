package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.Tag;

/**
 * Manages the currently selected tag globally.
 *
 * Core Responsibility:
 * - MAINTAIN single selected tag state
 * - PROVIDE getter/setter for other components
 * - ENFORCE single selection constraint (only one tag at a time)
 *
 * Design Principle:
 * - Singleton pattern: Stateful storage for selected tag
 * - Thread-safe storage via volatile field
 * - Other controllers/services can query/update selection
 *
 * Usage:
 * - Set: TagSelectionManager.getInstance().selectTag(tag)
 * - Get: Tag selected = TagSelectionManager.getInstance().getSelectedTag()
 * - Clear: TagSelectionManager.getInstance().clearSelection()
 */
public class TagSelectionManager {

    private static final TagSelectionManager instance = new TagSelectionManager();

    private volatile Tag selectedTag = null;

    private TagSelectionManager() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return the TagSelectionManager instance
     */
    public static TagSelectionManager getInstance() {
        return instance;
    }

    /**
     * Selects a tag (replaces any previous selection).
     *
     * @param tag the Tag to select, or null to clear
     */
    public void selectTag(Tag tag) {
        this.selectedTag = tag;
    }

    /**
     * Gets the currently selected tag.
     *
     * @return the selected Tag, or null if no tag is selected
     */
    public Tag getSelectedTag() {
        return selectedTag;
    }

    /**
     * Checks if a tag is currently selected.
     *
     * @return true if a tag is selected, false otherwise
     */
    public boolean hasSelection() {
        return selectedTag != null;
    }

    /**
     * Gets the ID of the selected tag.
     *
     * @return the selected tag ID, or null if no tag is selected
     */
    public Integer getSelectedTagId() {
        return selectedTag != null ? selectedTag.getId() : null;
    }

    /**
     * Gets the name of the selected tag.
     *
     * @return the selected tag name, or null if no tag is selected
     */
    public String getSelectedTagName() {
        return selectedTag != null ? selectedTag.getName() : null;
    }

    /**
     * Clears the current selection.
     */
    public void clearSelection() {
        this.selectedTag = null;
    }

    /**
     * Checks if a given tag is the currently selected tag.
     *
     * @param tag the Tag to check
     * @return true if this tag is selected, false otherwise
     */
    public boolean isSelected(Tag tag) {
        if (tag == null || selectedTag == null) {
            return false;
        }
        return tag.getId() != null && tag.getId().equals(selectedTag.getId());
    }
}
