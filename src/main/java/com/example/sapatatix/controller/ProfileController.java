package com.example.sapatatix.controller;

import com.example.sapatatix.Main;
import com.example.sapatatix.service.Session;
import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import java.io.File;
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
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileController {

    // Tombol navigasi sidebar
    @FXML private Button eventsBtn;
    @FXML private Button riwayatBtn;
    @FXML private Button buatEventBtn;
    @FXML private Button profileNavBtn;

    // Tombol dan field pada halaman profil
    @FXML private Button loginBtn;
    @FXML private Button saveBtn;
    @FXML private Label statusLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;

    @FXML private ImageView profileImageView;
    @FXML private ImageView sidebarProfileImageView;
    @FXML private Label sidebarEmailLabel;

    private File selectedProfileImageFile;

    @FXML
    public void initialize() {
        if (eventsBtn != null) eventsBtn.setOnAction(this::handleToDashboard);
        if (riwayatBtn != null) riwayatBtn.setOnAction(this::handleToRiwayat);
        if (buatEventBtn != null) buatEventBtn.setOnAction(this::handleToBuatEventEdit);
        if (loginBtn != null) loginBtn.setOnAction(this::handleKeluar);

        loadProfileData();
    }

    private void handleToDashboard(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/Dashboard.fxml", "Dashboard");
    }

    private void handleToRiwayat(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/Riwayat.fxml", "Riwayat");
    }

    private void handleToBuatEventEdit(ActionEvent event) {
        Main.setRoot("/com/example/sapatatix/FXML/BuatEventDeskripsi.fxml", "Buat Event");
    }

    private void handleKeluar(ActionEvent event) {
        SessionManager.userId = null;
        Session.setCurrentUserId(null);
        Session.setNamaDepan(null);
        Session.setNamaBelakang(null);
        Session.setTelepon(null);
        Session.setAlamat(null);
        Session.setKota(null);
        Main.setRoot("/com/example/sapatatix/FXML/BerandaFix.fxml", "Beranda");
    }

    private void loadProfileData() {
        final String userId = SessionManager.userId;
        if (userId == null || userId.isEmpty()) {
            Platform.runLater(() -> {
                showAlert(AlertType.INFORMATION, "Informasi", "Anda belum login. Beberapa fitur profil mungkin tidak tersedia.");
                firstNameField.setText("");
                lastNameField.setText("");
                phoneField.setText("");
                addressField.setText("");
                cityField.setText("");
                sidebarEmailLabel.setText("Belum Login");
                profileImageView.setImage(new Image("https://placehold.co/100x100/cccccc/333333?text=Profil"));
                sidebarProfileImageView.setImage(new Image("https://placehold.co/80x80/cccccc/333333?text=Profil"));
            });
            return;
        }

        SupabaseService.getProfile(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> showAlert(AlertType.ERROR, "Gagal", "Gagal memuat data profil: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseStr = responseBody.string();
                    if (response.isSuccessful() && !responseStr.trim().equals("[]")) {
                        JSONArray jsonArray = new JSONArray(responseStr);
                        JSONObject profileJson = jsonArray.getJSONObject(0);

                        Platform.runLater(() -> {
                            firstNameField.setText(profileJson.optString("nama_depan", ""));
                            lastNameField.setText(profileJson.optString("nama_belakang", ""));
                            phoneField.setText(profileJson.optString("telepon", ""));
                            addressField.setText(profileJson.optString("alamat", ""));
                            cityField.setText(profileJson.optString("kota", ""));
                            sidebarEmailLabel.setText(Session.getCurrentUserId() != null ? Session.getCurrentUserId() : "Email Pengguna");

                            String imageUrl = profileJson.optString("profile_picture_url", "");
                            if (!imageUrl.isEmpty()) {
                                try {
                                    Image profileImage = new Image(imageUrl);
                                    profileImageView.setImage(profileImage);
                                    sidebarProfileImageView.setImage(profileImage);
                                } catch (Exception e) {
                                    System.err.println("Gagal memuat foto profil: " + e.getMessage());
                                    profileImageView.setImage(new Image("https://placehold.co/100x100/cccccc/333333?text=Profil"));
                                    sidebarProfileImageView.setImage(new Image("https://placehold.co/80x80/cccccc/333333?text=Profil"));
                                }
                            } else {
                                profileImageView.setImage(new Image("https://placehold.co/100x100/cccccc/333333?text=Profil"));
                                sidebarProfileImageView.setImage(new Image("https://placehold.co/80x80/cccccc/333333?text=Profil"));
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            String errorBody = "";
                            try {
                                errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            showAlert(AlertType.INFORMATION, "Informasi", "Data profil tidak ditemukan. Kode: " + response.code() + ", Pesan: " + errorBody);
                        });
                        profileImageView.setImage(new Image("https://placehold.co/100x100/cccccc/333333?text=Profil"));
                        sidebarProfileImageView.setImage(new Image("https://placehold.co/80x80/cccccc/333333?text=Profil"));
                    }
                }
            }
        });
    }

    @FXML
    private void handleProfileImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage currentStage = (Stage) profileImageView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            selectedProfileImageFile = file;
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
            sidebarProfileImageView.setImage(image);
            showStatus("Foto profil dipilih. Klik 'Simpan Perubahan' untuk mengunggah.", "blue");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        final String userId = SessionManager.userId;
        if (userId == null || userId.isEmpty()) {
            showAlert(AlertType.ERROR, "Gagal", "Anda harus login untuk menyimpan perubahan profil.");
            return;
        }

        final String namaDepan = firstNameField.getText();
        final String namaBelakang = lastNameField.getText();
        final String telepon = phoneField.getText();
        final String alamat = addressField.getText();
        final String kota = cityField.getText();

        if (selectedProfileImageFile != null) {
            SupabaseService.uploadProfileImage(userId, selectedProfileImageFile, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Platform.runLater(() -> {
                        showAlert(AlertType.ERROR, "Gagal", "Gagal mengunggah foto profil: " + e.getMessage());
                        showStatus("Gagal mengunggah foto.", "red");
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            showStatus("Foto profil berhasil diunggah.", "green");
                            saveOtherProfileData(userId, namaDepan, namaBelakang, telepon, alamat, kota);
                        });
                    } else {
                        Platform.runLater(() -> {
                            String errorBody = ""; // Initialize errorBody
                            try {
                                errorBody = response.body() != null ? response.body().string() : "Unknown error"; // Corrected here
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                errorBody = "Error reading response body"; // Fallback in case string() itself throws
                            }
                            showAlert(AlertType.ERROR, "Gagal", "Gagal mengunggah foto profil. Kode: " + response.code() + ", Pesan: " + errorBody);
                            showStatus("Gagal mengunggah foto.", "red");
                        });
                    }
                }
            });
        } else {
            saveOtherProfileData(userId, namaDepan, namaBelakang, telepon, alamat, kota);
        }
    }

    private void saveOtherProfileData(String userId, String namaDepan, String namaBelakang, String telepon, String alamat, String kota) {
        SupabaseService.getProfile(userId, new Callback() {
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Gagal", "Data profil gagal disimpan/diperbarui. Koneksi bermasalah.");
                    showStatus("Gagal menyimpan data.", "red");
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String responseStr = responseBody.string();
                    if (response.isSuccessful()) {
                        boolean isEmpty = responseStr.trim().equals("[]");
                        if (isEmpty) {
                            SupabaseService.createProfile(userId, namaDepan, namaBelakang, telepon, alamat, kota, new Callback() {
                                public void onFailure(Call call, IOException e) {
                                    Platform.runLater(() -> {
                                        showAlert(AlertType.ERROR, "Gagal", "Data profil gagal disimpan.");
                                        showStatus("Gagal menyimpan data.", "red");
                                    });
                                }

                                public void onResponse(Call call, Response response) {
                                    Platform.runLater(() -> {
                                        if (response.isSuccessful()) {
                                            Session.setNamaDepan(namaDepan);
                                            Session.setNamaBelakang(namaBelakang);
                                            Session.setTelepon(telepon);
                                            Session.setAlamat(alamat);
                                            Session.setKota(kota);

                                            showAlert(AlertType.INFORMATION, "Sukses", "Data profil berhasil disimpan.");
                                            showStatus("Data berhasil disimpan.", "green");
                                            selectedProfileImageFile = null;
                                        } else {
                                            String errorBody = "";
                                            try {
                                                errorBody = response.body() != null ? response.body().string() : "Unknown error";
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            showAlert(AlertType.ERROR, "Gagal", "Data profil gagal disimpan. Kode: " + response.code() + ", Pesan: " + errorBody);
                                            showStatus("Gagal menyimpan data.", "red");
                                        }
                                    });
                                }
                            });
                        } else {
                            SupabaseService.updateProfile(userId, namaDepan, namaBelakang, telepon, alamat, kota, new Callback() {
                                public void onFailure(Call call, IOException e) {
                                    Platform.runLater(() -> {
                                        showAlert(AlertType.ERROR, "Gagal", "Data profil gagal diperbarui. Koneksi bermasalah.");
                                        showStatus("Gagal memperbarui data.", "red");
                                    });
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
                                            selectedProfileImageFile = null;
                                        } else {
                                            String errorBody = "";
                                            try {
                                                errorBody = response.body() != null ? response.body().string() : "Unknown error";
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            showAlert(AlertType.ERROR, "Gagal", "Gagal memperbarui data. Kode: " + response.code() + ", Pesan: " + errorBody);
                                            showStatus("Gagal memperbarui data.", "red");
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            String errorBody = "";
                            try {
                                errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            showAlert(AlertType.ERROR, "Gagal", "Gagal memuat data profil. Kode: " + response.code() + ", Pesan: " + errorBody);
                            showStatus("Gagal memuat data profil.", "red");
                        });
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