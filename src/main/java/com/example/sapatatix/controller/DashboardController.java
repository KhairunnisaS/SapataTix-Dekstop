package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality; // Import Modality
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent; // Import MouseEvent for setOnMouseClicked

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Button buatEventBtn;
    @FXML private Button riwayatBtn;
    @FXML private Button profilBtn;
    @FXML private TilePane eventTilePane; // fx:id untuk TilePane di FXML

    @FXML
    public void initialize() {
        loadEvents(); // Panggil metode untuk memuat event saat controller diinisialisasi
    }

    private void loadEvents() {
        SupabaseService.getEvents(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> System.out.println("Gagal memuat event: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string(); // Ambil body response hanya sekali

                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    Platform.runLater(() -> {
                        eventTilePane.getChildren().clear(); // Hapus semua kartu event yang ada (jika ada)
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject event = jsonArray.getJSONObject(i);
                            try {
                                VBox eventCard = createEventCard(event);
                                // PENTING: Tambahkan event handler untuk klik kartu
                                eventCard.setOnMouseClicked(mouseEvent -> showEventDetail(event));
                                eventTilePane.getChildren().add(eventCard);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Gagal membuat kartu event untuk: " + event.optString("judul"));
                            }
                        }
                    });
                } else {
                    Platform.runLater(() -> System.out.println("Gagal memuat event. Kode: " + response.code() + ". Response: " + responseBody));
                }
            }
        });
    }

    // Metode ini membuat VBox (kartu event) dari data JSONObject
    private VBox createEventCard(JSONObject event) {
        VBox card = new VBox();
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        // Sesuaikan ukuran kartu agar proporsional untuk TilePane
        card.setPrefHeight(280.0); // Tinggi kartu yang disarankan
        card.setPrefWidth(220.0); // Lebar kartu yang disarankan
        card.setStyle("-fx-background-color: #1A1C28; -fx-background-radius: 10;");

        // ImageView untuk banner event
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120.0); // Tinggi gambar
        imageView.setFitWidth(220.0); // Lebar gambar sesuai kartu
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-background-radius: 10 10 0 0;");

        String bannerUrl = event.optString("banner_url", ""); // Asumsi ada kolom 'banner_url' di database
        if (!bannerUrl.isEmpty()) {
            try {
                // Catatan: Jika banner_url hanya nama file, Anda perlu membentuk URL lengkap ke Supabase Storage Bucket Anda.
                // Contoh: String fullBannerUrl = "https://<your-project-id>.supabase.co/storage/v1/object/public/<nama_bucket_anda>/" + bannerUrl;
                // imageView.setImage(new Image(fullBannerUrl));
                imageView.setImage(new Image(bannerUrl)); // Asumsi bannerUrl sudah berupa URL lengkap
            } catch (Exception e) {
                System.err.println("Gagal memuat gambar banner untuk event " + event.optString("judul") + ": " + e.getMessage());
                imageView.setImage(new Image("https://placehold.co/220x120/B83D6E/ffffff?text=Event+Image"));
            }
        } else {
            imageView.setImage(new Image("https://placehold.co/220x120/B83D6E/ffffff?text=Event+Image"));
        }

        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5.0);
        detailsBox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        detailsBox.setAlignment(javafx.geometry.Pos.TOP_LEFT); // Menyelaraskan konten label ke kiri atas

        // Date Label
        Label dateLabel = new Label();
        String tanggalMulai = event.optString("tanggal_mulai", "");
        if (!tanggalMulai.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(tanggalMulai);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
                dateLabel.setText(date.format(formatter).toUpperCase());
            } catch (Exception e) {
                dateLabel.setText("Tanggal Tidak Valid");
            }
        } else {
            dateLabel.setText("Tanggal Tidak Tersedia");
        }
        dateLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI Bold", 14.0));

        // Title Label
        Label titleLabel = new Label(event.optString("judul", "Judul Event"));
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setWrapText(true);
        titleLabel.setFont(new Font("Segoe UI Bold", 14.0));

        // Location Label
        Label locationLabel = new Label(event.optString("tempat", "Tempat Event"));
        locationLabel.setTextFill(javafx.scene.paint.Color.web("#9e9e9e"));
        locationLabel.setWrapText(true);
        locationLabel.setFont(new Font("Segoe UI", 10.0));

        // Time Label
        Label timeLabel = new Label(event.optString("waktu_mulai", "") + " - " + event.optString("waktu_berakhir", ""));
        timeLabel.setTextFill(javafx.scene.paint.Color.web("#9e9e9e"));
        timeLabel.setFont(new Font("Segoe UI", 10.0));

        // Menggunakan data harga_tiket dari Supabase untuk label tiket
        String ticketsInfo = "IDR XX Tickets Available";
        if (event.has("harga_tiket")) {
            ticketsInfo = "IDR " + String.format("%,.0f", event.optDouble("harga_tiket", 0.0)) + " Available";
        } else if (event.has("stok_tiket")) { // Jika ada kolom stok tiket
            ticketsInfo = event.optInt("stok_tiket", 0) + " Tickets Available";
        }
        Label ticketsLabel = new Label(ticketsInfo);
        ticketsLabel.setTextFill(javafx.scene.paint.Color.web("#e91e63"));
        ticketsLabel.setFont(new Font("Segoe UI Bold", 10.0));

        detailsBox.getChildren().addAll(dateLabel, titleLabel, locationLabel, timeLabel, ticketsLabel);
        card.getChildren().addAll(imageView, detailsBox);

        return card;
    }

    // Metode ini membuka pop-up detail event ketika kartu diklik
    private void showEventDetail(JSONObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/EventDetailPopup.fxml"));
            Parent root = loader.load();

            EventDetailController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detail Event");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Memblokir jendela lain
            dialogStage.initOwner(eventTilePane.getScene().getWindow()); // Mengatur pemilik ke jendela dashboard

            dialogStage.setScene(new Scene(root));

            controller.setDialogStage(dialogStage);
            controller.setEventDetails(event); // Meneruskan data event ke controller pop-up

            dialogStage.showAndWait(); // Tampilkan pop-up dan tunggu hingga ditutup
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up detail event: " + e.getMessage());
        }
    }

    // Metode navigasi lain
    @FXML
    public void handleGoToBuatEvent() {
        loadFXML("/com/example/sapatatix/FXML/BuatEventDeskripsi.fxml", "Buat Event");
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
            Stage stage = (Stage) profilBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal membuka halaman: " + fxmlPath);
        }
    }
}