package com.practice.zkh.dbs;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHandler {
    Connection conn;
    String url = "jdbc:mysql://127.0.0.1:3306/zkh";
    String username = "root";
    String password = "root";

    public DatabaseHandler(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception ignored){}
    }

    public Connection getDBConnection(){return conn;}
}
