package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class BuatEventTiketController {

    @FXML private TextField ticketNameField;
    @FXML private TextField ticketPriceField;
    @FXML private TextField totalTicketsField;
    @FXML private VBox paidEventVBox;
    @FXML private VBox freeEventVBox;
    @FXML private Button btnSimpan;
    @FXML private Button minusButton;
    @FXML private Button plusButton;


    private String selectedTicketType = "berbayar"; // default

    @FXML
    public void initialize() {
        paidEventVBox.setOnMouseClicked(e -> pilihEventBerbayar());
        freeEventVBox.setOnMouseClicked(e -> pilihEventGratis());
        plusButton.setOnAction(e -> handlePlus());
        minusButton.setOnAction(e -> handleMinus());
        btnSimpan.setOnAction(e -> handleSimpanLanjutkan());
    }

    private void pilihEventBerbayar() {
        selectedTicketType = "berbayar";
        paidEventVBox.setStyle("-fx-border-color: #B83D6E; -fx-border-width: 2;");
        freeEventVBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");
    }

    private void pilihEventGratis() {
        selectedTicketType = "gratis";
        paidEventVBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;");
        freeEventVBox.setStyle("-fx-border-color: #B83D6E; -fx-border-width: 2;");
    }

    private void handlePlus() {
        try {
            int current = Integer.parseInt(totalTicketsField.getText());
            totalTicketsField.setText(String.valueOf(current + 1));
        } catch (NumberFormatException e) {
            totalTicketsField.setText("1"); // fallback kalau kosong/error
        }
    }

    private void handleMinus() {
        try {
            int current = Integer.parseInt(totalTicketsField.getText());
            if (current > 1) {
                totalTicketsField.setText(String.valueOf(current - 1));
            }
        } catch (NumberFormatException e) {
            totalTicketsField.setText("1"); // fallback
        }
    }

    @FXML
    private void handleSimpanLanjutkan() {
        // Ambil data dari TextField
        String namaTiket = ticketNameField.getText();
        String harga = ticketPriceField.getText();
        String jumlah = totalTicketsField.getText();

        String jenisEvent = selectedTicketType;// ambil dari vbox yang dipilih (berbayar/gratis)
        String eventId = SessionManager.id;

        if (eventId == null) {
            showAlert("Error", "ID event tidak tersedia");
            return;
        }

        SupabaseService.uploadTiket(eventId, jenisEvent, namaTiket, harga, jumlah, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Gagal", "Gagal koneksi: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (response) {
                    System.out.println("Response code: " + response.code());
                    System.out.println("Kode: " + response.code());
                    System.out.println("Respon: " + response.body().string());

                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            showAlert("Sukses", "Tiket berhasil disimpan.");
                            goToHalamanReview(); // pastikan ini ada
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Gagal", "Kode error: " + response.code()));
                    }
                }
            }

        });
    }

    private void goToHalamanReview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BuatEventReview.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSimpan.getScene().getWindow(); // ini penting
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Tidak bisa membuka halaman review");
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