package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.imageio.ImageIO;

public class OrderSummaryController {

    @FXML private Label summaryTicketTypeLabel;
    @FXML private Label summaryVisitorNameLabel;
    @FXML private Label summaryVisitorEmailLabel;
    @FXML private Label summaryTicketPriceLabel;
    @FXML private ImageView qrCodeImageView;
    @FXML private Label summarySubtotalLabel;
    @FXML private Label summaryTaxLabel;
    @FXML private Label summaryTotalOrderLabel;

    private Stage dialogStage;
    private TransactionData transactionData;
    private final double TAX_RATE = 0.10;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
        initializeData();
    }

    private void initializeData() {
        if (transactionData != null) {
            summaryTicketTypeLabel.setText(transactionData.getTicketType());
            summaryVisitorNameLabel.setText(transactionData.getVisitorFullName());
            summaryVisitorEmailLabel.setText(transactionData.getVisitorEmail());

            // Calculate totals (recalculate for accuracy)
            double subtotal = transactionData.getTicketPrice() * transactionData.getQuantity();
            double tax = subtotal * TAX_RATE;
            double total = subtotal + tax;

            summaryTicketPriceLabel.setText(formatRupiah(transactionData.getTicketPrice()));
            summarySubtotalLabel.setText(formatRupiah(subtotal));
            summaryTaxLabel.setText(formatRupiah(tax));
            summaryTotalOrderLabel.setText(formatRupiah(total));

            String fakeQrCodeUrl = "https://placehold.co/200x200/cccccc/333333?text=QR+Code+Palsu";
            qrCodeImageView.setImage(new Image(fakeQrCodeUrl));
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
    private void handleFinish() {
        dialogStage.close();
    }
}