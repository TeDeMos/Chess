package com.example.chesss;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChessApplication2 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication2.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 880, 720);
        stage.setMinWidth(220);
        stage.setMinHeight(180);
        stage.setTitle("Chess");
        ((MenuController) fxmlLoader.getController()).prepare(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}