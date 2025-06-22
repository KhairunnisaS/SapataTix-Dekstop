package com.example.sapatatix.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import okhttp3.Callback;

public class SupabaseService {
    // GANTI: Sesuaikan dengan URL project Supabase kamu
    private static final String PROJECT_URL = "https://mcqhhdeqkuklvxglpycb.supabase.co";
    // GANTI: Public API Key dari Project Settings > API
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1jcWhoZGVxa3VrbHZ4Z2xweWNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA0NDM3NjUsImV4cCI6MjA2NjAxOTc2NX0.BpXkt9b6FXT6lfhShUX8f2BCL7_M_iqwYsiWsEe9nf8";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Existing methods (register, login, createProfile, getProfile, updateProfile, etc.)

    // 🔹 REGISTER USER (dengan fullname)
    public static void register(String fullname, String email, String password, Callback callback) {
        String json = "{"
                + "\"fullname\":\"" + fullname + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\""
                + "}";

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/user")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 LOGIN USER (cek data)
    public static void login(String email, String password, Callback callback) {
        HttpUrl url = HttpUrl.parse(PROJECT_URL + "/rest/v1/user")
                .newBuilder()
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("password", "eq." + password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 SIMPAN DATA PROFIL
    public static void createProfile(String userId, String namaDepan, String namaBelakang, String telepon, String alamat, String kota, Callback callback) {
        String json = "{"
                + "\"user_id\":\"" + userId + "\","
                + "\"nama_depan\":\"" + namaDepan + "\","
                + "\"nama_belakang\":\"" + namaBelakang + "\","
                + "\"telepon\":\"" + telepon + "\","
                + "\"alamat\":\"" + alamat + "\","
                + "\"kota\":\"" + kota + "\""
                + "}";

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/profile")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 AMBIL DATA PROFIL BERDASARKAN USER_ID
    public static void getProfile(String userId, Callback callback) {
        HttpUrl url = HttpUrl.parse(PROJECT_URL + "/rest/v1/profile")
                .newBuilder()
                .addQueryParameter("user_id", "eq." + userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 UPDATE DATA PROFIL
    public static void updateProfile(String userId, String namaDepan, String namaBelakang, String telepon, String alamat, String kota, Callback callback) {
        String json = "{"
                + "\"nama_depan\":\"" + namaDepan + "\","
                + "\"nama_belakang\":\"" + namaBelakang + "\","
                + "\"telepon\":\"" + telepon + "\","
                + "\"alamat\":\"" + alamat + "\","
                + "\"kota\":\"" + kota + "\""
                + "}";

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/profile?user_id=eq." + userId)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .method("PATCH", RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 BUAT EVENT
    public static void buatEvent(String judul, String kategori, String tempat, String deskripsi,
                                 String host, String noHp, String sesi, String tanggal,
                                 String waktuMulai, String waktuBerakhir, String jenisEvent, String userId,
                                 Callback callback) {

        System.out.println("MULAI kirim ke Supabase...");

        JSONObject json = new JSONObject();
        json.put("judul", judul);
        json.put("kategori", kategori);
        json.put("tempat", tempat);
        json.put("deskripsi", deskripsi);
        json.put("nama_host", host);
        json.put("no_hp_host", noHp);
        json.put("sesi", sesi);
        json.put("tanggal_mulai", tanggal);
        json.put("waktu_mulai", waktuMulai);
        json.put("waktu_berakhir", waktuBerakhir);
        json.put("jenis_event", jenisEvent);

        if (userId != null && !userId.isEmpty()) {
            try {
                json.put("user_id", Integer.parseInt(userId));
            } catch (NumberFormatException e) {
                System.err.println("❗ userId tidak bisa di-parse ke INT: " + userId + ". Error: " + e.getMessage());
            }
        } else {
            System.out.println("❗ userId kosong atau null!");
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/event")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);

        System.out.println("SELESAI kirim ke Supabase (asynchronous)");
    }

    // Metode insertEvent ini sepertinya duplikat atau tidak digunakan, pertimbangkan untuk menghapusnya
    public static void insertEvent(JSONObject data, Callback callback) {
        RequestBody body = RequestBody.create(data.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(PROJECT_URL + "/events")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 UPLOAD BANNER (MODIFIED to actually upload to Storage)
    public static void uploadBanner(String eventId, File imageFile, Callback callback) {
        String bucketName = "event-banner"; // GANTI: Pastikan nama bucket Anda di Supabase Storage
        String fileNameInStorage = eventId + "_" + System.currentTimeMillis() + "_" + imageFile.getName(); // Nama unik
        String contentType = "image/jpeg"; // Default, akan mencoba infer

        try {
            contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) {
                String fileExtension = getFileExtension(imageFile.getName());
                if (fileExtension.equalsIgnoreCase("png")) {
                    contentType = "image/png";
                } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
                    contentType = "image/jpeg";
                } else {
                    contentType = "application/octet-stream";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            contentType = "application/octet-stream";
        }

        // Step 1: Upload the image file to Supabase Storage
        RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse(contentType));

        Request uploadRequest = new Request.Builder()
                .url(PROJECT_URL + "/storage/v1/object/" + bucketName + "/" + fileNameInStorage)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", contentType)
                .put(requestBody) // Gunakan PUT untuk mengunggah/menimpa objek
                .build();

        client.newCall(uploadRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Lanjutkan kegagalan ke callback asli
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Step 2: Dapatkan URL publik dari gambar yang diunggah
                    String publicImageUrl = PROJECT_URL + "/storage/v1/object/public/" + bucketName + "/" + fileNameInStorage;

                    // Step 3: Perbarui tabel 'event' dengan URL gambar publik ini
                    JSONObject updateJson = new JSONObject();
                    updateJson.put("banner_url", publicImageUrl); // Simpan URL publik

                    RequestBody updateBody = RequestBody.create(updateJson.toString(), JSON);

                    Request updateEventRequest = new Request.Builder()
                            .url(PROJECT_URL + "/rest/v1/event?id=eq." + eventId)
                            .patch(updateBody)
                            .addHeader("apikey", API_KEY)
                            .addHeader("Authorization", "Bearer " + API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Prefer", "return=representation")
                            .build();

                    client.newCall(updateEventRequest).enqueue(callback); // Lanjutkan sukses/gagal dari update event
                } else {
                    // Jika upload gambar ke storage gagal, lanjutkan respons kegagalan itu
                    callback.onResponse(call, response);
                }
            }
        });
    }

    // 🔹 UPLOAD TIKET
    public static void uploadTiket(String eventId, String jenisEvent, String namaTiket, String harga, String jumlah, Callback callback) {

        System.out.println("Mengirim data tiket ke Supabase: " );

        JSONObject json = new JSONObject();
        json.put("event_id", Integer.parseInt(eventId));
        json.put("jenis_event", jenisEvent);
        json.put("nama_tiket", namaTiket);
        json.put("harga", Integer.parseInt(harga));
        json.put("jumlah", Integer.parseInt(jumlah));

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/tiket")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 GET ALL EVENTS (untuk dashboard, mendukung filter dan pencarian)
    public static void getEvents(String searchTerm, String category, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(PROJECT_URL + "/rest/v1/event")
                .newBuilder();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            urlBuilder.addQueryParameter("or", "(judul.ilike.*" + searchTerm + "*,deskripsi.ilike.*" + searchTerm + "*)");
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("Semua Kategori")) {
            urlBuilder.addQueryParameter("kategori", "eq." + category);
        }

        HttpUrl url = urlBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 GET EVENT BY ID (untuk mengambil detail event spesifik)
    public static void getEventById(String eventId, Callback callback) {
        HttpUrl url = HttpUrl.parse(PROJECT_URL + "/rest/v1/event")
                .newBuilder()
                .addQueryParameter("id", "eq." + eventId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 GET TICKET BY EVENT ID
    public static void getTicketByEventId(String eventId, Callback callback) {
        HttpUrl url = HttpUrl.parse(PROJECT_URL + "/rest/v1/tiket")
                .newBuilder()
                .addQueryParameter("event_id", "eq." + eventId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 SAVE TICKET PURCHASE / TRANSACTION
    public static void saveTicketPurchase(String eventId, String userId, String ticketId, String ticketType,
                                          double ticketPrice, int quantity, String visitorFullName,
                                          String visitorEmail, String visitorPhone,
                                          String paymentMethod, double totalAmount, String qrCodeData,
                                          Callback callback) {
        JSONObject json = new JSONObject();
        json.put("event_id", eventId);
        json.put("buyer_id", userId);
        json.put("ticket_id", ticketId);
        json.put("quantity", quantity);
        json.put("total_amount", totalAmount);
        json.put("payment_method", paymentMethod);
        json.put("qr_code_data", qrCodeData);
        json.put("visitor_fullname", visitorFullName);
        json.put("visitor_email", visitorEmail);
        json.put("visitor_phone", visitorPhone);
        json.put("ticket_type", ticketType);
        json.put("ticket_price", ticketPrice);

        json.put("transaction_type", "Ticket Purchase");
        json.put("payment_status", "Completed");

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/transactions")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 🔹 UPLOAD PROFILE IMAGE TO SUPABASE STORAGE AND UPDATE PROFILE TABLE
    public static void uploadProfileImage(String userId, File imageFile, Callback callback) {
        String bucketName = "profile-pictures";
        String fileNameInStorage = userId + "_" + System.currentTimeMillis() + "_" + imageFile.getName();
        String contentType = "image/jpeg";

        try {
            contentType = Files.probeContentType(imageFile.toPath());
            if (contentType == null) {
                String fileExtension = getFileExtension(imageFile.getName());
                if (fileExtension.equalsIgnoreCase("png")) {
                    contentType = "image/png";
                } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
                    contentType = "image/jpeg";
                } else {
                    contentType = "application/octet-stream";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            contentType = "application/octet-stream";
        }

        RequestBody requestBody = RequestBody.create(imageFile, MediaType.parse(contentType));

        Request uploadRequest = new Request.Builder()
                .url(PROJECT_URL + "/storage/v1/object/" + bucketName + "/" + fileNameInStorage)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", contentType)
                .put(requestBody)
                .build();

        client.newCall(uploadRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String publicImageUrl = PROJECT_URL + "/storage/v1/object/public/" + bucketName + "/" + fileNameInStorage;

                    JSONObject updateJson = new JSONObject();
                    updateJson.put("profile_picture_url", publicImageUrl);

                    RequestBody updateBody = RequestBody.create(updateJson.toString(), JSON);

                    Request updateProfileRequest = new Request.Builder()
                            .url(PROJECT_URL + "/rest/v1/profile?user_id=eq." + userId)
                            .patch(updateBody)
                            .addHeader("apikey", API_KEY)
                            .addHeader("Authorization", "Bearer " + API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Prefer", "return=representation")
                            .build();

                    client.newCall(updateProfileRequest).enqueue(callback);
                } else {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    // 🔹 GET USER DATA (fullname and email) dari tabel 'user'
    public static void getUserData(String userId, Callback callback) {
        HttpUrl url = HttpUrl.parse(PROJECT_URL + "/rest/v1/user")
                .newBuilder()
                .addQueryParameter("id", "eq." + userId)
                .addQueryParameter("select", "fullname,email")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}