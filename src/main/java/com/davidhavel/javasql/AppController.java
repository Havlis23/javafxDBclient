package com.davidhavel.javasql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    private ComboBox<String> chooseTable;

    @FXML
    private Label choosedTable;

    @FXML
    private TableView<ObservableList<String>> recordsTable;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu newConnectionItem2;


    private static String user = "it-davidhavel";
    private static String pass = "aSdf.1234";
    private static String host = "sql.stredniskola.com:3306";
    private static String dbname = "davidhavel";

    private Connection connection;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF8", user, pass);
            System.out.println("Spojení s DB navázáno");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            ObservableList<String> tableNames = FXCollections.observableArrayList();
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }

            List<String> dbNames = getConnectedDatabases();
            for (String dbName : dbNames) {
                MenuItem dbItem = new MenuItem(dbName);
                dbItem.setOnAction(event -> {
                    String dbNameFromMenuItem = ((MenuItem) event.getSource()).getText();

                    // spojeni s db a nacteni connection details
                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbNameFromMenuItem + "?characterEncoding=UTF8", user, pass);
                        System.out.println("Připojen do DB: " + dbNameFromMenuItem);
                        AppController.setConnectionDetails(host, dbNameFromMenuItem, user, pass);
                    } catch (SQLException e) {
                        System.err.println("Error connecting to DB: " + e.getMessage());
                    }
                });
                newConnectionItem2.getItems().add(dbItem);
            }

            chooseTable.setItems(tableNames);

            chooseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    choosedTable.setText("Zvolená tabulka: " + newSelection);
                    System.out.println("Zvolená tabulka: " + newSelection);
                }
            });
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                        System.out.println("Spojení s DB ukončeno");
                    }
                } catch (SQLException e) {
                    System.err.println("Chyba při ukončování spojení s DB: " + e.getMessage());
                }
            }));

        } catch (SQLException e) {
            System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
        }
        // vytvoreni noveho menu v menubaru
        Menu newWindowMenu = new Menu("Připojení");

        Menu connectedDBs = new Menu("Databáze");

        // vytvorení nového MenuItem
        MenuItem newConnectionItem = new MenuItem("Nové připojení");

        MenuItem newConnectionItem2 = new MenuItem("");

        newConnectionItem.setOnAction(event -> {
            Stage noveOkno = new Stage();
            noveOkno.setTitle("Nové připojení");

            try {
                // Load the FXML file
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("newConnection.fxml"));
                // Create the Scene
                Scene scene = new Scene(fxmlLoader.load());
                // Set the Scene on the Stage
                noveOkno.setScene(scene);
            } catch (IOException e) {
                System.err.println("Chyba vytvoření nového okna: " + e);
            }

            // Show the new Stage
            noveOkno.show();

            // TODO: Establish a new connection to the new database
        });

// Add the MenuItem to the Menu
        newWindowMenu.getItems().add(newConnectionItem);

// Add the Menu to the MenuBar
        menuBar.getMenus().add(newWindowMenu);
    }

    public List<String> getConnectedDatabases() {
        List<String> dbNames = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("connectionDetails.txt"));
            for (String line : lines) {
                if (line.startsWith("URL: ")) {
                    String dbName = line.substring(line.lastIndexOf("/") + 1);
                    dbNames.add(dbName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading connectionDetails.txt: " + e.getMessage());
        }
        return dbNames;
    }

    public void getRecord(ActionEvent event) {
        String selectedTable = chooseTable.getValue();
        if (selectedTable == null)
            return;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbname + "?characterEncoding=UTF8", user, pass)) {
            System.out.println("Spojení s DB navázáno");

            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedTable);

            recordsTable.getColumns().clear();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
                final int columnIndex = i - 1;
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));
                recordsTable.getColumns().add(column);
            }

            ObservableList<ObservableList<String>> records = FXCollections.observableArrayList();

            resultSet.beforeFirst(); // absolutne neivm, jak fahruji kursory v Javě, ale fahruje to

            while (resultSet.next()) {
                ObservableList<String> record = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    record.add(resultSet.getString(i));
                }
                records.add(record);
            }

            recordsTable.setItems(records);
            System.out.println("Záznamy z tabulky: " + recordsTable.getItems());
        } catch (SQLException e) {
            System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
        }


    }

    public static void setConnectionDetails(String host, String name, String user, String pass) {
        AppController.host = host;
        AppController.dbname = name;
        AppController.user = user;
        AppController.pass = pass;
    }


}