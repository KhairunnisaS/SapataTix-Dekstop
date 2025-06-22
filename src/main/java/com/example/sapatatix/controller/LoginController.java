package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import com.example.sapatatix.session.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email dan password tidak boleh kosong.");
            return;
        }

        SupabaseService.login(email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    messageLabel.setText("Login gagal: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string(); // hanya panggil .string() sekali

                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (jsonArray.length() > 0) {
                        JSONObject user = jsonArray.getJSONObject(0);

                        // ✅ Simpan userId ke session
                        SessionManager.userId = user.get("id").toString();

                        Platform.runLater(() -> {
                            messageLabel.setText("Login berhasil!");
                            goToDashboard();
                        });
                    } else {
                        Platform.runLater(() -> {
                            messageLabel.setText("Email atau password salah!");
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        messageLabel.setText("Login gagal. Kode: " + response.code());
                    });
                }
            }
        });
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Gagal memuat halaman dashboard.");
        }
    }

    @FXML
    public void handleBack() {
        goToRegister();
    }

    @FXML
    public void handleGoToRegister() {
        goToRegister();
    }

    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/Daftar.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Gagal membuka halaman register.");
        }
    }
}