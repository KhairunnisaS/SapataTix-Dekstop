package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class BuatEventEditController {

    @FXML private TextField judulField;
    @FXML private ComboBox<String> kategoriCombo;
    @FXML private TextField tempatField;
    @FXML private TextArea deskripsiArea;
    @FXML private TextField hostField;
    @FXML private TextField noHpField;
    @FXML private ComboBox<String> sesiCombo;
    @FXML private DatePicker tanggalPicker;
    @FXML private TextField waktuMulaiField;
    @FXML private TextField waktuBerakhirField;
    @FXML private RadioButton satuHariRadio;
    @FXML private RadioButton berjalanRadio;
    @FXML private ToggleGroup eventTypeGroup;

    @FXML
    public void initialize() {
        kategoriCombo.getItems().addAll("Budaya", "Amal", "Pariwisata", "Lainnya");
        sesiCombo.getItems().addAll("Pagi", "Siang", "Sore", "Malam");
    }

    @FXML
    private void handleSimpanEvent() {
        String judul = judulField.getText();
        String kategori = kategoriCombo.getValue();
        String tempat = tempatField.getText();
        String deskripsi = deskripsiArea.getText();
        String host = hostField.getText();
        String noHp = noHpField.getText();
        String sesi = sesiCombo.getValue();
        String tanggal = (tanggalPicker.getValue() != null) ? tanggalPicker.getValue().toString() : "";
        String waktuMulai = waktuMulaiField.getText();
        String waktuBerakhir = waktuBerakhirField.getText();
        String jenisEvent = satuHariRadio.isSelected() ? "Event Satu Hari" :
                berjalanRadio.isSelected() ? "Event Berjalan" : "";

        if (judul.isEmpty() || kategori == null || tempat.isEmpty() || deskripsi.isEmpty() ||
                host.isEmpty() || noHp.isEmpty() || sesi == null || tanggal.isEmpty() ||
                waktuMulai.isEmpty() || waktuBerakhir.isEmpty() || jenisEvent.isEmpty()) {
            showAlert("Peringatan", "Semua field wajib diisi!");
            return;
        }

        SupabaseService.buatEvent(
                judul, kategori, tempat, deskripsi,
                host, noHp, sesi, tanggal,
                waktuMulai, waktuBerakhir, jenisEvent,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        showAlert("Gagal", "Gagal mengirim data ke Supabase.");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            javafx.application.Platform.runLater(() -> {
                                showAlert("Sukses", "Event berhasil disimpan!");
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BuatEventBanner.fxml"));
                                    Parent root = loader.load();
                                    Stage stage = (Stage) judulField.getScene().getWindow();
                                    stage.setScene(new Scene(root));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showAlert("Error", "Gagal membuka halaman berikutnya.");
                                }
                            });
                        } else {
                            showAlert("Gagal", "Gagal menyimpan event. Kode: " + response.code());
                        }
                    }
                });
    }

    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}