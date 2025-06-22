package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.model.Event; // Import kelas Event model
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField; // Import TextField
import javafx.scene.control.ComboBox; // Import ComboBox (not used in actual FXML, but might be from previous draft)

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Button buatEventBtn;
    @FXML private Button riwayatBtn;
    @FXML private Button profilBtn;
    @FXML private TilePane eventTilePane;

    // FXML Elements for Search and Filter
    @FXML private TextField searchInputField; // fx:id="searchInputField" di FXML
    @FXML private Label allCategoryLabel;    // fx:id="allCategoryLabel" di FXML
    @FXML private Label budayaCategoryLabel; // fx:id="budayaCategoryLabel" di FXML
    @FXML private Label amalCategoryLabel;   // fx:id="amalCategoryLabel" di FXML
    @FXML private Label pariwisataCategoryLabel; // fx:id="pariwisataCategoryLabel" di FXML

    private String currentSelectedCategory = "All"; // Default kategori yang terpilih
    private Label lastSelectedCategoryLabel; // Untuk menyimpan referensi label kategori yang terakhir dipilih

    @FXML
    public void initialize() {
        // Inisialisasi style untuk kategori "All" sebagai yang terpilih saat awal
        allCategoryLabel.setStyle("-fx-underline: true; -fx-font-weight: bold; -fx-text-fill: #b83d6e;");
        lastSelectedCategoryLabel = allCategoryLabel;

        // Atur event handler untuk memicu pencarian/filter
        if (searchInputField != null) {
            searchInputField.setOnAction(event -> handleSearch()); // Trigger search on Enter in text field
        }

        // Atur event handler untuk klik label kategori
        if (allCategoryLabel != null) allCategoryLabel.setOnMouseClicked(this::handleCategoryClick);
        if (budayaCategoryLabel != null) budayaCategoryLabel.setOnMouseClicked(this::handleCategoryClick);
        if (amalCategoryLabel != null) amalCategoryLabel.setOnMouseClicked(this::handleCategoryClick);
        if (pariwisataCategoryLabel != null) pariwisataCategoryLabel.setOnMouseClicked(this::handleCategoryClick);


        loadEvents(null, null); // Muat event awal tanpa filter
    }

    // Metode untuk memuat event dengan parameter pencarian dan kategori
    private void loadEvents(String searchTerm, String category) {
        SupabaseService.getEvents(searchTerm, category, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> System.out.println("Gagal memuat event: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    Platform.runLater(() -> {
                        eventTilePane.getChildren().clear();
                        if (jsonArray.length() == 0) {
                            Label noResultsLabel = new Label("Tidak ada event yang ditemukan untuk kriteria ini.");
                            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                            eventTilePane.getChildren().add(noResultsLabel);
                            TilePane.setMargin(noResultsLabel, new Insets(50));
                            return;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eventJson = jsonArray.getJSONObject(i);
                            try {
                                Event eventObject = Event.fromJson(eventJson);
                                VBox eventCard = createEventCard(eventObject);
                                eventCard.setOnMouseClicked(mouseEvent -> showEventDetail(eventObject));
                                eventTilePane.getChildren().add(eventCard);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Gagal membuat kartu event untuk: " + eventJson.optString("judul"));
                            }
                        }
                    });
                } else {
                    Platform.runLater(() -> System.out.println("Gagal memuat event. Kode: " + response.code() + ". Response: " + responseBody));
                }
            }
        });
    }

    // Metode untuk menangani aksi pencarian/filter dari UI
    @FXML
    private void handleSearch() {
        String searchTerm = searchInputField.getText();
        loadEvents(searchTerm, currentSelectedCategory);
    }

    // Metode untuk menangani klik pada label kategori
    @FXML
    private void handleCategoryClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();

        // Reset style label yang sebelumnya terpilih
        if (lastSelectedCategoryLabel != null) {
            lastSelectedCategoryLabel.setStyle("-fx-underline: false; -fx-font-weight: normal; -fx-text-fill: white;");
        }

        // Set style untuk label yang baru diklik
        clickedLabel.setStyle("-fx-underline: true; -fx-font-weight: bold; -fx-text-fill: #b83d6e;");
        lastSelectedCategoryLabel = clickedLabel; // Perbarui label yang terakhir terpilih

        // Ambil teks kategori dari label yang diklik
        currentSelectedCategory = clickedLabel.getText();
        if (currentSelectedCategory.equalsIgnoreCase("All")) {
            currentSelectedCategory = "Semua Kategori"; // Sesuaikan dengan nilai yang dikirim ke SupabaseService
        }

        handleSearch(); // Lakukan pencarian ulang dengan kategori baru
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setPrefHeight(280.0);
        card.setPrefWidth(220.0);
        card.setStyle("-fx-background-color: #1A1C28; -fx-background-radius: 10;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(120.0);
        imageView.setFitWidth(220.0);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-background-radius: 10 10 0 0;");

        String bannerUrl = event.getBannerUrl();
        if (bannerUrl != null && !bannerUrl.isEmpty()) {
            try {
                imageView.setImage(new Image(bannerUrl));
            } catch (Exception e) {
                System.err.println("Gagal memuat gambar banner untuk event " + event.getJudul() + ": " + e.getMessage());
                imageView.setImage(new Image("https://placehold.co/220x120/B83D6E/ffffff?text=Event+Image"));
            }
        } else {
            imageView.setImage(new Image("https://placehold.co/220x120/B83D6E/ffffff?text=Event+Image"));
        }

        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5.0);
        detailsBox.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        detailsBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        Label dateLabel = new Label(event.getFormattedTanggal());
        dateLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI Bold", 14.0));

        Label titleLabel = new Label(event.getJudul());
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setWrapText(true);
        titleLabel.setFont(new Font("Segoe UI Bold", 14.0));

        Label locationLabel = new Label(event.getTempat());
        locationLabel.setTextFill(javafx.scene.paint.Color.web("#9e9e9e"));
        locationLabel.setWrapText(true);
        locationLabel.setFont(new Font("Segoe UI", 10.0));

        Label timeLabel = new Label(event.getFormattedWaktu());
        timeLabel.setTextFill(javafx.scene.paint.Color.web("#9e9e9e"));
        timeLabel.setFont(new Font("Segoe UI", 10.0));

        Label ticketsLabel = new Label("IDR XX Tickets Available");
        ticketsLabel.setTextFill(javafx.scene.paint.Color.web("#e91e63"));
        ticketsLabel.setFont(new Font("Segoe UI Bold", 10.0));

        detailsBox.getChildren().addAll(dateLabel, titleLabel, locationLabel, timeLabel, ticketsLabel);
        card.getChildren().addAll(imageView, detailsBox);

        return card;
    }

    private void showEventDetail(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/EventDetailPopup.fxml"));
            Parent root = loader.load();

            EventDetailController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detail Event");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(eventTilePane.getScene().getWindow());

            dialogStage.setScene(new Scene(root));

            controller.setDialogStage(dialogStage);
            controller.setEventDetails(event);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up detail event: " + e.getMessage());
        }
    }

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