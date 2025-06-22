package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import javafx.fxml.FXML;
import org.json.JSONObject;
import org.json.JSONArray;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class BuatEventDeskripsiController {

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
        assert judulField != null : "judulField tidak dihubungkan dari FXML";
        assert kategoriCombo != null : "kategoriCombo tidak dihubungkan dari FXML";
        assert tempatField != null : "tempatField tidak dihubungkan dari FXML";
        assert deskripsiArea != null : "deskripsiArea tidak dihubungkan dari FXML";
        assert hostField != null : "hostField tidak dihubungkan dari FXML";
        assert noHpField != null : "noHpField tidak dihubungkan dari FXML";
        assert sesiCombo != null : "sesiCombo tidak dihubungkan dari FXML";
        assert tanggalPicker != null : "tanggalPicker tidak dihubungkan dari FXML";
        assert waktuMulaiField != null : "waktuMulaiField tidak dihubungkan dari FXML";
        assert waktuBerakhirField != null : "waktuBerakhirField tidak dihubungkan dari FXML";
        assert satuHariRadio != null : "satuHariRadio tidak dihubungkan dari FXML";
        assert berjalanRadio != null : "berjalanRadio tidak dihubungkan dari FXML";
        assert eventTypeGroup != null : "eventTypeGroup tidak dihubungkan dari FXML";

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
        String userId = SessionManager.userId;

        if (judul.isEmpty() || kategori == null || tempat.isEmpty() || deskripsi.isEmpty() ||
                host.isEmpty() || noHp.isEmpty() || sesi == null || tanggal.isEmpty() ||
                waktuMulai.isEmpty() || waktuBerakhir.isEmpty() || jenisEvent.isEmpty()) {
            showAlert("Peringatan", "Semua field wajib diisi!");
            return;
        }

        if (userId == null || userId.trim().isEmpty()) {
            showAlert("Gagal", "User ID tidak boleh kosong/null.");
            return;
        }

        SupabaseService.buatEvent(
                judul, kategori, tempat, deskripsi,
                host, noHp, sesi, tanggal,
                waktuMulai, waktuBerakhir, jenisEvent, userId,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        showAlert("Gagal", "Gagal mengirim data ke Supabase.");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            System.out.println("RESPON SUPABASE:\n" + responseBody);

                            JSONArray array = new JSONArray(responseBody);

                            if (array.length() > 0) {
                                JSONObject json = array.getJSONObject(0);

                                if (json.has("id")) {
                                    String id = json.get("id").toString();
                                    SessionManager.id = id;

                                    javafx.application.Platform.runLater(() -> {
                                        try {
                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BuatEventBanner.fxml"));
                                            Parent root = loader.load();
                                            Stage stage = (Stage) judulField.getScene().getWindow();
                                            stage.setScene(new Scene(root));
                                            stage.setTitle("Banner Event");
                                            stage.show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            showAlert("Error", "Gagal membuka halaman berikutnya.");
                                        }
                                    });

                                } else {
                                    System.out.println("Field 'id' tidak ditemukan dalam respons JSON.");
                                    showAlert("Gagal", "Field 'id' tidak ditemukan pada data yang dikembalikan.");
                                }

                            } else {
                                System.out.println("Respons Supabase kosong.");
                                showAlert("Gagal", "Tidak ada data yang dikembalikan dari Supabase.");
                            }

                        } else {
                            System.out.println("Kode respons: " + response.code());
                            System.out.println("Isi respons: " + response.body().string());
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