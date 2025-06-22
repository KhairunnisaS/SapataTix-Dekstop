package com.example.sapatatix.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.io.File;

public class SupabaseService {
    private static final String PROJECT_URL = "https://mcqhhdeqkuklvxglpycb.supabase.co"; // GANTI: Sesuaikan dengan URL project Supabase kamu
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1jcWhoZGVxa3VrbHZ4Z2xweWNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA0NDM3NjUsImV4cCI6MjA2NjAxOTc2NX0.BpXkt9b6FXT6lfhShUX8f2BCL7_M_iqwYsiWsEe9nf8"; // GANTI: Public API Key dari Project Settings > API

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // 🔹 REGISTER USER (dengan fullname)
    public static void register(String fullname, String email, String password, Callback callback) {
        String json = "{"
                + "\"fullname\":\"" + fullname + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\""
                + "}";

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/user") // endpoint Supabase REST
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
                .method("PATCH", RequestBody.create(json, JSON)) // Gunakan PATCH untuk update sebagian
                .build();

        client.newCall(request).enqueue(callback);
    }

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

        // Pastikan userId adalah integer, bukan string kosong
        if (userId != null && !userId.isEmpty()) {
            json.put("user_id", Integer.parseInt(userId));
        } else {
            System.out.println("❗ userId kosong atau null!");
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/event")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation") // HARUS ADA supaya respons JSON-nya termasuk 'id'
                .post(body) // ⬅⬅⬅ INI yang kurang di kodingan kamu sebelumnya
                .build();

        client.newCall(request).enqueue(callback);

        System.out.println("SELESAI kirim ke Supabase (asynchronous)");
    }

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


    public static void uploadBanner(String eventId, File imageFile, Callback callback) {
        JSONObject json = new JSONObject();
        json.put("banner_url", imageFile.getName()); // atau imageFile.getAbsolutePath() jika kamu ingin path penuh

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/event?id=eq." + eventId) // ← PENTING: harus ada /rest/v1/
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(callback);
    }


}
