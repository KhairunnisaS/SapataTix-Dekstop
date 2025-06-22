package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.service.SupabaseService; // Diperlukan untuk saveTicketPurchase
import com.example.sapatatix.session.SessionManager; // Diperlukan untuk mendapatkan userId

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONObject; // Diperlukan untuk memparsing respons Supabase

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentMethodController {

    @FXML private ToggleGroup paymentMethodGroup;
    @FXML private RadioButton danaRadioBtn;
    @FXML private RadioButton gopayRadioBtn;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalOrderLabel;

    private Stage dialogStage;
    private TransactionData transactionData;
    private final double TAX_RATE = 0.10; // 10% pajak

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
        updatePaymentSummary();
    }

    private void updatePaymentSummary() {
        double subtotal = transactionData.getTicketPrice() * transactionData.getQuantity();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText(formatRupiah(subtotal));
        taxLabel.setText(formatRupiah(tax));
        totalOrderLabel.setText(formatRupiah(total));
    }

    private String formatRupiah(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(value).replace("Rp", "Rp");
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/VisitorDetailsPopup.fxml"));
            Parent root = loader.load();

            VisitorDetailsController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Detail Pengunjung");
            newStage.initModality(dialogStage.getModality());
            newStage.initOwner(dialogStage.getOwner());
            newStage.setScene(new Scene(root));

            controller.setDialogStage(newStage);
            controller.setTransactionData(transactionData);

            dialogStage.close();
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Detail Pengunjung: " + e.getMessage());
        }
    }

    @FXML
    private void handlePayNow() {
        RadioButton selectedRadioButton = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selectedRadioButton == null) {
            showAlert("Peringatan", "Pilih metode pembayaran terlebih dahulu.");
            return;
        }

        String paymentMethod = selectedRadioButton.getText();
        transactionData.setPaymentMethod(paymentMethod);

        // Hitung total akhir
        double subtotal = transactionData.getTicketPrice() * transactionData.getQuantity();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        // Mendapatkan userId dari SessionManager
        String userId = SessionManager.userId; // Menggunakan SessionManager.userId
        if (userId == null || userId.isEmpty()) {
            showAlert("Error", "User tidak login. Silakan login terlebih dahulu.");
            return;
        }

        // Simulasikan transaksi berhasil dan dapatkan ID transaksi & QR code data
        // Dalam aplikasi nyata, ini akan melibatkan API gateway pembayaran dan respons server
        String dummyTransactionId = "TRX" + System.currentTimeMillis();
        String dummyQrCodeData = "Event: " + transactionData.getEvent().optString("judul") +
                "\nQty: " + transactionData.getQuantity() +
                "\nBuyer: " + transactionData.getVisitorFullName() +
                "\nEmail: " + transactionData.getVisitorEmail() +
                "\nTransaction ID: " + dummyTransactionId;
        transactionData.setTransactionId(dummyTransactionId);
        transactionData.setQrCodeData(dummyQrCodeData);


        // Panggil SupabaseService untuk menyimpan transaksi
        SupabaseService.saveTicketPurchase(
                transactionData.getEvent().optString("id"), // ID Event dari JSONObject event
                userId,                                     // ID User yang login
                transactionData.getTicketType(),
                transactionData.getTicketPrice(),
                transactionData.getQuantity(),
                transactionData.getVisitorFullName(),
                transactionData.getVisitorEmail(),
                transactionData.getVisitorPhone(),
                transactionData.getPaymentMethod(),
                total,                                      // Total pembayaran
                transactionData.getQrCodeData(),            // Data QR Code yang akan disimpan
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Platform.runLater(() -> showAlert("Gagal", "Gagal menyimpan transaksi: " + e.getMessage()));
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                System.out.println("Transaksi berhasil disimpan: " + responseBody);
                                // Lanjutkan ke pop-up pembayaran berhasil
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/PaymentSuccessPopup.fxml"));
                                    Parent root = loader.load();

                                    PaymentSuccessController controller = loader.getController();
                                    Stage newStage = new Stage();
                                    newStage.setTitle("Pembayaran Berhasil");
                                    newStage.initModality(dialogStage.getModality());
                                    newStage.initOwner(dialogStage.getOwner());
                                    newStage.setScene(new Scene(root));

                                    controller.setDialogStage(newStage);
                                    controller.setTransactionData(transactionData); // Teruskan data

                                    dialogStage.close();
                                    newStage.showAndWait();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showAlert("Error", "Gagal memuat halaman sukses.");
                                }
                            });
                        } else {
                            Platform.runLater(() -> showAlert("Gagal", "Gagal menyimpan transaksi. Kode: " + response.code() + ", Pesan: " + responseBody));
                        }
                    }
                }
        );
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}