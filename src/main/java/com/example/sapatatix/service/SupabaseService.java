package com.example.sapatatix.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
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
        String fileName = "banner_" + eventId + "_" + System.currentTimeMillis() + ".jpg";

        uploadImageToStorage(fileName, imageFile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onResponse(call, response);
                    return;
                }

                // Setelah upload ke storage berhasil, simpan path di tabel event
                JSONObject json = new JSONObject();
                json.put("banner_url", fileName); // hanya simpan nama file

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json")
                );

                Request patchRequest = new Request.Builder()
                        .url(PROJECT_URL + "/rest/v1/event?id=eq." + eventId)
                        .patch(body)
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .build();

                client.newCall(patchRequest).enqueue(callback);
            }
        });
    }

    public static void uploadImageToStorage(String fileName, File imageFile, Callback callback) {
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody fileBody = RequestBody.create(imageFile, mediaType);

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/storage/v1/object/event-banner/" + fileName) // ← bucket 'event-banner'
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "image/*")
                .put(fileBody)
                .build();

        client.newCall(request).enqueue(callback);
    }


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
                .url(PROJECT_URL + "/rest/v1/tiket?event_id=eq." + eventId)// pastikan endpoint ini sesuai dengan Supabase kamu
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getEventById(String eventId, Callback callback) {
        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/event?id=eq." + eventId)
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getTiketByEventId(String eventId, Callback callback) {
        String url = PROJECT_URL + "/rest/v1/tiket?event_id=eq." + eventId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }




}
