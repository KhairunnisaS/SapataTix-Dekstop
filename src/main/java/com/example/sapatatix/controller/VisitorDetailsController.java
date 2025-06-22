package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VisitorDetailsController {

    @FXML private Label eventNameHeaderLabel;
    @FXML private Label eventDateTimeHeaderLabel;
    @FXML private Label ticketInfoLabel;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private CheckBox agreementCheckbox;
    @FXML private Label footerQuantityLabel;
    @FXML private Label footerTotalPriceLabel;

    private Stage dialogStage;
    private TransactionData transactionData;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
        initializeData();
    }

    private void initializeData() {
        if (transactionData != null && transactionData.getEvent() != null) {
            eventNameHeaderLabel.setText(transactionData.getEvent().optString("judul", ""));

            String tanggalMulai = transactionData.getEvent().optString("tanggal_mulai", "");
            String waktuMulai = transactionData.getEvent().optString("waktu_mulai", "");
            String waktuBerakhir = transactionData.getEvent().optString("waktu_berakhir", "");

            String formattedDate = "";
            if (!tanggalMulai.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(tanggalMulai);
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
                    formattedDate = date.format(dateFormatter);
                } catch (Exception e) {
                    formattedDate = "Tanggal Tidak Valid";
                }
            }

            String formattedTime = "";
            if (!waktuMulai.isEmpty() && !waktuBerakhir.isEmpty()) {
                try {
                    LocalTime startTime = LocalTime.parse(waktuMulai);
                    LocalTime endTime = LocalTime.parse(waktuBerakhir);
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // 24-hour format
                    formattedTime = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
                } catch (Exception e) {
                    formattedTime = waktuMulai + " - " + waktuBerakhir;
                }
            }
            eventDateTimeHeaderLabel.setText(formattedDate + " | " + formattedTime);

            ticketInfoLabel.setText(transactionData.getTicketType() + ": Ticket #" + transactionData.getQuantity());

            footerQuantityLabel.setText(String.valueOf(transactionData.getQuantity()));
            footerTotalPriceLabel.setText(formatRupiah(transactionData.getTicketPrice() * transactionData.getQuantity()));

            // Load existing data if available (e.g., if returning from next stage)
            fullNameField.setText(transactionData.getVisitorFullName());
            emailField.setText(transactionData.getVisitorEmail());
            phoneField.setText(transactionData.getVisitorPhone());
        }
    }

    private String formatRupiah(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(value).replace("Rp", "Rp");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/TicketSelectionPopup.fxml"));
            Parent root = loader.load();

            TicketSelectionController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Pilih Tiket");
            newStage.initModality(dialogStage.getModality());
            newStage.initOwner(dialogStage.getOwner());
            newStage.setScene(new Scene(root));

            controller.setDialogStage(newStage);
            controller.setTransactionData(transactionData); // Pass data back

            dialogStage.close();
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Pilih Tiket: " + e.getMessage());
        }
    }

    @FXML
    private void handleContinueToPaymentMethod() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Peringatan", "Semua kolom wajib diisi.");
            return;
        }
        if (!agreementCheckbox.isSelected()) {
            showAlert("Peringatan", "Anda harus menyetujui Syarat dan Ketentuan serta Kebijakan Privasi.");
            return;
        }

        // Save data to transactionData
        transactionData.setVisitorFullName(fullName);
        transactionData.setVisitorEmail(email);
        transactionData.setVisitorPhone(phone);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/PaymentMethodPopup.fxml"));
            Parent root = loader.load();

            PaymentMethodController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Metode Pembayaran");
            newStage.initModality(dialogStage.getModality());
            newStage.initOwner(dialogStage.getOwner());
            newStage.setScene(new Scene(root));

            controller.setDialogStage(newStage);
            controller.setTransactionData(transactionData); // Pass data

            dialogStage.close();
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Metode Pembayaran: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}