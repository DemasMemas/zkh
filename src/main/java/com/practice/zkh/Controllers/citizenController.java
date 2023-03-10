package com.practice.zkh.Controllers;

import com.practice.zkh.dbs.DatabaseHandler;
import com.practice.zkh.dbo.Offer;
import com.practice.zkh.zkhMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class citizenController implements Initializable {
    Connection conn = new DatabaseHandler().getDBConnection();
    @FXML private Spinner<Double> cWaterS, electricityS, gasS, hWaterS, heatingS;
    @FXML private Label nameLabel;
    @FXML private TableColumn<Offer, String> offerC, summaryC, countC;
    @FXML private TableView<Offer> priceTable;
    ObservableList<Offer> offers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        offerC.setCellValueFactory(new PropertyValueFactory<>("name"));
        summaryC.setCellValueFactory(new PropertyValueFactory<>("summary"));
        countC.setCellValueFactory(new PropertyValueFactory<>("count"));

        nameLabel.setText("Добро пожаловать, " + zkhMain.userName + "!");
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM citizens WHERE citizen_id=?");
            preparedStatement.setString(1, zkhMain.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            gasS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("gas_counter")).doubleValue()));
            hWaterS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("hot_water_counter")).doubleValue()));
            cWaterS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("cold_water_counter")).doubleValue()));
            heatingS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("heating_counter")).doubleValue()));
            electricityS.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000, BigDecimal.valueOf(resultSet.getDouble("electricity_counter")).doubleValue()));
        } catch (Exception ignored){        }
    }

    public void countAll() {
        BigDecimal summary = new BigDecimal(0);
        refreshSpinners();
        BigDecimal gas_tariff = new BigDecimal(0), hot_water_tariff = new BigDecimal(0), cold_water_tariff = new BigDecimal(0), electricity_tariff = new BigDecimal(0), heating_tariff = new BigDecimal(0),
                water_disposing_tariff = new BigDecimal(0), capital_repair_tariff = new BigDecimal(0), repair_tariff = new BigDecimal(0), area = new BigDecimal(0);
        int residents = 0;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM citizens WHERE citizen_id=?");
            preparedStatement.setString(1, zkhMain.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String city = resultSet.getString("city");
            String district = resultSet.getString("district");
            String street = resultSet.getString("street");
            String house = resultSet.getString("house");
            area = BigDecimal.valueOf(resultSet.getDouble("flat_area"));
            residents = resultSet.getInt("residents");

            preparedStatement = conn.prepareStatement("SELECT houses.management_company_id FROM " +
                    "houses,cities,districts,streets WHERE cities.name=? AND districts.name=? AND streets.name=? " +
                    "AND house_number=?");
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, district);
            preparedStatement.setString(3, street);
            preparedStatement.setInt(4, Integer.parseInt(house));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            preparedStatement = conn.prepareStatement("SELECT * FROM management_company WHERE management_company_id=?");
            preparedStatement.setInt(1, resultSet.getInt("management_company_id"));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            gas_tariff = BigDecimal.valueOf(resultSet.getDouble("gas_tariff"));
            hot_water_tariff = BigDecimal.valueOf(resultSet.getDouble("hot_water_tariff"));
            cold_water_tariff = BigDecimal.valueOf(resultSet.getDouble("cold_water_tariff"));
            electricity_tariff = BigDecimal.valueOf(resultSet.getDouble("electricity_tariff"));
            heating_tariff = BigDecimal.valueOf(resultSet.getDouble("heating_tariff"));
            water_disposing_tariff = BigDecimal.valueOf(resultSet.getDouble("water_disposing_tariff"));
            capital_repair_tariff = BigDecimal.valueOf(resultSet.getDouble("capital_repair_tariff"));
            repair_tariff = BigDecimal.valueOf(resultSet.getDouble("repair_tariff"));
        } catch (Exception ignored){}

        offers.clear();
        if (electricityS.getValue() != 0){
            offers.add(new Offer("Электроэнергия", electricityS.getValue() + " кВт*ч", electricity_tariff.multiply(BigDecimal.valueOf(electricityS.getValue())) + " ₽"));
            summary = summary.add(electricity_tariff.multiply(BigDecimal.valueOf(electricityS.getValue())));
        }
        else {
            offers.add(new Offer("Электроэнергия", "Нет счётчика", electricity_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(electricity_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (hWaterS.getValue() != 0){
            offers.add(new Offer("Горячая вода", hWaterS.getValue() + " м3",hot_water_tariff.multiply(BigDecimal.valueOf(hWaterS.getValue())) + " ₽"));
            summary = summary.add(hot_water_tariff.multiply(BigDecimal.valueOf(hWaterS.getValue())));
        }
        else {
            offers.add(new Offer("Горячая вода", "Нет счётчика", hot_water_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(hot_water_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (cWaterS.getValue() != 0){
            offers.add(new Offer("Холодная вода", cWaterS.getValue() + " м3",cold_water_tariff.multiply(BigDecimal.valueOf(cWaterS.getValue())) + " ₽"));
            summary = summary.add(cold_water_tariff.multiply(BigDecimal.valueOf(cWaterS.getValue())));
        }
        else {
            offers.add(new Offer("Холодная вода", "Нет счётчика", cold_water_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(cold_water_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (heatingS.getValue() != 0){
            offers.add(new Offer("Отопление", heatingS.getValue() + " ГКал",heating_tariff.multiply(BigDecimal.valueOf(heatingS.getValue())) + " ₽"));
            summary = summary.add(heating_tariff.multiply(BigDecimal.valueOf(heatingS.getValue())));
        }
        else {
            offers.add(new Offer("Отопление", "Нет счётчика", heating_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(heating_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (gasS.getValue() != 0){
            offers.add(new Offer("Газ", gasS.getValue() + " м3", gas_tariff.multiply(BigDecimal.valueOf(gasS.getValue())) + " ₽"));
            summary = summary.add(gas_tariff.multiply(BigDecimal.valueOf(gasS.getValue())));
        }
        else {
            offers.add(new Offer("Газ", "Нет счётчика", gas_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(gas_tariff.multiply(BigDecimal.valueOf(60)));
        }

        offers.add(new Offer("Ремонт", "-", repair_tariff.multiply(area) + " ₽"));
        summary = summary.add(repair_tariff.multiply(area));
        offers.add(new Offer("Капитальный ремонт", "-", capital_repair_tariff.multiply(BigDecimal.valueOf(residents)) + " ₽"));
        summary = summary.add(capital_repair_tariff.multiply(BigDecimal.valueOf(residents)));
        offers.add(new Offer("Водоотвод", hWaterS.getValue() + cWaterS.getValue() + " м3",
                water_disposing_tariff.multiply(BigDecimal.valueOf((hWaterS.getValue() + cWaterS.getValue()))) + " ₽"));
        summary = summary.add(water_disposing_tariff.multiply(BigDecimal.valueOf((hWaterS.getValue() + cWaterS.getValue()))));
        offers.add(new Offer("Итого", "-", summary + " ₽"));
        priceTable.setItems(offers);
    }

    public void save() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE citizens SET electricity_counter = ?, hot_water_counter = ?, " +
                            "cold_water_counter = ?, heating_counter = ?, gas_counter = ? WHERE citizen_id = ?");
            preparedStatement.setDouble(1, electricityS.getValue());
            preparedStatement.setDouble(2, hWaterS.getValue());
            preparedStatement.setDouble(3, cWaterS.getValue());
            preparedStatement.setDouble(4, heatingS.getValue());
            preparedStatement.setDouble(5, gasS.getValue());
            preparedStatement.setString(6, zkhMain.userID);
            preparedStatement.executeUpdate();
        } catch (Exception ignored){}
    }

    public void countLast() {
        BigDecimal summary = new BigDecimal(0);
        BigDecimal gas_tariff = new BigDecimal(0), hot_water_tariff = new BigDecimal(0), cold_water_tariff = new BigDecimal(0), electricity_tariff = new BigDecimal(0), heating_tariff = new BigDecimal(0),
                water_disposing_tariff = new BigDecimal(0), capital_repair_tariff = new BigDecimal(0), repair_tariff = new BigDecimal(0), area = new BigDecimal(0),
                hWater = new BigDecimal(0), cWater = new BigDecimal(0), heating = new BigDecimal(0), electricity = new BigDecimal(0), gas = new BigDecimal(0);
        int residents = 0;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM citizens WHERE citizen_id=?");
            preparedStatement.setString(1, zkhMain.userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String city = resultSet.getString("city");
            String district = resultSet.getString("district");
            String street = resultSet.getString("street");
            String house = resultSet.getString("house");
            area = BigDecimal.valueOf(resultSet.getDouble("flat_area"));
            residents = resultSet.getInt("residents");

            hWater = BigDecimal.valueOf(resultSet.getDouble("hot_water_counter"));
            cWater = BigDecimal.valueOf(resultSet.getDouble("cold_water_counter"));
            heating = BigDecimal.valueOf(resultSet.getDouble("heating_counter"));
            electricity = BigDecimal.valueOf(resultSet.getDouble("electricity_counter"));
            gas = BigDecimal.valueOf(resultSet.getDouble("gas_counter"));

            preparedStatement = conn.prepareStatement("SELECT houses.management_company_id FROM " +
                    "houses,cities,districts,streets WHERE cities.name=? AND districts.name=? AND streets.name=? " +
                    "AND house_number=?");
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, district);
            preparedStatement.setString(3, street);
            preparedStatement.setInt(4, Integer.parseInt(house));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            preparedStatement = conn.prepareStatement("SELECT * FROM management_company WHERE management_company_id=?");
            preparedStatement.setInt(1, resultSet.getInt("management_company_id"));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            gas_tariff = BigDecimal.valueOf(resultSet.getDouble("gas_tariff"));
            hot_water_tariff = BigDecimal.valueOf(resultSet.getDouble("hot_water_tariff"));
            cold_water_tariff = BigDecimal.valueOf(resultSet.getDouble("cold_water_tariff"));
            electricity_tariff = BigDecimal.valueOf(resultSet.getDouble("electricity_tariff"));
            heating_tariff = BigDecimal.valueOf(resultSet.getDouble("heating_tariff"));
            water_disposing_tariff = BigDecimal.valueOf(resultSet.getDouble("water_disposing_tariff"));
            capital_repair_tariff = BigDecimal.valueOf(resultSet.getDouble("capital_repair_tariff"));
            repair_tariff = BigDecimal.valueOf(resultSet.getDouble("repair_tariff"));
        } catch (Exception ignored){}

        offers.clear();
        if (electricityS.getValue() != 0){
            offers.add(new Offer("Электроэнергия", BigDecimal.valueOf(electricityS.getValue()).subtract(electricity) + " кВт*ч",
                    electricity_tariff.multiply(BigDecimal.valueOf(electricityS.getValue()).subtract(electricity)) + " ₽"));
            summary = summary.add(electricity_tariff.multiply(BigDecimal.valueOf(electricityS.getValue()).subtract(electricity)));
        }
        else {
            offers.add(new Offer("Электроэнергия", "Нет счётчика", electricity_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(electricity_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (hWaterS.getValue() != 0){
            offers.add(new Offer("Горячая вода", BigDecimal.valueOf(hWaterS.getValue()).subtract(hWater) + " м3",
                    hot_water_tariff.multiply(BigDecimal.valueOf(hWaterS.getValue()).subtract(hWater)) + " ₽"));
            summary = summary.add(electricity_tariff.multiply(BigDecimal.valueOf(electricityS.getValue()).subtract(electricity)));
        }
        else{
            offers.add(new Offer("Горячая вода", "Нет счётчика", hot_water_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(hot_water_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (cWaterS.getValue() != 0){
            offers.add(new Offer("Холодная вода", BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater) + " м3",
                    cold_water_tariff.multiply(BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater)) + " ₽"));
            summary = summary.add(cold_water_tariff.multiply(BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater)));
        }
        else {
            offers.add(new Offer("Холодная вода", "Нет счётчика", cold_water_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(cold_water_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (heatingS.getValue() != 0){
            offers.add(new Offer("Отопление", BigDecimal.valueOf(heatingS.getValue()).subtract(heating) + " ГКал",
                    heating_tariff.multiply(BigDecimal.valueOf(heatingS.getValue()).subtract(heating)) + " ₽"));
            summary = summary.add(heating_tariff.multiply(BigDecimal.valueOf(heatingS.getValue()).subtract(heating)));
        }
        else {
            offers.add(new Offer("Отопление", "Нет счётчика", heating_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(heating_tariff.multiply(BigDecimal.valueOf(60)));
        }

        if (gasS.getValue() != 0){
            offers.add(new Offer("Газ", BigDecimal.valueOf(gasS.getValue()).subtract(gas) + " м3",
                    BigDecimal.valueOf(gasS.getValue()).subtract(gas).multiply(gas_tariff) + " ₽"));
            summary = summary.add(gas_tariff.multiply(BigDecimal.valueOf(gasS.getValue()).subtract(gas)));
        }
        else {
            offers.add(new Offer("Газ", "Нет счётчика", gas_tariff.multiply(BigDecimal.valueOf(60)) + " ₽"));
            summary = summary.add(gas_tariff.multiply(BigDecimal.valueOf(60)));
        }

        offers.add(new Offer("Ремонт", "-",repair_tariff.multiply(area) + " ₽"));
        summary = summary.add(repair_tariff.multiply(area));
        offers.add(new Offer("Капитальный ремонт", "-", capital_repair_tariff.multiply(BigDecimal.valueOf(residents)) + " ₽"));
        summary = summary.add(capital_repair_tariff.multiply(BigDecimal.valueOf(residents)));
        offers.add(new Offer("Водоотвод", (BigDecimal.valueOf(hWaterS.getValue()).subtract(hWater).add(BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater))) + " м3",
                 water_disposing_tariff.multiply((BigDecimal.valueOf(hWaterS.getValue()).subtract(hWater).add(BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater)))) + " ₽"));
        summary = summary.add(water_disposing_tariff.multiply((BigDecimal.valueOf(hWaterS.getValue()).subtract(hWater).add(BigDecimal.valueOf(cWaterS.getValue()).subtract(cWater)))));
        offers.add(new Offer("Итого", "-", summary + " ₽"));
        priceTable.setItems(offers);
    }

    public void refreshSpinners(){
        electricityS.increment();
        electricityS.decrement();
        hWaterS.increment();
        hWaterS.decrement();
        cWaterS.increment();
        cWaterS.decrement();
        heatingS.increment();
        heatingS.decrement();
    }
}
