package com.example.sapatatix.controller;

import com.example.sapatatix.service.TransactionData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

// Mengomentari import ZXing karena kita akan menggunakan QR code palsu
// import com.google.zxing.BarcodeFormat;
// import com.google.zxing.WriterException;
// import com.google.zxing.client.j2se.MatrixToImageWriter;
// import com.google.zxing.common.BitMatrix;
// import com.google.zxing.qrcode.QRCodeWriter;

// Mengomentari import AWT image karena tidak lagi memproses BufferedImage
// import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream; // Tetap diperlukan jika placeholder dimuat dari stream
import java.io.ByteArrayOutputStream; // Tetap diperlukan jika placeholder dimuat dari stream
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.imageio.ImageIO; // Mungkin tidak lagi diperlukan jika tidak memproses BufferedImage

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

            // ✅ Ganti logika pembuatan QR Code dengan menampilkan gambar palsu
            // Anda bisa menggunakan URL placeholder atau menempatkan gambar QR palsu di folder Gambar
            // Contoh menggunakan URL placeholder:
            String fakeQrCodeUrl = "https://placehold.co/200x200/cccccc/333333?text=QR+Code+Palsu";
            qrCodeImageView.setImage(new Image(fakeQrCodeUrl));

            // Jika Anda memiliki gambar QR palsu di folder Gambar (misal: "fake_qr.png")
            // Maka gunakan: qrCodeImageView.setImage(new Image(getClass().getResourceAsStream("/com/example/sapatatix/Gambar/fake_qr.png")));

            // Kode asli ZXing dikomentari
            /*
            String qrCodeContent = transactionData.getQrCodeData();
            if (qrCodeContent != null && !qrCodeContent.isEmpty()) {
                try {
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 200, 200); // Width, Height
                    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(qrImage, "png", baos);
                    Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));
                    qrCodeImageView.setImage(image);
                } catch (WriterException | IOException e) {
                    e.printStackTrace();
                    System.err.println("Gagal membuat QR Code: " + e.getMessage());
                    qrCodeImageView.setImage(new Image("https://placehold.co/200x200/cccccc/333333?text=QR+Error"));
                }
            } else {
                qrCodeImageView.setImage(new Image("https://placehold.co/200x200/cccccc/333333?text=No+QR+Data"));
            }
            */
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