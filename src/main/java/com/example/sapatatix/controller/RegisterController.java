package com.example.sapatatix.controller;

import com.example.sapatatix.service.SupabaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginRedirectBtn;

    @FXML
    public void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String fullname = fullnameField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
            System.out.println("Semua field harus diisi.");
            return;
        }

        SupabaseService.register(fullname, email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    System.out.println("Register gagal: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        System.out.println("Register berhasil!");
                        goToLogin(); // ⬅️ langsung arahkan ke halaman login
                    });
                } else {
                    String error = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println("Register gagal: " + error);
                    });
                }
            }
        });
    }

    @FXML
    public void handleGoToLogin() {
        goToLogin(); // ⬅️ panggil metode yang sama
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/masuk.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) fullnameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal membuka halaman login.");
        }
    }
}