package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.model.Event;
import com.example.sapatatix.model.Ticket;
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
import org.json.JSONObject;
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
    @FXML private Label detailJenisSpesifikLabel;
    @FXML private Label detailTambahanLabel;

    private Stage dialogStage;
    private Event eventData;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEventDetails(Event event) {
        this.eventData = event;
        detailJudulHeaderLabel.setText(event.getJudul());

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
        String eventId = eventData.getId();

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
                                Ticket ticketObject = Ticket.fromJson(ticketJson);

                                TransactionData newTransaction = new TransactionData(eventData);
                                newTransaction.setTicketObject(ticketObject);

                                newTransaction.setTicketId(ticketObject.getId());
                                newTransaction.setTicketType(ticketObject.getNamaTiket());
                                newTransaction.setTicketPrice(ticketObject.calculatePrice());

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