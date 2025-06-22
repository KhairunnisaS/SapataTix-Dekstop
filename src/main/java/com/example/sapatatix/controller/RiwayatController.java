package com.example.sapatatix.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RiwayatController implements Initializable {

    @FXML
    private Button eventsBtn;

    @FXML
    private Button createEventNavBtn;

    @FXML
    private Button historyNavBtn;

    @FXML
    private Button profileNavBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private VBox historyContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tidak ada listener langsung di sini karena tombol sudah pakai onAction di FXML
    }

    @FXML
    private void handleEventsNav(ActionEvent event) {
        loadPage(event, "/com/example/sapatatix/fxml/Dashboard.fxml");
    }

    @FXML
    private void handleCreateEventNav(ActionEvent event) {
        loadPage(event, "/com/example/sapatatix/fxml/BuatEvent.fxml");
    }

    @FXML
    private void handleHistoryNav(ActionEvent event) {
        // Sudah di halaman ini, tidak perlu melakukan apapun atau tampilkan notifikasi
    }

    @FXML
    private void handleProfileNav(ActionEvent event) {
        loadPage(event, "/com/example/sapatatix/fxml/Profil.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        loadPage(event, "/com/example/sapatatix/fxml/Login.fxml");
    }

    private void loadPage(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Bisa tambahkan pop-up alert di sini kalau diperlukan
        }
    }
}
