package com.dpzstudio.timer;

import com.dpzstudio.timer.util.DatabaseInitializer;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.init();

        scene = new Scene(loadFXML("/views/home"), 400, 500);

        // NOTE : Link CSS

        scene.getStylesheets().add(getClass().getResource("/styles/default-themes.css").toExternalForm());

        InputStream icon = getClass().getResourceAsStream("/icons/app.png");
        if (icon != null) {
            Image appIcon = new Image(icon);
            stage.getIcons().add(appIcon);
        }

        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(400);
        stage.setX(0);
        stage.setY(0);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
