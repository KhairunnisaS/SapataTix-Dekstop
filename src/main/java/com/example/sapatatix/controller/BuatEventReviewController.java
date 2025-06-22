package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class BuatEventReviewController {

    @FXML private Label eventNameLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label eventLocationLabel;
    @FXML private Label eventDescriptionLabel;
    @FXML private Label labelJenisHargaTiket;
    @FXML private Label eventHostLabel;
    @FXML private ImageView eventImageView;

    private String eventId;

    @FXML
    public void initialize() {
        eventId = SessionManager.id;
        if (eventId != null) {
            loadEventData(eventId);
            loadTiketData(eventId);
        } else {
            showError("ID event tidak ditemukan.");
        }
    }

    private void loadEventData(String eventId) {
        SupabaseService.getEventById(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showError("Gagal koneksi (event): " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        try {
                            JSONArray array = new JSONArray(jsonData);
                            if (array.length() > 0) {
                                JSONObject eventObj = array.getJSONObject(0);
                                eventNameLabel.setText(eventObj.optString("judul", "-"));
                                eventDateLabel.setText(eventObj.optString("tanggal_mulai", "-"));
                                eventTimeLabel.setText(eventObj.optString("waktu_mulai", "-"));
                                eventLocationLabel.setText(eventObj.optString("tempat", "-"));
                                eventDescriptionLabel.setText(eventObj.optString("deskripsi", "-"));
                                eventHostLabel.setText(eventObj.optString("nama_host", "-"));


                                String bannerPath = eventObj.optString("banner_url");
                                if (bannerPath != null && !bannerPath.isEmpty()) {
                                    String imageUrl = "https://mcqhhdeqkuklvxglpycb.supabase.co/storage/v1/object/public/event-banner/" + bannerPath;
                                    Image image = new Image(imageUrl, true);
                                    eventImageView.setImage(image);
                                }

                            } else {
                                showError("Data event tidak ditemukan.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showError("Gagal memproses data event.");
                        }
                    });
                } else {
                    showError("Kode error event: " + response.code());
                }
            }
        });
    }

    private void loadTiketData(String eventId) {
        SupabaseService.getTiketByEventId(eventId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showError("Gagal mengambil data tiket: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        try {
                            JSONArray tiketArray = new JSONArray(jsonData);
                            if (tiketArray.length() > 0) {
                                JSONObject tiketObject = tiketArray.getJSONObject(0);
                                String jenisTiket = tiketObject.optString("jenis_event", "-");
                                int harga = tiketObject.optInt("harga", 0);

                                String hargaFormatted = jenisTiket.equalsIgnoreCase("gratis") ? "Gratis" : "Rp " + harga;
                                labelJenisHargaTiket.setText(jenisTiket + " : " + hargaFormatted);
                            } else {
                                labelJenisHargaTiket.setText("Belum ada tiket");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Gagal memproses data tiket.");
                        }
                    });
                } else {
                    showError("Kode error tiket: " + response.code());
                }
            }
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> System.err.println("[ERROR] " + message));
    }
}