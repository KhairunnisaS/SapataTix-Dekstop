package com.example.sapatatix.controller;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import com.example.sapatatix.Main;
import com.example.sapatatix.service.Session;
import com.example.sapatatix.service.SupabaseService;
import java.io.File;
import javafx.scene.image.ImageView;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProfileController {

    // Tombol navigasi sidebar
    @FXML private Button eventsBtn;
    @FXML private Button riwayatBtn;
    @FXML private Button buatEventBtn;

    // Tombol dan field pada halaman profil
    @FXML private Button loginBtn;
    @FXML private Button saveBtn;
    @FXML private Label statusLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;

    @FXML private javafx.scene.image.ImageView fotoProfilView;

    // Tambahan untuk foto profil
    @FXML private ImageView profileImageView;
    @FXML private Button uploadPhotoBtn;

    @FXML
    public void initialize() {
        if (eventsBtn != null) eventsBtn.setOnAction(this::handleToDashboard);
        if (riwayatBtn != null) riwayatBtn.setOnAction(this::handleToRiwayat);
        if (buatEventBtn != null) buatEventBtn.setOnAction(this::handleToBuatEventEdit);
        if (loginBtn != null) loginBtn.setOnAction(this::handleKeluar);

        // Isi field dari data session jika tersedia
        if (Session.getNamaDepan() != null) firstNameField.setText(Session.getNamaDepan());
        if (Session.getNamaBelakang() != null) lastNameField.setText(Session.getNamaBelakang());
        if (Session.getTelepon() != null) phoneField.setText(Session.getTelepon());
        if (Session.getAlamat() != null) addressField.setText(Session.getAlamat());
        if (Session.getKota() != null) cityField.setText(Session.getKota());

        // Tampilkan kembali foto profil jika sudah ada di session
        String fotoPath = Session.getFotoProfilPath();
        if (fotoPath != null && !fotoPath.isEmpty()) {
            Image foto = new Image(fotoPath);
            fotoProfilView.setImage(foto); // Pastikan fx:id="fotoProfilView" di FXML
        }


        // Jika foto sudah pernah diupload sebelumnya
        if (Session.getFotoProfilPath() != null && profileImageView != null) {
            profileImageView.setImage(new Image("file:" + Session.getFotoProfilPath()));
        }
    }

    // Fungsi upload foto profil

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);  // <- null bisa kamu ganti dengan stage utama jika perlu
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            fotoProfilView.setImage(image);

            // Simpan path ke session
            Session.setFotoProfilPath(selectedFile.toURI().toString());
            System.out.println("Gambar dipilih: " + selectedFile.toURI());  // DEBUG
        } else {
            System.out.println("Tidak ada file dipilih.");  // DEBUG
        }
    }


    // Navigasi ke Dashboard
    private void handleToDashboard(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/Dashboard.fxml", "Dashboard");
    }

    // Navigasi ke Riwayat
    private void handleToRiwayat(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/Riwayat.fxml", "Riwayat");
    }

    // Navigasi ke Buat Event Edit
    private void handleToBuatEventEdit(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/BuatEventEdit.fxml", "Buat Event");
    }

    // Navigasi ke halaman utama
    private void handleKeluar(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/BerandaFix.fxml", "Beranda");
    }

    // Simpan atau perbarui data profil
    @FXML
    private void handleSave(ActionEvent event) {
        final String userId = Session.getCurrentUserId();
        final String namaDepan = firstNameField.getText();
        final String namaBelakang = lastNameField.getText();
        final String telepon = phoneField.getText();
        final String alamat = addressField.getText();
        final String kota = cityField.getText();

        SupabaseService.getProfile(userId, new Callback() {
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showAlert(AlertType.ERROR, "Gagal", "Data gagal disimpan."));
            }

            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseStr = responseBody.string();
                    if (response.isSuccessful()) {
                        boolean isEmpty = responseStr.trim().equals("[]");
                        if (isEmpty) {
                            SupabaseService.createProfile(userId, namaDepan, namaBelakang, telepon, alamat, kota, new Callback() {
                                public void onFailure(Call call, IOException e) {
                                    Platform.runLater(() -> showAlert(AlertType.ERROR, "Gagal", "Data gagal disimpan."));
                                }

                                public void onResponse(Call call, Response response) {
                                    Platform.runLater(() -> {
                                        if (response.isSuccessful()) {
                                            Session.setNamaDepan(namaDepan);
                                            Session.setNamaBelakang(namaBelakang);
                                            Session.setTelepon(telepon);
                                            Session.setAlamat(alamat);
                                            Session.setKota(kota);

                                            showAlert(AlertType.INFORMATION, "Sukses", "Data berhasil disimpan.");
                                            showStatus("Data berhasil disimpan.", "green");
                                        } else {
                                            showAlert(AlertType.ERROR, "Gagal", "Data gagal disimpan.");
                                        }
                                    });
                                }
                            });
                        } else {
                            SupabaseService.updateProfile(userId, namaDepan, namaBelakang, telepon, alamat, kota, new Callback() {
                                public void onFailure(Call call, IOException e) {
                                    Platform.runLater(() -> showAlert(AlertType.ERROR, "Gagal", "Data gagal disimpan."));
                                }

                                public void onResponse(Call call, Response response) {
                                    Platform.runLater(() -> {
                                        if (response.isSuccessful()) {
                                            Session.setNamaDepan(namaDepan);
                                            Session.setNamaBelakang(namaBelakang);
                                            Session.setTelepon(telepon);
                                            Session.setAlamat(alamat);
                                            Session.setKota(kota);

                                            showAlert(AlertType.INFORMATION, "Diperbarui", "Data profil berhasil diperbarui.");
                                            showStatus("Data berhasil diperbarui.", "green");
                                        } else {
                                            showAlert(AlertType.ERROR, "Gagal", "Gagal memperbarui data.");
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        Platform.runLater(() -> showAlert(AlertType.ERROR, "Gagal", "Gagal memuat data profil."));
                    }
                }
            }
        });
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
