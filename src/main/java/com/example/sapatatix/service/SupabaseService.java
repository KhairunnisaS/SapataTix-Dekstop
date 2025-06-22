package com.example.sapatatix.service;

import okhttp3.*;

import java.io.IOException;

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
    // 🔹 CREATE TRANSACTION
    public static void createTransaction(
            Long buyerId,
            Long sellerId,
            Long quantity,
            Double totalAmount,
            Long ticketId,
            String transactionType,
            String paymentMethod,
            String paymentStatus,
            String paymentReference,
            Callback callback
    ) {
        String json = "{"
                + "\"buyer_id\":" + buyerId + ","
                + "\"seller_id\":" + sellerId + ","
                + "\"quantity\":" + quantity + ","
                + "\"total_amount\":" + totalAmount + ","
                + "\"ticket_id\":" + ticketId + ","
                + "\"transaction_type\":\"" + transactionType + "\","
                + "\"payment_methode\":\"" + paymentMethod + "\","
                + "\"payment_status\":\"" + paymentStatus + "\","
                + "\"payment_reference\":\"" + paymentReference + "\""
                + "}";

        Request request = new Request.Builder()
                .url(PROJECT_URL + "/rest/v1/transactions")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(callback);
    }

}
