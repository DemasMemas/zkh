package com.practice.zkh.Controllers;

import com.practice.zkh.dbs.DatabaseHandler;
import com.practice.zkh.zkhMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class authorizeController {
    Connection conn = new DatabaseHandler().getDBConnection();
    @FXML private TextField numberField;
    @FXML private PasswordField passwordField;

    public void login() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement
                    ("SELECT citizen_id,password,name FROM citizens WHERE citizen_id=? AND password =?");
            preparedStatement.setString(1, numberField.getText().trim());
            preparedStatement.setString(2, passwordField.getText().trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                zkhMain.userName = resultSet.getString("name");
                zkhMain.userID = numberField.getText().trim();
                // сменить окно на жителя
                FXMLLoader fxmlLoader = new FXMLLoader(zkhMain.class.getResource("fxmls/citizen.fxml"));
                openNewWindow(fxmlLoader, 400, 625);
            } else {
                preparedStatement = conn.prepareStatement
                        ("SELECT management_company_id,password FROM management_company WHERE management_company_id=? AND password =?");
                preparedStatement.setString(1, numberField.getText().trim());
                preparedStatement.setString(2, passwordField.getText().trim());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    zkhMain.userID = numberField.getText().trim();
                    // сменить окно на управляющего
                    FXMLLoader fxmlLoader = new FXMLLoader(zkhMain.class.getResource("fxmls/manager.fxml"));
                    openNewWindow(fxmlLoader, 400, 505);
                } else {
                    numberField.setText("");
                    numberField.setPromptText("Неверные данные");
                }
            }

        } catch (Exception ignored) {
        }
    }

    private void openNewWindow(FXMLLoader fxmlLoader, int width, int height) throws java.io.IOException {
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        Stage stage = new Stage();
        stage.setTitle("Расчёт кварт. платы");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:src/main/resources/com/practice/zkh/images/logo.jpg"));
        stage.show();
        numberField.getScene().getWindow().hide();
    }

    public void register() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(zkhMain.class.getResource("fxmls/register.fxml"));
        openNewWindow(fxmlLoader, 450, 335);
    }

}
