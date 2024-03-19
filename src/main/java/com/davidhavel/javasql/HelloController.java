package com.davidhavel.javasql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;


import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private ComboBox<String> chooseTable;

    @FXML
    private ComboBox<String> records;

    @FXML
    private Label choosedTable;

    @FXML
    private TableView recordsTable;


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

                // Create a Statement that generates ResultSet objects that are scrollable.
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedTable);

                // Clear the existing columns
                recordsTable.getColumns().clear();

                // Get the column names from the ResultSetMetaData
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Create a TableColumn for each column in the ResultSet
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);

                    // Create a new TableColumn
                    TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);

                    // Set the cell value factory
                    final int columnIndex = i - 1;
                    column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));

                    // Add the column to the TableView
                    recordsTable.getColumns().add(column);
                }

                // Create an ObservableList to hold the records
                ObservableList<ObservableList<String>> records = FXCollections.observableArrayList();

                // Iterate over the ResultSet again to add the records to the TableView
                resultSet.beforeFirst(); // Reset cursor position
                while (resultSet.next()) {
                    // For each record, create a new ObservableList
                    ObservableList<String> record = FXCollections.observableArrayList();

                    // Add each column value to the list
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        record.add(resultSet.getString(i));
                    }

                    // Add the list of column values (which represents a record) to the list of records
                    records.add(record);
                }

                // Set the items of the TableView to the list of records
                recordsTable.setItems(records);

            } catch (SQLException e) {
                // If an error occurs while connecting to the database, display an error message
                System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
            }
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
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF8", user, pass)) {
            System.out.println("Spojení s DB navázáno");

            // Get all table names
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            ObservableList<String> tableNames = FXCollections.observableArrayList();
            while (tables.next()) {
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