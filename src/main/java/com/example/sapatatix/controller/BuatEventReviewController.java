package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import com.example.sapatatix.model.Event; // Import Event model
import com.example.sapatatix.model.Ticket; // Import Ticket model

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label; // Import Label
import javafx.scene.control.Button; // Import Button
import javafx.stage.Stage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class BuatEventReviewController {

    // FXML Elements for Event Details
    @FXML private Label judulEventLabel;
    @FXML private Label kategoriEventLabel;
    @FXML private Label tempatEventLabel;
    @FXML private Label tanggalEventLabel;
    @FXML private Label waktuEventLabel;
    @FXML private Label hostEventLabel;
    @FXML private Label deskripsiEventLabel;

    // FXML Elements for Ticket Details
    @FXML private Label namaTiketLabel;
    @FXML private Label jenisTiketLabel;
    @FXML private Label hargaTiketLabel;
    @FXML private Label jumlahTiketLabel;

    @FXML private Button finishButton; // Tombol selesai

    private Event currentEvent; // Untuk menyimpan objek event yang sedang di-review
    private Ticket currentTicket; // Untuk menyimpan objek tiket yang sedang di-review

    @FXML
    public void initialize() {
        String eventId = SessionManager.id; // Ambil eventId dari SessionManager
        if (eventId == null || eventId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID Event tidak ditemukan. Tidak dapat menampilkan review.");
            return;
        }

        loadEventDetails(eventId);
        loadTicketDetails(eventId); // Panggil juga untuk memuat detail tiket
    }

    private void loadEventDetails(String eventId) {
        SupabaseService.getEventById(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat detail event: " + e.getMessage()));
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful() && !responseBody.trim().equals("[]")) {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    JSONObject eventJson = jsonArray.getJSONObject(0);

                    // Konversi JSONObject ke objek Event model
                    currentEvent = Event.fromJson(eventJson);

                    Platform.runLater(() -> {
                        judulEventLabel.setText(currentEvent.getJudul());
                        kategoriEventLabel.setText(currentEvent.getKategori());
                        tempatEventLabel.setText(currentEvent.getTempat());
                        tanggalEventLabel.setText(currentEvent.getFormattedTanggal());
                        waktuEventLabel.setText(currentEvent.getFormattedWaktu());
                        hostEventLabel.setText(currentEvent.getNamaHost());
                        deskripsiEventLabel.setText(currentEvent.getDeskripsi());
                        // Anda bisa menampilkan detail spesifik dari subclass event di sini
                        // Misalnya: detailJenisSpesifikLabel.setText(currentEvent.getJenisSpesifik());
                        // Atau: detailTambahanLabel.setText(currentEvent.getDetailTambahan());
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Informasi", "Event tidak ditemukan. Kode: " + response.code() + ", Pesan: " + responseBody));
                }
            }
        });
    }

    private void loadTicketDetails(String eventId) {
        // PERBAIKAN: Mengoreksi nama metode yang salah dari getTiketByEventId menjadi getTicketByEventId
        SupabaseService.getTicketByEventId(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat detail tiket: " + e.getMessage()));
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful() && !responseBody.trim().equals("[]")) {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    JSONObject ticketJson = jsonArray.getJSONObject(0);

                    // Konversi JSONObject ke objek Ticket model
                    currentTicket = Ticket.fromJson(ticketJson);

                    Platform.runLater(() -> {
                        namaTiketLabel.setText(currentTicket.getNamaTiket());
                        jenisTiketLabel.setText(currentTicket.getNamaTiket()); // Asumsi nama tiket juga bisa jadi jenis
                        hargaTiketLabel.setText(String.format("Rp %,.0f", currentTicket.calculatePrice())); // Harga polimorfik
                        jumlahTiketLabel.setText(String.valueOf(currentTicket.getJumlahTersedia()));
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Informasi", "Tiket untuk event ini tidak ditemukan. Kode: " + response.code() + ", Pesan: " + responseBody));
                }
            }
        });
    }

    @FXML
    private void handleFinish() {
        // Logika untuk kembali ke Dashboard atau halaman lain setelah review selesai
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) finishButton.getScene().getWindow(); // Mengambil stage dari tombol
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat halaman Dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}