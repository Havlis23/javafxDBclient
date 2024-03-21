package com.davidhavel.javasql;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Super Duper SQL Connector");
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApp.class.getResource("/com/davidhavel/javasql/img/app-icon.jpg").toString()));
        scene.getStylesheets().add(MainApp.class.getResource("/com/davidhavel/javasql/main.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}