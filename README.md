# SapataTix Desktop

Aplikasi desktop untuk manajemen event dan penjualan tiket, dibangun menggunakan JavaFX dan terintegrasi dengan Supabase sebagai backend.

## Fitur Utama

* **Autentikasi Pengguna:**
    * Registrasi akun baru.
    * Login pengguna yang sudah terdaftar.
* **Manajemen Profil:**
    * Melihat dan memperbarui informasi detail profil pengguna (nama, telepon, alamat, kota).
    * Mengunggah dan menampilkan foto profil pengguna.
* **Manajemen Event:**
    * Membuat event baru dengan detail lengkap (judul, kategori, tempat, deskripsi, host, sesi, tanggal, waktu).
    * Mengunggah banner kustom untuk setiap event.
    * Menentukan jenis tiket (berbayar/gratis) dan mengelola harga serta jumlah tiket.
* **Pencarian & Detail Event:**
    * Menjelajahi daftar event yang tersedia.
    * Melihat detail lengkap setiap event (tanggal, waktu, tempat, deskripsi, host).
* **Pembelian Tiket:**
    * Memilih jenis dan jumlah tiket untuk event.
    * Mengisi detail pengunjung (nama lengkap, email, nomor telepon).
    * Memilih metode pembayaran (misalnya DANA, GoPay).
    * Melihat ringkasan pesanan (subtotal, pajak, total).
    * Konfirmasi pembayaran dan menampilkan informasi QR Code (simulasi).
* **Riwayat Transaksi:**
    * Melihat daftar riwayat pembelian tiket atau event yang dibuat.

## Teknologi yang Digunakan

* **Frontend:** JavaFX (untuk antarmuka pengguna desktop)
* **Backend:** Supabase (sebagai Backend-as-a-Service)
    * **PostgreSQL Database:** Untuk menyimpan data pengguna, profil, event, tiket, dan transaksi.
    * **Supabase Auth:** Untuk autentikasi pengguna.
    * **Supabase Storage:** Untuk menyimpan file gambar seperti banner event dan foto profil.
* **Konektivitas API:** OkHttp3 (untuk komunikasi HTTP dengan Supabase API)
* **JSON Parsing:** `org.json` (untuk memproses data JSON dari dan ke Supabase)
* **Manajemen Proyek:** Apache Maven

## Cara Menjalankan Aplikasi

Ikuti langkah-langkah di bawah ini untuk mengatur dan menjalankan aplikasi SapataTix Desktop di lingkungan lokal Anda.

### Prasyarat

* **Java Development Kit (JDK):** Versi 23 atau yang lebih baru.
* **Apache Maven:** Versi 3.8.5 atau yang lebih baru.
* **IDE:** IntelliJ IDEA (disarankan) atau IDE lain yang mendukung proyek Maven dan JavaFX.
* **Akun Supabase:** Proyek Supabase yang sudah aktif dengan konfigurasi database yang sesuai.

### Konfigurasi Supabase

1.  **Buat Proyek Supabase:** Jika belum punya, buat proyek baru di [Supabase Dashboard](https://supabase.com/dashboard).
2.  **Konfigurasi Tabel Database:** Pastikan Anda memiliki tabel-tabel berikut dengan skema yang benar (seperti yang telah dibahas sebelumnya):
    * `user` (untuk data registrasi/login, dengan kolom `id`, `email`, `fullname`, `password`)
    * `profile` (untuk detail profil pengguna, dengan kolom `user_id` (foreign key ke `user.id`), `nama_depan`, `nama_belakang`, `telepon`, `alamat`, `kota`, `profile_picture_url`)
    * `event` (untuk detail event, dengan kolom `id`, `user_id` (foreign key ke `user.id`), `judul`, `kategori`, `tempat`, `deskripsi`, `nama_host`, `no_hp_host`, `sesi`, `tanggal_mulai`, `waktu_mulai`, `waktu_berakhir`, `jenis_event`, `banner_url`)
    * `tiket` (untuk detail tiket event, dengan kolom `id`, `event_id` (foreign key ke `event.id`), `jenis_event`, `nama_tiket`, `harga`, `jumlah`)
    * `transactions` (untuk catatan transaksi, dengan kolom `transaction_id`, `created_at`, `buyer_id`, `event_id`, `ticket_id`, `quantity`, `total_amount`, `payment_method`, `payment_status`, `transaction_type`, `qr_code_data`, `visitor_fullname`, `visitor_email`, `visitor_phone`, `ticket_type`, `ticket_price`)
3.  **Konfigurasi Supabase Storage:**
    * Buat bucket baru bernama `profile-pictures` (untuk foto profil) dan bucket lain jika Anda mengelola banner event secara terpisah (misalnya `event-banners`).
    * Atur **Kebijakan RLS (Row Level Security)** yang sesuai untuk setiap bucket dan tabel:
        * Untuk bucket `profile-pictures`: Kebijakan `INSERT` (untuk upload oleh pengguna terautentikasi) dan `SELECT` (untuk melihat oleh pengguna yang sesuai, bisa publik atau terautentikasi).
        * Untuk tabel database: Kebijakan `SELECT`, `INSERT`, `UPDATE` yang sesuai untuk setiap tabel agar aplikasi dapat berinteraksi dengan data.
4.  **Dapatkan Kredensial API:**
    * Dari Supabase Dashboard Anda, pergi ke **Project Settings -> API**.
    * Salin **`Project URL`** dan **`Public Anon Key`**.

### Pengaturan Proyek

1.  **Clone Repositori:**
    ```bash
    git clone [URL_REPOSITORI_ANDA]
    cd SapataTix-Dekstop
    ```
2.  **Buka di IDE:** Buka folder proyek `SapataTix-Dekstop` di IntelliJ IDEA atau IDE pilihan Anda.
3.  **Perbarui Kredensial Supabase:**
    * Buka file `src/main/java/com/example/sapatatix/service/SupabaseService.java`.
    * Ganti placeholder `PROJECT_URL` dan `API_KEY` dengan kredensial yang Anda dapatkan dari Supabase Dashboard.
        ```java
        private static final String PROJECT_URL = "URL_PROYEK_SUPABASE_ANDA";
        private static final String API_KEY = "PUBLIC_ANON_KEY_SUPABASE_ANDA";
        ```
    * Pastikan nama bucket `profile-pictures` juga sudah benar di file ini.

### Menjalankan Aplikasi

1.  **Unduh Dependensi:** IDE Anda (misalnya IntelliJ IDEA) seharusnya secara otomatis mengunduh semua dependensi Maven yang diperlukan. Jika tidak, Anda bisa menjalankan:
    ```bash
    mvn clean install
    ```
2.  **Jalankan dari IDE:**
    * Temukan file `src/main/java/com/example/sapatatix/Main.java`.
    * Klik kanan pada file tersebut dan pilih `Run 'Main.main()'`.
3.  **Jalankan dengan Maven (dari Terminal):**
    ```bash
    mvn clean javafx:run
    ```

Aplikasi seharusnya akan terbuka, menampilkan halaman beranda, dan Anda bisa mulai melakukan registrasi, login, dan menjelajahi fitur-fiturnya.

---

## Video Presentasi YouTube

**[Tonton Video Presentasi di YouTube](https://youtu.be/cxYkvqAKkbo)**

---
