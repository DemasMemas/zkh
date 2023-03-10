module com.practice.zkh {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.practice.zkh to javafx.fxml;
    exports com.practice.zkh;
    exports com.practice.zkh.Controllers;
    opens com.practice.zkh.Controllers to javafx.fxml;
    exports com.practice.zkh.dbo;
    opens com.practice.zkh.dbo to javafx.fxml;
    exports com.practice.zkh.dbs;
    opens com.practice.zkh.dbs to javafx.fxml;
}