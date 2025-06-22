package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.model.Event; // Import kelas Event model
import com.example.sapatatix.model.Ticket; // Import kelas Ticket model
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject; // Tetap diperlukan untuk parsing awal dari Supabase
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class EventDetailController {

    @FXML private ImageView detailImageView;
    @FXML private Label detailJudulHeaderLabel;
    @FXML private Label detailTanggalLabel;
    @FXML private Label detailWaktuLabel;
    @FXML private Label detailTempatLabel;
    @FXML private Label detailDeskripsiLabel;
    @FXML private Label detailHostLabel;
    @FXML private Label detailTicketInfoLabel;
    @FXML private Label detailJenisSpesifikLabel; // Tambahkan ini jika Anda ingin menampilkan jenis spesifik event
    @FXML private Label detailTambahanLabel;     // Tambahkan ini jika Anda ingin menampilkan detail tambahan event

    private Stage dialogStage;
    private Event eventData; // Ganti JSONObject menjadi objek Event dari model

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Metode ini sekarang menerima objek Event dari model
    public void setEventDetails(Event event) {
        this.eventData = event;
        detailJudulHeaderLabel.setText(event.getJudul());

        // Menggunakan getter dari objek Event untuk tanggal dan waktu
        detailTanggalLabel.setText(event.getFormattedTanggal());
        detailWaktuLabel.setText(event.getFormattedWaktu());

        detailTempatLabel.setText(event.getTempat());
        detailDeskripsiLabel.setText(event.getDeskripsi());
        detailHostLabel.setText(event.getNamaHost());

        // Menampilkan detail polimorfik (dari subclass Event)
        if (detailJenisSpesifikLabel != null) {
            detailJenisSpesifikLabel.setText(event.getJenisSpesifik());
        }
        if (detailTambahanLabel != null) {
            detailTambahanLabel.setText(event.getDetailTambahan());
        }


        detailTicketInfoLabel.setText("IDR XX Tickets Available"); // Placeholder, akan diupdate dari objek Ticket

        // Load banner image
        String bannerUrl = event.getBannerUrl();
        if (bannerUrl != null && !bannerUrl.isEmpty()) {
            try {
                detailImageView.setImage(new Image(bannerUrl));
            } catch (Exception e) {
                System.err.println("Failed to load banner image: " + e.getMessage());
                detailImageView.setImage(new Image("https://placehold.co/760x200/B83D6E/ffffff?text=Event+Banner"));
            }
        } else {
            detailImageView.setImage(new Image("https://placehold.co/760x200/B83D6E/ffffff?text=Event+Banner"));
        }
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleBuyTicket() {
        String eventId = eventData.getId(); // Menggunakan ID dari objek Event

        if (eventId == null || eventId.isEmpty()) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Error", "ID event tidak tersedia. Tidak dapat melanjutkan pembelian tiket.");
            });
            return;
        }

        SupabaseService.getTicketByEventId(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengambil informasi tiket: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (jsonArray.length() > 0) {
                        JSONObject ticketJson = jsonArray.getJSONObject(0); // Asumsi satu jenis tiket per event untuk kesederhanaan

                        Platform.runLater(() -> {
                            try {
                                // Konversi JSONObject menjadi objek Ticket dari model
                                Ticket ticketObject = Ticket.fromJson(ticketJson);

                                // Buat objek TransactionData baru dan teruskan objek Event dan Ticket
                                TransactionData newTransaction = new TransactionData(eventData); // Menggunakan eventData (objek Event)
                                newTransaction.setTicketObject(ticketObject); // Set objek Ticket

                                // Data tambahan yang sebelumnya disimpan di TransactionData bisa diambil dari ticketObject
                                newTransaction.setTicketId(ticketObject.getId()); // ID dari objek Ticket
                                newTransaction.setTicketType(ticketObject.getNamaTiket()); // Nama tiket dari objek Ticket
                                newTransaction.setTicketPrice(ticketObject.calculatePrice()); // Harga tiket dari metode polimorfik

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/TicketSelectionPopup.fxml"));
                                Parent root = loader.load();

                                TicketSelectionController controller = loader.getController();
                                Stage ticketSelectionStage = new Stage();
                                ticketSelectionStage.setTitle("Pilih Tiket");
                                ticketSelectionStage.initModality(Modality.APPLICATION_MODAL);
                                ticketSelectionStage.initOwner(dialogStage);

                                ticketSelectionStage.setScene(new Scene(root));

                                controller.setDialogStage(ticketSelectionStage);
                                controller.setTransactionData(newTransaction);

                                dialogStage.close();
                                ticketSelectionStage.showAndWait();
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.err.println("Gagal memuat pop-up Pemilihan Tiket: " + e.getMessage());
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Informasi", "Tidak ada informasi tiket yang ditemukan untuk event ini.");
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengambil informasi tiket. Kode: " + response.code() + ", Respons: " + responseBody);
                    });
                }
            }
        });
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