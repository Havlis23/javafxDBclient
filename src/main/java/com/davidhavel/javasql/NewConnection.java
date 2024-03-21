package com.davidhavel.javasql;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NewConnection {

    @FXML
    private TextField hostDB;

    @FXML
    private TextField dbName;

    @FXML
    private TextField dbPass;

    @FXML
    private TextField dbPort;

    @FXML
    private TextField dbUser;

    public void connectToDB(ActionEvent event) {
        String host = hostDB.getText();
        String name = dbName.getText();
        String user = dbUser.getText();
        String pass = dbPass.getText();
        String port = dbPort.getText();

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name;

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Připojeno: " + url);

            AppController.setConnectionDetails(host, name, user, pass);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();

        } catch (SQLException | IOException e) {
            System.err.println("Nepřipojeno: " + e.getMessage());
        }
    }

    public void testConnection(ActionEvent event) {
        String host = hostDB.getText();
        String name = dbName.getText();
        String user = dbUser.getText();
        String pass = dbPass.getText();
        String port = dbPort.getText();

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name;

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Úspěšné navázání spojení s DB: " + url);
            saveConnectionDetails(url, user, pass);

        } catch (SQLException e) {
            System.err.println("Chyba navázaní spojení s DB: " + e.getMessage());
        }
    }

    private void saveConnectionDetails(String url, String user, String pass) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("connectionDetails.txt", true))) {
            writer.write("URL: " + url);
            writer.newLine();
            writer.write("User: " + user);
            writer.newLine();
            writer.write("Password: " + pass);
            writer.newLine();
            writer.write("--------------------");
            writer.newLine();
            System.out.println("Podrobnosti připojení uloženy do souboru");
        } catch (IOException e) {
            System.err.println("Chyba při zápisu do souboru: " + e.getMessage());
        }
    }

}
