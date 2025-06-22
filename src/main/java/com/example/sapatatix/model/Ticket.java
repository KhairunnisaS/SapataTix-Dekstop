package com.example.sapatatix.model;

import org.json.JSONObject;

// Kelas abstrak karena tidak semua tiket adalah tiket dasar, melainkan jenis spesifik
public abstract class Ticket {
    protected String id; // ID tiket dari database (kolom 'id' di tabel 'tiket')
    protected String eventId; // ID event yang terkait
    protected String namaTiket; // Misal: "Reguler", "VIP", "Pelajar"
    protected double hargaDasar;
    protected int jumlahTersedia; // Stok tiket

    public Ticket(String id, String eventId, String namaTiket, double hargaDasar, int jumlahTersedia) {
        this.id = id;
        this.eventId = eventId;
        this.namaTiket = namaTiket;
        this.hargaDasar = hargaDasar;
        this.jumlahTersedia = jumlahTersedia;
    }

    // Metode abstrak untuk menghitung harga, diimplementasikan berbeda oleh subclass
    public abstract double calculatePrice();

    // Metode abstrak untuk mendapatkan detail akses khusus (misal: area VIP)
    public abstract String getAccessDetails();

    // --- Getters untuk field umum ---
    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public String getNamaTiket() { return namaTiket; }
    public double getHargaDasar() { return hargaDasar; }
    public int getJumlahTersedia() { return jumlahTersedia; }

    // Metode utilitas untuk membuat objek Ticket dari JSONObject (Ini akan perlu diadaptasi)
    public static Ticket fromJson(JSONObject json) {
        String namaTiket = json.optString("nama_tiket", "");
        switch (namaTiket) {
            case "Reguler":
                return new RegularTicket(
                        json.optString("id"), json.optString("event_id"), json.optString("nama_tiket"),
                        json.optDouble("harga", 0.0), json.optInt("jumlah", 0)
                );
            case "VIP":
                return new VIPTicket(
                        json.optString("id"), json.optString("event_id"), json.optString("nama_tiket"),
                        json.optDouble("harga", 0.0), json.optInt("jumlah", 0)
                );
            case "Pelajar":
                return new StudentTicket(
                        json.optString("id"), json.optString("event_id"), json.optString("nama_tiket"),
                        json.optDouble("harga", 0.0), json.optInt("jumlah", 0)
                );
            default:
                // Fallback jika jenis tiket tidak cocok, bisa kembalikan RegularTicket atau null/exception
                return new RegularTicket(
                        json.optString("id"), json.optString("event_id"), json.optString("nama_tiket"),
                        json.optDouble("harga", 0.0), json.optInt("jumlah", 0)
                );
        }
    }
}