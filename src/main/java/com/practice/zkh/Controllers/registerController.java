package com.practice.zkh.Controllers;

import com.practice.zkh.dbs.DatabaseHandler;
import com.practice.zkh.zkhMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class registerController implements Initializable {
    Connection conn = new DatabaseHandler().getDBConnection();
    @FXML private ComboBox<String> cityCB, districtCB, streetCB, houseCB;
    @FXML private TextField electricityTF, flatTF, gasTF, hWaterTF, heatingTF,
            cWaterTF, areaTF, passwordTF, nameTF, residentsTF;
    @FXML private Label messageLabel;
    public ObservableList<String> cityList = FXCollections.observableArrayList();
    public ObservableList<String> districtList = FXCollections.observableArrayList();
    public ObservableList<String> streetList = FXCollections.observableArrayList();
    public ObservableList<String> houseList = FXCollections.observableArrayList();
    int maxFlatsInHouse = 0;
    int cityID, districtID, streetID, houseID;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = conn.prepareStatement("SELECT * FROM cities");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                cityList.add(resultSet.getString("name"));
            cityCB.setItems(cityList);
        } catch (Exception ignored){}
        cityCB.setOnAction(event -> {
            districtList.clear();
            streetList.clear();
            houseList.clear();
            streetCB.setItems(streetList);
            houseCB.setItems(houseList);
            try {
                PreparedStatement preparedStatementD = conn.prepareStatement("SELECT * FROM cities WHERE name=?");
                preparedStatementD.setString(1, cityCB.getValue());
                ResultSet resultSetD = preparedStatementD.executeQuery();
                resultSetD.next();
                cityID = resultSetD.getInt("city_id");
                preparedStatementD = conn.prepareStatement("SELECT * FROM districts WHERE city_id=?");
                preparedStatementD.setInt(1, cityID);
                resultSetD = preparedStatementD.executeQuery();
                while (resultSetD.next())
                    districtList.add(resultSetD.getString("name"));
                districtCB.setItems(districtList);
            } catch (Exception ignored){}
        });

        districtCB.setOnAction(event -> {
            streetList.clear();
            houseList.clear();
            houseCB.setItems(houseList);
            try {
                PreparedStatement preparedStatementD = conn.prepareStatement("SELECT * FROM districts WHERE name=? AND city_id=?");
                preparedStatementD.setString(1, districtCB.getValue());
                preparedStatementD.setInt(2, cityID);
                ResultSet resultSetD = preparedStatementD.executeQuery();
                resultSetD.next();
                districtID = resultSetD.getInt("district_id");
                preparedStatementD = conn.prepareStatement("SELECT * FROM streets WHERE district_id=?");
                preparedStatementD.setInt(1, districtID);
                resultSetD = preparedStatementD.executeQuery();
                while (resultSetD.next())
                    streetList.add(resultSetD.getString("name"));
                streetCB.setItems(streetList);
            } catch (Exception ignored){}
        });

        streetCB.setOnAction(event -> {
            houseList.clear();
            try {
                PreparedStatement preparedStatementD = conn.prepareStatement("SELECT * FROM streets WHERE name=? and district_id=?");
                preparedStatementD.setString(1, streetCB.getValue());
                preparedStatementD.setInt(2, districtID);
                ResultSet resultSetD = preparedStatementD.executeQuery();
                resultSetD.next();
                streetID = resultSetD.getInt("street_id");
                preparedStatementD = conn.prepareStatement("SELECT * FROM houses WHERE street_id=?");
                preparedStatementD.setInt(1, streetID);
                resultSetD = preparedStatementD.executeQuery();
                while (resultSetD.next())
                    houseList.add(String.valueOf(resultSetD.getInt("house_number")));
                houseCB.setItems(houseList);
            } catch (Exception ignored){}
        });

        houseCB.setOnAction(event -> {
            try {
                PreparedStatement preparedStatementD = conn.prepareStatement("SELECT * FROM houses WHERE house_number=?");
                preparedStatementD.setInt(1, Integer.parseInt(houseCB.getValue()));
                ResultSet resultSetD = preparedStatementD.executeQuery();
                resultSetD.next();
                maxFlatsInHouse = resultSetD.getInt("flat_count");
                houseID = Integer.parseInt(houseCB.getValue());
            } catch (Exception ignored){}
        });

    }

    public void register(){
        try {
            if(passwordTF.getText().trim().equals("") || nameTF.getText().trim().equals("") ||
                    residentsTF.getText().trim().equals("") || areaTF.getText().trim().equals(""))
                messageLabel.setText("Не введены данные");
            else {
                if (Integer.parseInt(flatTF.getText().trim()) <= maxFlatsInHouse){
                    String citizenID = cityID + "-" + districtID + "-" + streetID + "-" + houseID + "-" + Integer.parseInt(flatTF.getText().trim());
                    PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM citizens WHERE citizen_id=?");
                    preparedStatement.setString(1, citizenID);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()){
                        preparedStatement = conn.prepareStatement("INSERT INTO citizens VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        preparedStatement.setString(1, citizenID);
                        preparedStatement.setString(2, passwordTF.getText().trim());
                        preparedStatement.setString(3, nameTF.getText().trim());
                        preparedStatement.setString(4, cityCB.getValue());
                        preparedStatement.setString(5, districtCB.getValue());
                        preparedStatement.setString(6, streetCB.getValue());
                        preparedStatement.setString(7, houseCB.getValue());
                        preparedStatement.setInt(8, Integer.parseInt(flatTF.getText().trim()));
                        preparedStatement.setFloat(9, Float.parseFloat(electricityTF.getText().trim()));
                        preparedStatement.setFloat(10, Float.parseFloat(hWaterTF.getText().trim()));
                        preparedStatement.setFloat(11, Float.parseFloat(cWaterTF.getText().trim()));
                        preparedStatement.setFloat(12, Float.parseFloat(heatingTF.getText().trim()));
                        preparedStatement.setFloat(13, Float.parseFloat(gasTF.getText().trim()));
                        preparedStatement.setInt(14, Integer.parseInt(residentsTF.getText().trim()));
                        preparedStatement.setFloat(15, Float.parseFloat(areaTF.getText().trim()));
                        preparedStatement.executeUpdate();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Успещная регистрация");
                        alert.setContentText("Ваш номер для авторизации: " + citizenID);
                        alert.showAndWait();
                        FXMLLoader fxmlLoader = new FXMLLoader(zkhMain.class.getResource("fxmls/login.fxml"));
                        openNewWindow(fxmlLoader);
                    } else messageLabel.setText("Этот адрес уже зарегистрирован");
                } else messageLabel.setText("Введен неправильный номер квартиры");
            }
        } catch (Exception ignored){messageLabel.setText("Введены неправильные данные");}
    }

    private void openNewWindow(FXMLLoader fxmlLoader) throws java.io.IOException {
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        Stage stage = new Stage();
        stage.setTitle("Расчёт кварт. платы");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:src/main/resources/com/practice/zkh/images/logo.jpg"));
        stage.show();
        messageLabel.getScene().getWindow().hide();
    }
}