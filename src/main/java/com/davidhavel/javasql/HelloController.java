package com.davidhavel.javasql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label fname;
    @FXML
    private ComboBox<String> chooseTable;

    @FXML
    private ComboBox<String> records;

    @FXML
    private Label choosedTable;

    public void getRecord(ActionEvent event) {
        String selectedTable = chooseTable.getValue();
        if (selectedTable != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("Chybí driver na MySQL:" + e.getMessage());
            }
            String user = "it-davidhavel";
            String pass = "aSdf.1234";
            String host = "sql.stredniskola.com:3306";
            String dbname = "davidhavel";
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF8", user, pass)) {
                System.out.println("Spojení s DB navázáno");

                // Execute a SQL query to fetch the first record from the selected table
                Statement statement = connection.createStatement();
                // Fetch the first record from the selected table
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedTable + " LIMIT 1");

                // Display the fetched record in the fname label
                if (resultSet.next()) {
                    fname.setText(resultSet.getString(1));
                } else {
                    // If no records are found in the selected table, display a message in the fname label
                    fname.setText("No records found in the selected table.");
                }

            } catch (SQLException e) {
                // If an error occurs while connecting to the database, display an error message
                System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
            }
        } else {
            fname.setText("No table selected.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Chybí driver na MySQL:" + e.getMessage());
        }
        String user = "it-davidhavel";
        String pass = "aSdf.1234";
        String host = "sql.stredniskola.com:3306";
        String dbname = "davidhavel";
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF8", user, pass)){
            System.out.println("Spojení s DB navázáno");

            // Get all table names
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            ObservableList<String> tableNames = FXCollections.observableArrayList();
            while(tables.next()){
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            chooseTable.setItems(tableNames);

            // Add listener to update choosedTable label when a new item is selected
            chooseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    choosedTable.setText("Zvolená tabulka: " + newSelection);
                    System.out.println("Zvolená tabulka: " + newSelection);
                }
            });

        } catch (SQLException e) {
            System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
        }
    }
}