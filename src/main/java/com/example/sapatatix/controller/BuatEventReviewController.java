package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import com.example.sapatatix.model.Event;
import com.example.sapatatix.model.Ticket;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class BuatEventReviewController {

    // FXML Elements for Event Details - Disesuaikan dengan fx:id di FXML
    @FXML private ImageView eventImageView;
    @FXML private Label eventNameLabel;
    @FXML private Label eventCategoryLabel; // Baru: Untuk kategori event
    @FXML private Label eventLocationLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label eventHostLabel;
    @FXML private Label eventHostPhoneLabel; // Baru: Untuk nomor HP host
    @FXML private Label eventSesiLabel;      // Baru: Untuk sesi event
    @FXML private Label eventTypeLabel;      // Baru: Untuk jenis event (satu hari/berjalan)
    @FXML private Label eventDescriptionLabel;


    // FXML Elements for Ticket Details - Disesuaikan dengan fx:id di FXML
    @FXML private Label labelJenisHargaTiket;

    // FXML Elements for Buttons
    @FXML private Button backButton;
    @FXML private Button uploadEventButton; // Nama lama 'unggahBtn' di FXML, ganti jadi 'uploadEventButton' di sini dan di FXML

    private Event currentEvent;
    private Ticket currentTicket;

    @FXML
    public void initialize() {
        String eventId = SessionManager.id;
        if (eventId == null || eventId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID Event tidak ditemukan. Tidak dapat menampilkan review.");
            return;
        }

        // Atur aksi untuk tombol
        if (backButton != null) backButton.setOnAction(e -> handleBack());
        if (uploadEventButton != null) uploadEventButton.setOnAction(e -> handleUnggahEvent());

        loadEventDetails(eventId);
        loadTicketDetails(eventId);
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

                    currentEvent = Event.fromJson(eventJson);

                    Platform.runLater(() -> {
                        eventNameLabel.setText(currentEvent.getJudul());
                        eventCategoryLabel.setText("Kategori: " + currentEvent.getKategori()); // Isi label kategori
                        eventLocationLabel.setText(currentEvent.getTempat());
                        eventDateLabel.setText(currentEvent.getFormattedTanggal());
                        eventTimeLabel.setText(currentEvent.getFormattedWaktu());
                        eventHostLabel.setText(currentEvent.getNamaHost());
                        eventHostPhoneLabel.setText("No. HP Host: " + currentEvent.getNoHpHost()); // Isi label nomor HP host
                        eventSesiLabel.setText("Sesi: " + currentEvent.getSesi());           // Isi label sesi
                        eventTypeLabel.setText("Tipe: " + currentEvent.getJenisEvent());     // Isi label jenis event
                        eventDescriptionLabel.setText(currentEvent.getDeskripsi());

                        // Load banner image
                        String bannerUrl = currentEvent.getBannerUrl();
                        if (bannerUrl != null && !bannerUrl.isEmpty()) {
                            try {
                                String fullBannerUrl = "https://mcqhhdeqkuklvxglpycb.supabase.co/storage/v1/object/public/event-banner/" + bannerUrl;
                                eventImageView.setImage(new Image(fullBannerUrl));
                            } catch (Exception e) {
                                System.err.println("Gagal memuat banner event: " + e.getMessage());
                                eventImageView.setImage(new Image("https://placehold.co/250x150/B83D6E/ffffff?text=Image+Event"));
                            }
                        } else {
                            eventImageView.setImage(new Image("https://placehold.co/250x150/B83D6E/ffffff?text=Image+Event"));
                        }
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Informasi", "Event tidak ditemukan. Kode: " + response.code() + ", Pesan: " + responseBody));
                }
            }
        });
    }

    private void loadTicketDetails(String eventId) {
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

                    currentTicket = Ticket.fromJson(ticketJson);

                    Platform.runLater(() -> {
                        if (labelJenisHargaTiket != null) {
                            String hargaFormatted = "";
                            if (currentTicket.getNamaTiket() != null && currentTicket.getNamaTiket().equalsIgnoreCase("gratis")) {
                                hargaFormatted = "Gratis";
                            } else {
                                hargaFormatted = String.format("Rp %,.0f", currentTicket.calculatePrice());
                            }

                            labelJenisHargaTiket.setText(
                                    currentTicket.getNamaTiket() +
                                            " : " + hargaFormatted +
                                            " (Jumlah: " + currentTicket.getJumlahTersedia() + ")"
                            );
                        }
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Informasi", "Tiket untuk event ini tidak ditemukan. Kode: " + response.code() + ", Pesan: " + responseBody));
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BuatEventTiket.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Buat Event Tiket");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat halaman sebelumnya.");
        }
    }

    @FXML
    private void handleUnggahEvent() {
        // Logika untuk menyelesaikan/mempublikasikan event dan kembali ke Dashboard
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) uploadEventButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Event berhasil dipublikasikan!");
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