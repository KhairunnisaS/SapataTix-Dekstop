package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class PaymentSuccessController {

    private Stage dialogStage;
    private TransactionData transactionData;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    @FXML
    private void handleContinueToOrderSummary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/OrderSummaryPopup.fxml"));
            Parent root = loader.load();

            OrderSummaryController controller = loader.getController();
            Stage newStage = new Stage();
            newStage.setTitle("Rangkuman Pesanan");
            newStage.initModality(dialogStage.getModality());
            newStage.initOwner(dialogStage.getOwner());
            newStage.setScene(new Scene(root));

            controller.setDialogStage(newStage);
            controller.setTransactionData(transactionData); // Pass data

            dialogStage.close();
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat pop-up Rangkuman Pesanan: " + e.getMessage());
        }
    }
}