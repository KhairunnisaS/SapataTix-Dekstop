package com.example.sapatatix.model;

public class VIPTicket extends Ticket {
    private static final double VIP_MULTIPLIER = 1.5; // Contoh pengali harga VIP
    private String vipAreaDescription;

    public VIPTicket(String id, String eventId, String namaTiket, double hargaDasar, int jumlahTersedia) {
        super(id, eventId, namaTiket, hargaDasar, jumlahTersedia);
        this.vipAreaDescription = "Area khusus VIP dengan pemandangan terbaik dan fasilitas eksklusif.";
    }

    @Override
    public double calculatePrice() {
        return hargaDasar * VIP_MULTIPLIER;
    }

    @Override
    public String getAccessDetails() {
        return "Akses ke area VIP: " + vipAreaDescription;
    }

    public String getVipAreaDescription() {
        return vipAreaDescription;
    }
}