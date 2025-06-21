package com.example.sapatatix.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private Button buatEventBtn;
    @FXML private Button riwayatBtn;
    @FXML private Button profilBtn;

    @FXML
    public void handleGoToBuatEvent() {
        loadFXML("/com/example/sapatatix/FXML/BuatEventEdit.fxml", "Buat Event");
    }

    @FXML
    public void handleGoToRiwayat() {
        loadFXML("/com/example/sapatatix/FXML/Riwayat.fxml", "Riwayat Event");
    }

    @FXML
    public void handleGoToProfil() {
        loadFXML("/com/example/sapatatix/FXML/Profile.fxml", "Profil Pengguna");
    }

    private void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) profilBtn.getScene().getWindow(); // gunakan salah satu tombol
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal membuka halaman: " + fxmlPath);
        }
    }
}