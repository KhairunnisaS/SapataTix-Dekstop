package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import com.example.sapatatix.model.Ticket; // Import kelas Ticket model
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
    private double currentTicketPrice; // Harga per tiket dari objek Ticket

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
        initializeData();
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        ticketQuantitySpinner.setValueFactory(valueFactory);
        ticketQuantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTotals());
    }

    private void initializeData() {
        if (transactionData != null && transactionData.getEventObject() != null && transactionData.getTicketObject() != null) {
            Ticket ticket = transactionData.getTicketObject(); // Ambil objek Ticket

            // Menggunakan getter dan metode polimorfik dari objek Ticket
            String ticketName = ticket.getNamaTiket();
            currentTicketPrice = ticket.calculatePrice(); // Menggunakan calculatePrice() polimorfik

            ticketTypeNameLabel.setText(ticketName);
            ticketTypePriceLabel.setText(formatRupiah(currentTicketPrice));

            if (transactionData.getQuantity() > 0) {
                ticketQuantitySpinner.getValueFactory().setValue(transactionData.getQuantity());
            } else {
                ticketQuantitySpinner.getValueFactory().setValue(1);
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
        // Pastikan ticketType dan ticketPrice di TransactionData juga diupdate sesuai objek Ticket
        if (transactionData.getTicketObject() != null) {
            transactionData.setTicketType(transactionData.getTicketObject().getNamaTiket());
            transactionData.setTicketPrice(transactionData.getTicketObject().calculatePrice());
        }
    }

    private String formatRupiah(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(value).replace("Rp", "Rp");
    }

    @FXML
    private void handleBack() {
        dialogStage.close();
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
            controller.setTransactionData(transactionData);

            dialogStage.close();
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Detail Pengunjung: " + e.getMessage());
        }
    }
}