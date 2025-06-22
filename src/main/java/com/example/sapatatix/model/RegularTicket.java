package com.example.sapatatix.model;

public class RegularTicket extends Ticket {

    public RegularTicket(String id, String eventId, String namaTiket, double hargaDasar, int jumlahTersedia) {
        super(id, eventId, namaTiket, hargaDasar, jumlahTersedia);
    }

    @Override
    public double calculatePrice() {
        return hargaDasar; // Tiket reguler harganya sama dengan harga dasar
    }

    @Override
    public String getAccessDetails() {
        return "Akses umum ke area event.";
    }
}