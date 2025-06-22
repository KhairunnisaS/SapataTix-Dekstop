package com.example.sapatatix.service;

import com.example.sapatatix.model.Event;
import com.example.sapatatix.model.Ticket;
import org.json.JSONObject;

public class TransactionData {
    private Event eventObject;
    private Ticket ticketObject;

    private String ticketType;
    private double ticketPrice;
    private int quantity;
    private String visitorFullName;
    private String visitorEmail;
    private String visitorPhone;
    private String paymentMethod;
    private String transactionId;
    private String qrCodeData;
    private String ticketId;

    public TransactionData(Event eventObject) {
        this.eventObject = eventObject;
    }

    // --- Getters & Setters ---

    public Event getEventObject() { return eventObject; }
    public void setEventObject(Event eventObject) { this.eventObject = eventObject; }

    public Ticket getTicketObject() { return ticketObject; }
    public void setTicketObject(Ticket ticketObject) { this.ticketObject = ticketObject; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getVisitorFullName() { return visitorFullName; }
    public void setVisitorFullName(String visitorFullName) { this.visitorFullName = visitorFullName; }

    public String getVisitorEmail() { return visitorEmail; }
    public void setVisitorEmail(String visitorEmail) { this.visitorEmail = visitorEmail; }

    public String getVisitorPhone() { return visitorPhone; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
}