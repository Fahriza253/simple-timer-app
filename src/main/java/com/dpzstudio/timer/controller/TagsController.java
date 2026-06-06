package com.dpzstudio.timer.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.layout.Priority;
import com.dpzstudio.timer.model.Tag;
import com.dpzstudio.timer.service.TagService;
import com.dpzstudio.timer.service.TagSelectionManager;

public class TagsController implements Initializable {

    @FXML
    private TextField inputTagName;
    @FXML
    private Button btnAddTag;
    @FXML
    private TableView<Tag> tableTags;
    @FXML
    private TableColumn<Tag, Number> columnNoTags;
    @FXML
    private TableColumn<Tag, String> columnTagName;
    @FXML
    private TableColumn<Tag, Void> columnActions;

    private final TagService tagService = new TagService();
    private final TagSelectionManager selectionManager = TagSelectionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        configureTable();
        configureRowHighlighting();
        loadTags();

        btnAddTag.setOnAction(event -> handleAddTag());
        inputTagName.setOnAction(event -> handleAddTag());
    }

    private void configureTable() {
        columnNoTags.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(tableTags.getItems().indexOf(cellData.getValue()) + 1)
        );

        columnTagName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnActions.setCellFactory(column -> new TagActionCell(this::selectTag, this::deleteTag));
    }

    private void configureRowHighlighting() {
        tableTags.setRowFactory(tv -> new TableRow<Tag>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    // getStyleClass().remove("selected-tag-row");
                } else {
                    if (selectionManager.isSelected(item)) {
                        if (!getStyleClass().contains("selected-tag-row")) {
                            getStyleClass().add("selected-tag-row");
                        }
                    } else {
                        getStyleClass().remove("selected-tag-row");
                    }
                }
            }
        });
    }

    private Callback<TableColumn<Tag, Void>, TableCell<Tag, Void>> createActionCellFactory() {
        return column -> new TableCell<>() {
            private final Button selectButton = new Button("Select");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5);

            {
                selectButton.getStyleClass().add("btn-primary");
                deleteButton.getStyleClass().add("btn-warn");

                HBox.setHgrow(selectButton, Priority.ALWAYS);
                HBox.setHgrow(deleteButton, Priority.ALWAYS);
                selectButton.setMaxWidth(Double.MAX_VALUE);
                deleteButton.setMaxWidth(Double.MAX_VALUE);

                selectButton.setOnAction(event -> {
                    Tag tag = getTableView().getItems().get(getIndex());
                    selectTag(tag);
                });

                deleteButton.setOnAction(event -> {
                    Tag tag = getTableView().getItems().get(getIndex());
                    deleteTag(tag);
                });

                buttonBox.setSpacing(5);
                buttonBox.setPadding(new Insets(0, 5, 0, 5));
                buttonBox.getChildren().addAll(selectButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Tag tag = getTableView().getItems().get(getIndex());
                boolean selected = selectionManager.isSelected(tag);
                selectButton.setText(selected ? "Selected" : "Select");
                selectButton.setDisable(selected);
                setGraphic(buttonBox);
            }
        };
    }

    private void loadTags() {
        List<Tag> tags = tagService.listTags();

        if (selectionManager.getSelectedTagId() != null
                && tags.stream().noneMatch(tag -> tag.getId() != null && tag.getId().equals(selectionManager.getSelectedTagId()))) {
            selectionManager.clearSelection();
        }

        tableTags.setItems(FXCollections.observableArrayList(tags));
        tableTags.refresh();
    }

    private void handleAddTag() {
        String tagName = inputTagName.getText();

        try {
            tagService.createTag(tagName);
            inputTagName.clear();
            loadTags();
            showInformation("Tag added successfully.");
        } catch (IllegalArgumentException e) {
            showWarning(e.getMessage());
        } catch (Exception e) {
            showWarning("Failed to add tag: " + e.getMessage());
        }
    }

    private void selectTag(Tag tag) {
        if (tag == null || tag.getId() == null) {
            showWarning("Cannot select this tag right now.");
            return;
        }

        selectionManager.selectTag(tag);
        tableTags.refresh();
        showInformation("Tag '" + tag.getName() + "' selected.");
    }

    private void deleteTag(Tag tag) {
        if (tag == null || tag.getId() == null) {
            showWarning("Cannot delete this tag right now.");
            return;
        }

        try {
            if (selectionManager.isSelected(tag)) {
                selectionManager.clearSelection();
            }

            tagService.deleteTag(tag.getId());
            loadTags();
            showInformation("Tag deleted successfully.");
        } catch (Exception e) {
            showWarning("Failed to delete tag: " + e.getMessage());
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
