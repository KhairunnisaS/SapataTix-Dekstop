package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class TicketSelectionController {

    @FXML private Label ticketTypeNameLabel;
    @FXML private Label ticketTypePriceLabel;
    @FXML private Spinner<Integer> ticketQuantitySpinner;
    @FXML private Label totalQuantityLabel;
    @FXML private Label totalPriceLabel;

    private Stage dialogStage;
    private TransactionData transactionData;
    private double currentTicketPrice; // Harga per tiket dari eventData

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
        initializeData();
    }

    @FXML
    public void initialize() {
        // Spinner setup, will be finalized in setTransactionData
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        ticketQuantitySpinner.setValueFactory(valueFactory);
        ticketQuantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTotals());
    }

    private void initializeData() {
        if (transactionData != null && transactionData.getEvent() != null) {
            // Asumsi event memiliki "harga_tiket" dan "jenis_tiket"
            // Anda mungkin perlu menyesuaikan nama kolom 'harga_tiket' dan 'jenis_tiket' jika berbeda di database Supabase Anda
            String ticketName = "Tiket Reguler " + transactionData.getEvent().optString("judul", "Event");
            currentTicketPrice = transactionData.getEvent().optDouble("harga_tiket", 600000.0); // Default price jika tidak ditemukan

            ticketTypeNameLabel.setText(ticketName);
            ticketTypePriceLabel.setText(formatRupiah(currentTicketPrice));

            // Set initial quantity from transactionData if already selected
            if (transactionData.getQuantity() > 0) {
                ticketQuantitySpinner.getValueFactory().setValue(transactionData.getQuantity());
            } else {
                ticketQuantitySpinner.getValueFactory().setValue(1); // Default to 1
            }
            updateTotals();
        }
    }

    private void updateTotals() {
        int quantity = ticketQuantitySpinner.getValue();
        double total = currentTicketPrice * quantity;

        totalQuantityLabel.setText(String.valueOf(quantity));
        totalPriceLabel.setText(formatRupiah(total));

        // Update transactionData
        transactionData.setQuantity(quantity);
        transactionData.setTicketType(ticketTypeNameLabel.getText()); // Simpan jenis tiket yang dipilih
        transactionData.setTicketPrice(currentTicketPrice); // Simpan harga per tiket
    }

    private String formatRupiah(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(value).replace("Rp", "Rp"); // Menghilangkan desimal jika .00
    }

    @FXML
    private void handleBack() {
        dialogStage.close(); // Tutup pop-up saat ini, kembali ke EventDetailPopup
        // Anda mungkin perlu membuka kembali EventDetailPopup jika ini adalah alur "kembali" dari langkah pertama
        // Namun, jika EventDetailPopup masih terbuka di belakangnya, cukup close ini.
    }

    @FXML
    private void handleContinueToVisitorDetails() {
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
            controller.setTransactionData(transactionData); // Teruskan data transaksi

            dialogStage.close(); // Tutup stage pemilihan tiket
            newStage.showAndWait(); // Tampilkan stage detail pengunjung
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Detail Pengunjung: " + e.getMessage());
        }
    }
}