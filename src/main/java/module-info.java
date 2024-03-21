module com.davidhavel.javasql {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens com.davidhavel.javasql to javafx.fxml;
    exports com.davidhavel.javasql;
}