package com.example.sapatatix.model;

public class StudentTicket extends Ticket {
    private static final double STUDENT_DISCOUNT_FACTOR = 0.8; // Contoh diskon 20%
    private String validationRequirement;

    public StudentTicket(String id, String eventId, String namaTiket, double hargaDasar, int jumlahTersedia) {
        super(id, eventId, namaTiket, hargaDasar, jumlahTersedia);
        this.validationRequirement = "Wajib menunjukkan kartu pelajar/mahasiswa aktif saat masuk.";
    }

    @Override
    public double calculatePrice() {
        return hargaDasar * STUDENT_DISCOUNT_FACTOR;
    }

    @Override
    public String getAccessDetails() {
        return "Akses umum dengan diskon khusus pelajar. " + validationRequirement;
    }

    public String getValidationRequirement() {
        return validationRequirement;
    }
}