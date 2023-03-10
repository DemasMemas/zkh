package com.practice.zkh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class zkhMain extends Application {
    public static String userName;
    public static String userID;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(zkhMain.class.getResource("fxmls/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("Расчёт кварт. платы");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:src/main/resources/com/practice/zkh/images/logo.jpg"));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}