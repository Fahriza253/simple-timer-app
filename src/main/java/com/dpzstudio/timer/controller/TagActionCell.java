package com.dpzstudio.timer.controller;

import com.dpzstudio.timer.model.Tag;
import com.dpzstudio.timer.service.TagSelectionManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Consumer;

public class TagActionCell extends TableCell<Tag, Void> {

    private final Button selectButton = new Button("Select");
    private final Button deleteButton = new Button("Delete");
    private final HBox buttonBox = new HBox(5);

    private final TagSelectionManager selectionManager = TagSelectionManager.getInstance();

    public TagActionCell(Consumer<Tag> onSelectAction, Consumer<Tag> onDeleteAction) {
        // Apply CSS classes
        selectButton.getStyleClass().add("btn-primary");
        deleteButton.getStyleClass().add("btn-warn");

        // Ensure buttons stretch to max width (from our previous fix)
        HBox.setHgrow(selectButton, Priority.ALWAYS);
        HBox.setHgrow(deleteButton, Priority.ALWAYS);
        selectButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);

        // Attach event listeners using the passed-in consumers
        selectButton.setOnAction(event -> {
            Tag tag = getTableView().getItems().get(getIndex());
            if (tag != null) {
                onSelectAction.accept(tag);
            }
        });

        deleteButton.setOnAction(event -> {
            Tag tag = getTableView().getItems().get(getIndex());
            if (tag != null) {
                onDeleteAction.accept(tag);
            }
        });

        // Configure layout container
        buttonBox.setSpacing(5);
        buttonBox.setPadding(new Insets(0, 5, 0, 5));
        buttonBox.getStyleClass().add("action-button-box");
        buttonBox.getChildren().addAll(selectButton, deleteButton);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        // Handle empty rows
        if (empty || getIndex() >= getTableView().getItems().size()) {
            setGraphic(null);
            return;
        }

        // Get the current tag for this row
        Tag tag = getTableView().getItems().get(getIndex());
        if (tag == null) {
            setGraphic(null);
            return;
        }

        // Update button UI based on selection state
        boolean selected = selectionManager.isSelected(tag);
        selectButton.setText(selected ? "Selected" : "Select");
        selectButton.setDisable(selected);

        setGraphic(buttonBox);
    }
}
