package com.example.perpusdig;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharedPreferencess {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "user_preferences";
    private static final String IS_LOGGED_IN = "is_logged_in";

    private static final String ID_USER_KEY = "id_user";
    private static final String EMAIL_KEY = "email_user";
    private static final String USERNAME_KEY = "username";
    private static final String NIK_ANGGOTA_KEY = "nik_anggota";
    private static final String NAMA_ANGGOTA_KEY = "nama_anggota";
    private static final String TELP_ANGGOTA_KEY = "telp";
    private static final String ALAMAT_ANGGOTA_KEY = "alamat";
    private static final String FOTO_ANGGOTA_KEY = "foto_anggota";
    private static final String STATUS_ANGGOTA_KEY = "status_verifikasi";
    private static final String TGL_ANGGOTA_KEY = "tgl_pendaftaran";

    public SharedPreferencess(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Simpan status login
    public void saveLoginStatus(boolean status) {
        editor.putBoolean(IS_LOGGED_IN, status);
        editor.apply();
    }

    // Ambil status login
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    // Hapus semua data di SharedPreferences
    public void clearAllData() {
        editor.clear(); // Menghapus semua data
        editor.apply(); // Menyimpan perubahan
    }

    public void saveUserData(String username, String nama, String telp, String alamat) {
        editor.putString(USERNAME_KEY, username);
        editor.putString(NAMA_ANGGOTA_KEY, nama);
        editor.putString(TELP_ANGGOTA_KEY, telp);
        editor.putString(ALAMAT_ANGGOTA_KEY, alamat);
        editor.apply(); // Simpan semua perubahan
    }

    public void saveProfil(byte[] fotoAnggota) {
        // Ubah byte array menjadi string Base64
        String encodedImage = Base64.encodeToString(fotoAnggota, Base64.DEFAULT);
        // Simpan string Base64 ke SharedPreferences
        editor.putString(FOTO_ANGGOTA_KEY, encodedImage);
        editor.apply(); // Simpan semua perubahan
    }

    // Simpan ID User dan NIK Anggota
    public void saveIdUser(int id_user) {
        editor.putInt(ID_USER_KEY, id_user);
        editor.apply();
    }
    public int getIdUser() {
        return sharedPreferences.getInt(ID_USER_KEY, -1);
    }

    // Simpan email pengguna
    public void saveEmail(String email_user) {
        editor.putString(EMAIL_KEY, email_user);
        editor.apply();
    }
    public String getEmail() {
        return sharedPreferences.getString(EMAIL_KEY, "");
    }

    // Simpan username pengguna
    public void saveUsername(String username) {
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }
    public String getUsername() {
        return sharedPreferences.getString(USERNAME_KEY, "");
    }

    public void saveNikAnggota(String nik_anggota) {
        editor.putString(NIK_ANGGOTA_KEY, nik_anggota);
        editor.apply();
    }
    public String getNikAnggota() {
        return sharedPreferences.getString(NIK_ANGGOTA_KEY, "");
    }

    // Simpan nama anggota
    public void saveNamaAnggota(String nama_anggota) {
        editor.putString(NAMA_ANGGOTA_KEY, nama_anggota);
        editor.apply();
    }
    public String getNamaAnggota() {
        return sharedPreferences.getString(NAMA_ANGGOTA_KEY, "");
    }

    // Simpan telepon anggota
    public void saveTelpAnggota(String telp_anggota) {
        editor.putString(TELP_ANGGOTA_KEY, telp_anggota);
        editor.apply();
    }
    public String getTelpAnggota() {
        return sharedPreferences.getString(TELP_ANGGOTA_KEY, "");
    }

    // Simpan alamat anggota
    public void saveAlamatAnggota(String alamat_anggota) {
        editor.putString(ALAMAT_ANGGOTA_KEY, alamat_anggota);
        editor.apply();
    }
    public String getAlamatAnggota() {
        return sharedPreferences.getString(ALAMAT_ANGGOTA_KEY, "");
    }

    // Simpan foto anggota
    public void saveFotoAnggota(String foto_anggota) {
        editor.putString(FOTO_ANGGOTA_KEY, foto_anggota);
        editor.apply();
    }
    public String getFotoAnggota() {
        return sharedPreferences.getString(FOTO_ANGGOTA_KEY, "");
    }

    // Simpan status verifikasi anggota
    public void saveStatusAnggota(String status_verifikasi) {
        editor.putString(STATUS_ANGGOTA_KEY, status_verifikasi);
        editor.apply();
    }
    public String getStatusAnggota() {
        return sharedPreferences.getString(STATUS_ANGGOTA_KEY, "");
    }

    // Simpan tanggal pendaftaran anggota
    public void saveTglAnggota(String tgl_pendaftaran) {
        try {
            if (tgl_pendaftaran == null || tgl_pendaftaran.isEmpty()) {
                editor.putString(TGL_ANGGOTA_KEY, tgl_pendaftaran);
            } else {
                // Format tanggal dari API (format asal)
                SimpleDateFormat sdfAsal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                // Format tanggal yang diinginkan
                SimpleDateFormat sdfTujuan = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                // Parsing tanggal dari format asal ke objek Date
                Date tanggal = sdfAsal.parse(tgl_pendaftaran);
                // Format ulang ke format tujuan
                String tglFormatted = sdfTujuan.format(tanggal);
                editor.putString(TGL_ANGGOTA_KEY, tglFormatted);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Jika terjadi error saat parsing, simpan tanggal asli
            editor.putString(TGL_ANGGOTA_KEY, tgl_pendaftaran);
        }
        editor.apply();
    }

    public String getTglAnggota() {
        return sharedPreferences.getString(TGL_ANGGOTA_KEY, "");
    }
}