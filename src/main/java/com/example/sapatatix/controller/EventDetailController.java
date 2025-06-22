package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.service.SupabaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class EventDetailController {

    @FXML private ImageView detailImageView;
    @FXML private Label detailJudulHeaderLabel;
    @FXML private Label detailTanggalLabel;
    @FXML private Label detailWaktuLabel;
    @FXML private Label detailTempatLabel;
    @FXML private Label detailDeskripsiLabel;
    @FXML private Label detailHostLabel;
    @FXML private Label detailTicketInfoLabel;

    private Stage dialogStage;
    private JSONObject eventData;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEventDetails(JSONObject event) {
        this.eventData = event;
        detailJudulHeaderLabel.setText(event.optString("judul", "Detail Event"));

        // Set Date
        String tanggalMulai = event.optString("tanggal_mulai", "");
        if (!tanggalMulai.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(tanggalMulai);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM");
                detailTanggalLabel.setText(date.format(dateFormatter));
            } catch (Exception e) {
                detailTanggalLabel.setText("Tanggal Tidak Valid");
            }
        } else {
            detailTanggalLabel.setText("Tanggal Tidak Tersedia");
        }

        // Set Time
        String waktuMulai = event.optString("waktu_mulai", "");
        String waktuBerakhir = event.optString("waktu_berakhir", "");
        if (!waktuMulai.isEmpty() && !waktuBerakhir.isEmpty()) {
            try {
                LocalTime startTime = LocalTime.parse(waktuMulai);
                LocalTime endTime = LocalTime.parse(waktuBerakhir);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                detailWaktuLabel.setText(startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter));
            } catch (Exception e) {
                detailWaktuLabel.setText(waktuMulai + " - " + waktuBerakhir);
            }
        } else if (!waktuMulai.isEmpty()) {
            detailWaktuLabel.setText(waktuMulai);
        } else {
            detailWaktuLabel.setText("Waktu Tidak Tersedia");
        }

        detailTempatLabel.setText(event.optString("tempat", ""));
        detailDeskripsiLabel.setText(event.optString("deskripsi", ""));
        detailHostLabel.setText(event.optString("nama_host", ""));

        detailTicketInfoLabel.setText("IDR XX Tickets Available");

        // Load banner image
        String bannerUrl = event.optString("banner_url", "");
        if (!bannerUrl.isEmpty()) {
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

    // Metode handleBuyTicket() untuk memulai alur transaksi
    @FXML
    private void handleBuyTicket() {
        String eventId = eventData.optString("id"); // Get the event ID

        if (eventId == null || eventId.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Event ID is missing. Cannot proceed with ticket purchase.");
                alert.showAndWait();
            });
            return;
        }

        SupabaseService.getTicketByEventId(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to retrieve ticket information: " + e.getMessage());
                    alert.showAndWait();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (jsonArray.length() > 0) {
                        JSONObject ticketJson = jsonArray.getJSONObject(0); // Assuming one ticket type per event for simplicity

                        Platform.runLater(() -> {
                            try {
                                // Create new TransactionData and pass both event and ticket details
                                TransactionData newTransaction = new TransactionData(eventData);
                                newTransaction.setTicketId(ticketJson.optString("id")); // Set the ticket ID from the 'tiket' table
                                newTransaction.setTicketType(ticketJson.optString("nama_tiket", "Tiket Reguler")); // Use nama_tiket from DB
                                newTransaction.setTicketPrice(ticketJson.optDouble("harga", 0.0)); // Use harga from DB

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
                                System.err.println("Failed to load Ticket Selection pop-up: " + e.getMessage());
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setHeaderText(null);
                            alert.setContentText("No ticket information found for this event.");
                            alert.showAndWait();
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to retrieve ticket information. Code: " + response.code() + ", Response: " + responseBody);
                        alert.showAndWait();
                    });
                }
            }
        });
    }
}