package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class BuatEventBannerController {

    @FXML private Button btnSimpanLanjutkan;
    @FXML private Button btnPilihFile;
    @FXML private TextField filePathField;
    @FXML private ImageView imagePreview;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Event handler untuk tombol
        btnPilihFile.setOnAction(e -> handlePilihFile());
        filePathField.setOnMouseClicked(e -> handlePilihFile());
        btnSimpanLanjutkan.setOnAction(e -> handleSimpanLanjutkan());
    }

    @FXML
    private void handlePilihFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Banner");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(btnPilihFile.getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());

            // Tampilkan preview gambar dengan ukuran penuh
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
            imagePreview.setPreserveRatio(false); // agar isi kotak penuh
            imagePreview.setFitWidth(950);        // sesuaikan dengan ukuran kotak di FXML
            imagePreview.setFitHeight(314);
        } else {
            filePathField.setText("Belum ada file dipilih");
        }
    }

    private void handleSimpanLanjutkan() {
        if (selectedFile == null) {
            showAlert("Peringatan", "Silakan pilih file banner terlebih dahulu.");
            return;
        }

        String eventId = SessionManager.id;
        if (eventId == null || eventId.isEmpty()) {
            showAlert("Error", "ID event tidak tersedia.");
            return;
        }

        SupabaseService.uploadBanner(eventId, selectedFile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Gagal", "Gagal mengunggah banner: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        goToHalamanTiket();
                    });
                } else {
                    Platform.runLater(() -> showAlert("Gagal", "Gagal menyimpan banner. Kode: " + response.code()));
                }
            }
        });
    }

    private void goToHalamanTiket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BuatEventTiket.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSimpanLanjutkan.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Gagal", "Tidak bisa membuka halaman tiket.");
        }
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}