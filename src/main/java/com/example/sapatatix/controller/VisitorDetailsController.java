package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.model.Event; // Import Event model
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
        if (transactionData != null && transactionData.getEventObject() != null) {
            Event event = transactionData.getEventObject(); // Ambil objek Event dari TransactionData

            eventNameHeaderLabel.setText(event.getJudul()); // Menggunakan getter dari objek Event

            String tanggalMulai = event.getTanggalMulai().format(DateTimeFormatter.ISO_LOCAL_DATE); // Ambil dari objek Event
            String waktuMulai = event.getWaktuMulai().format(DateTimeFormatter.ISO_LOCAL_TIME);     // Ambil dari objek Event
            String waktuBerakhir = event.getWaktuBerakhir().format(DateTimeFormatter.ISO_LOCAL_TIME); // Ambil dari objek Event


            String formattedDate = "";
            if (event.getTanggalMulai() != null) {
                formattedDate = event.getTanggalMulai().format(DateTimeFormatter.ofPattern("dd MMM"));
            } else {
                formattedDate = "Tanggal Tidak Valid"; // Fallback jika tanggal null
            }

            String formattedTime = "";
            if (event.getWaktuMulai() != null && event.getWaktuBerakhir() != null) {
                formattedTime = event.getWaktuMulai().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + event.getWaktuBerakhir().format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                formattedTime = "Waktu Tidak Valid"; // Fallback jika waktu null
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
            controller.setTransactionData(transactionData);

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
            controller.setTransactionData(transactionData);

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