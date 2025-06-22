// src/main/java/com/example/sapatatix/service/TransactionData.java
package com.example.sapatatix.service;

import org.json.JSONObject;

public class TransactionData {
    private JSONObject event;
    private String ticketType;
    private double ticketPrice;
    private int quantity;
    private String visitorFullName;
    private String visitorEmail;
    private String visitorPhone;
    private String paymentMethod;
    private String transactionId;
    private String qrCodeData;
    private String ticketId; // Add this field

    public TransactionData(JSONObject event) {
        this.event = event;
    }

    public JSONObject getEvent() { return event; }
    public String getTicketType() { return ticketType; }
    public double getTicketPrice() { return ticketPrice; }
    public int getQuantity() { return quantity; }
    public String getVisitorFullName() { return visitorFullName; }
    public String getVisitorEmail() { return visitorEmail; }
    public String getVisitorPhone() { return visitorPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public String getQrCodeData() { return qrCodeData; }
    public String getTicketId() { return ticketId; } // Getter for ticketId


    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setVisitorFullName(String visitorFullName) { this.visitorFullName = visitorFullName; }
    public void setVisitorEmail(String visitorEmail) { this.visitorEmail = visitorEmail; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; } // Setter for ticketId
}