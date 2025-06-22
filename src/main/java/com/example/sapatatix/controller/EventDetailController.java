package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.controller.TicketSelectionController; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;
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
    private JSONObject eventData; // Menyimpan data event yang diteruskan

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEventDetails(JSONObject event) {
        this.eventData = event; // Simpan data event
        detailJudulHeaderLabel.setText(event.optString("judul", "Detail Event"));

        // Set Date
        String tanggalMulai = event.optString("tanggal_mulai", "");
        if (!tanggalMulai.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(tanggalMulai);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM"); // e.g., Minggu, 23 Juni 2024
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
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // e.g., 09:00 AM
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

        // Asumsi informasi tiket belum ada di Supabase, tetap sebagai placeholder atau ambil jika tersedia
        detailTicketInfoLabel.setText("IDR XX Tickets Available");

        // Load banner image
        String bannerUrl = event.optString("banner_url", "");
        if (!bannerUrl.isEmpty()) {
            try {
                // PENTING: Pilih salah satu dari dua opsi di bawah, dan hapus yang lain!
                // Opsi 1: Jika 'bannerUrl' dari database sudah berupa URL lengkap (misal: "https://mysupabase.co/storage/v1/object/public/banners/image.jpg")
                detailImageView.setImage(new Image(bannerUrl));

                // Opsi 2: Jika 'bannerUrl' dari database hanya nama file, BUKA KOMENTAR baris di bawah dan GANTI placeholder:
                // String fullBannerUrl = "https://<your-project-id>.supabase.co/storage/v1/object/public/event_banners/" + bannerUrl; // GANTI: <your-project-id> dan 'event_banners' dengan nilai aktual Anda
                // detailImageView.setImage(new Image(fullBannerUrl));

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
        try {
            // Buat objek TransactionData baru dan teruskan data event awal
            TransactionData newTransaction = new TransactionData(eventData); // Gunakan eventData yang sudah disimpan

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/TicketSelectionPopup.fxml"));
            Parent root = loader.load();

            TicketSelectionController controller = loader.getController();
            Stage ticketSelectionStage = new Stage();
            ticketSelectionStage.setTitle("Pilih Tiket");
            ticketSelectionStage.initModality(Modality.APPLICATION_MODAL);
            ticketSelectionStage.initOwner(dialogStage); // Owner adalah pop-up detail event

            ticketSelectionStage.setScene(new Scene(root));

            controller.setDialogStage(ticketSelectionStage);
            controller.setTransactionData(newTransaction); // Teruskan objek TransactionData

            dialogStage.close(); // Tutup pop-up detail event
            ticketSelectionStage.showAndWait(); // Tampilkan pop-up pemilihan tiket
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Pilih Tiket: " + e.getMessage());
        }
    }
}