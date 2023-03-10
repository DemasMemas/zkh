package com.practice.zkh.Controllers;

import com.practice.zkh.dbs.DatabaseHandler;
import com.practice.zkh.zkhMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.SpinnerValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class managerController implements Initializable {
    Connection conn = new DatabaseHandler().getDBConnection();

    @FXML private Spinner<Double> cWaterS, capitalRepairS, electricityS, gasS, hWaterS, heatingS, repairS, waterDisposalS;
    @FXML private ComboBox<String> cityCB, districtCB, streetCB;
    @FXML private TextField flatTF, houseTF;

    public ObservableList<String> cityList = FXCollections.observableArrayList();
    public ObservableList<String> districtList = FXCollections.observableArrayList();
    public ObservableList<String> streetList = FXCollections.observableArrayList();
    int flatsInHouse, cityID, districtID, streetID, houseID;

    public void addAddress() {
        flatsInHouse = Integer.parseInt(flatTF.getText().trim());
        houseID = Integer.parseInt(houseTF.getText().trim());
        try {
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            preparedStatement = conn.prepareStatement("SELECT * FROM cities WHERE name=?");
            preparedStatement.setString(1, cityCB.getValue());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                preparedStatement = conn.prepareStatement("SELECT * FROM districts WHERE name=? AND city_id=?");
                preparedStatement.setString(1, districtCB.getValue());
                preparedStatement.setInt(2, cityID);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    preparedStatement = conn.prepareStatement("SELECT * FROM streets WHERE name=? AND district_id=?");
                    preparedStatement.setString(1, streetCB.getValue());
                    preparedStatement.setInt(2, districtID);
                    resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()){
                        preparedStatement = conn.prepareStatement("INSERT INTO streets VALUES (street_id,?,?)");
                        preparedStatement.setInt(1, districtID);
                        preparedStatement.setString(2, streetCB.getValue());
                        preparedStatement.executeUpdate();

                        preparedStatement = conn.prepareStatement("SELECT * FROM streets WHERE name=? and district_id=?");
                        preparedStatement.setString(1, streetCB.getValue());
                        preparedStatement.setInt(2, districtID);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        streetID = resultSet.getInt("street_id");
                    }
                } else {
                    insertDistrict();
                }
            } else{
                preparedStatement = conn.prepareStatement("INSERT INTO cities VALUES (city_id,?)");
                preparedStatement.setString(1, cityCB.getValue());
                preparedStatement.executeUpdate();

                preparedStatement = conn.prepareStatement("SELECT * FROM cities WHERE name=?");
                preparedStatement.setString(1, cityCB.getValue());
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                cityID = resultSet.getInt("city_id");

                insertDistrict();
            }

            preparedStatement = conn.prepareStatement("SELECT * FROM cities, districts, " +
                    "streets, houses WHERE cities.city_id = ? AND districts.district_id = ? " +
                    "AND streets.street_id = ? AND houses.street_id = ? AND houses.house_number = ?");
            preparedStatement.setInt(1, cityID);
            preparedStatement.setInt(2, districtID);
            preparedStatement.setInt(3, streetID);
            preparedStatement.setInt(4, streetID);
            preparedStatement.setInt(5, houseID);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()){
                preparedStatement = conn.prepareStatement("INSERT INTO houses VALUES (?,?,?,?)");
                preparedStatement.setInt(1, houseID);
                preparedStatement.setInt(2, streetID);
                preparedStatement.setInt(3, Integer.parseInt(zkhMain.userID));
                preparedStatement.setInt(4, flatsInHouse);
                preparedStatement.executeUpdate();
            } else {
                System.out.println(resultSet.getString("street_id"));
                houseTF.setPromptText("Этот адрес уже занят");
                houseTF.setText("");
            }
        } catch (Exception ignored){        }
    }

    private void insertDistrict() throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        preparedStatement = conn.prepareStatement("INSERT INTO districts VALUES (district_id,?,?)");
        preparedStatement.setInt(1, cityID);
        preparedStatement.setString(2, districtCB.getValue());
        preparedStatement.executeUpdate();

        preparedStatement = conn.prepareStatement("SELECT * FROM districts WHERE name=? AND city_id=?");
        preparedStatement.setString(1, districtCB.getValue());
        preparedStatement.setInt(2, cityID);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        districtID = resultSet.getInt("district_id");

        preparedStatement = conn.prepareStatement("INSERT INTO streets VALUES (street_id,?,?)");
        preparedStatement.setInt(1, districtID);
        preparedStatement.setString(2, streetCB.getValue());
        preparedStatement.executeUpdate();

        preparedStatement = conn.prepareStatement("SELECT * FROM streets WHERE name=? and district_id=?");
        preparedStatement.setString(1, streetCB.getValue());
        preparedStatement.setInt(2, districtID);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        streetID = resultSet.getInt("street_id");
    }

    public void update() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE management_company SET electricity_tariff = ?, hot_water_tariff = ?, " +
                            "cold_water_tariff = ?, heating_tariff = ?, gas_tariff = ?, water_disposing_tariff = ?," +
                            "repair_tariff = ?, capital_repair_tariff = ? WHERE management_company_id = ?");
            preparedStatement.setDouble(1, electricityS.getValue());
            preparedStatement.setDouble(2, hWaterS.getValue());
            preparedStatement.setDouble(3, cWaterS.getValue());
            preparedStatement.setDouble(4, heatingS.getValue());
            preparedStatement.setDouble(5, gasS.getValue());
            preparedStatement.setDouble(6, waterDisposalS.getValue());
            preparedStatement.setDouble(7, repairS.getValue());
            preparedStatement.setDouble(8, capitalRepairS.getValue());
            preparedStatement.setString(9, zkhMain.userID);
            preparedStatement.executeUpdate();
        } catch (Exception ignored){}
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM management_company WHERE management_company_id=?");
            preparedStatement.setString(1, zkhMain.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            gasS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("gas_tariff")).doubleValue()));
            hWaterS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("hot_water_tariff")).doubleValue()));
            cWaterS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("cold_water_tariff")).doubleValue()));
            heatingS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("heating_tariff")).doubleValue()));
            electricityS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("electricity_tariff")).doubleValue()));
            waterDisposalS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("water_disposing_tariff")).doubleValue()));
            repairS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("repair_tariff")).doubleValue()));
            capitalRepairS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("capital_repair_tariff")).doubleValue()));

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
                streetCB.setItems(streetList);
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
                try {
                    PreparedStatement preparedStatementD = conn.prepareStatement("SELECT * FROM streets WHERE name=? and district_id=?");
                    preparedStatementD.setString(1, streetCB.getValue());
                    preparedStatementD.setInt(2, districtID);
                    ResultSet resultSetD = preparedStatementD.executeQuery();
                    resultSetD.next();
                    streetID = resultSetD.getInt("street_id");
                } catch (Exception ignored){}
            });
        } catch (Exception ignored){        }
    }
}
