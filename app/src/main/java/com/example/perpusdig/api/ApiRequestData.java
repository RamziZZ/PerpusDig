package com.example.perpusdig.api;

import com.example.perpusdig.model.LoginRequest;
import com.example.perpusdig.model.LupaPasswordRequest;
import com.example.perpusdig.model.RegisterRequest;
import com.example.perpusdig.model.ResponseLupaPassword;
import com.example.perpusdig.model.ResponseModelAnggota;
import com.example.perpusdig.model.ResponseModelPinjamBuku;
import com.example.perpusdig.model.ResponseModelPinjamEbook;
import com.example.perpusdig.model.ResponseLogin;
import com.example.perpusdig.model.ResponseModelBuku;
import com.example.perpusdig.model.ResponseModelEbook;
import com.example.perpusdig.model.ResponseRegister;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiRequestData {

    // Endpoint untuk login
    @POST("login.php")
    Call<ResponseLogin> loginUser(@Body LoginRequest loginRequest);

    // Endpoint untuk registrasi
    @POST("regis_akun.php")
    Call<ResponseRegister> register(@Body RegisterRequest request);

    // Endpoint untuk cek email
    @POST("cek_email.php")
    Call<ResponseLupaPassword> cekEmail(@Body LupaPasswordRequest request);

    // Endpoint untuk reset password
    @POST("lupa_password.php")
    Call<ResponseLupaPassword> lupapassword(@Body LupaPasswordRequest request);

    // Endpoint untuk mendapatkan daftar e book
    @GET("ebook.php")
    Call<ResponseModelEbook> ardGetDataEbook();

    // Endpoint untuk mendapatkan detail buku berdasarkan ID
    @GET("ebook_detail.php")
    Call<ResponseModelEbook> ardGetDetailEbook(@Query("id") String idBook);

    // Endpoint untuk mendapatkan daftar e book
    @GET("ebook_trending.php")
    Call<ResponseModelEbook> ardGetTrendingEbook();

    // Endpoint untuk mendapatkan riwayat ebook berdasarkan id_user
    @GET("riwayat_ebook.php")
    Call<ResponseModelPinjamEbook> ardGetRiwayatEbook(@Query("id_user") Integer idUser);

    // Endpoint untuk menyimpan peminjaman ebook
    @FormUrlEncoded
    @POST("pinjam_ebook.php")
    Call<ResponseModelPinjamEbook> ardCreatePinjamEbook(
            @Field("judul") String judul,
            @Field("kategori") String kategori,
            @Field("status_peminjaman") String status_peminjaman,
            @Field("id_user") Integer id_user,
            @Field("id_ebook") Integer id_ebook
    );

    // Endpoint untuk mendapatkan daftar buku
    @GET("buku.php")
    Call<ResponseModelBuku> ardGetDataBuku();

    // Endpoint untuk mendapatkan detail buku berdasarkan ID
    @GET("buku_detail.php")
    Call<ResponseModelBuku> ardGetDetailBuku(@Query("id") String idBuku);

    // Endpoint untuk mendapatkan riwayat buku berdasarkan id_user
    @GET("riwayat_buku.php")
    Call<ResponseModelPinjamBuku> ardGetRiwayatBuku(@Query("id_user") Integer idUser);

    // Endpoint untuk menghapus riwayat buku berdasarkan id_peminjaman
    @GET("riwayat_delete.php")
    Call<ResponseModelPinjamBuku> ardGetHapusRiwayat(@Query("id_peminjaman") String idPeminjaman);

    // Endpoint untuk menyimpan peminjaman buku
    @FormUrlEncoded
    @POST("pinjam_buku.php")
    Call<ResponseModelPinjamBuku> ardCreatePinjamBuku(
            @Field("judul") String judul,
            @Field("kategori") String kategori,
            @Field("status_peminjaman") String status_peminjaman,
            @Field("id_user") Integer id_user,
            @Field("id_buku") Integer id_buku,
            @Field("nik_anggota") String nik_anggota
    );

    // Endpoint untuk mendapatkan data anggota berdasarkan id_user
    @GET("anggota.php")
    Call<ResponseModelAnggota> ardGetDataAnggota(@Query("id_user") Integer idUser);

    // Endpoint untuk menyimpan data anggota
    @Multipart
    @POST("anggota_simpan.php")
    Call<ResponseModelAnggota> ardCreateDataAnggota(
            @Part("nik_anggota") RequestBody nik_anggota,
            @Part("nama_anggota") RequestBody nama_anggota,
            @Part("telp") RequestBody telp,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part foto_ktp,  // Foto KTP sebagai Multipart file
            @Part MultipartBody.Part foto_anggota,  // Foto Anggota sebagai Multipart file
            @Part("status_verifikasi") RequestBody status_verifikasi,
            @Part("id_user") RequestBody id_user
    );

    // Endpoint untuk update data anggota
    @FormUrlEncoded
    @POST("anggota_update.php")
    Call<ResponseModelAnggota> ardUpdateDataAnggota(
            @Field("id_user") Integer id_user,
            @Field("username") String username,
            @Field("nama_anggota") String nama_anggota,
            @Field("telp") String telp,
            @Field("alamat") String alamat
    );

    // Endpoint untuk update profil anggota
    @Multipart
    @POST("anggota_upfoto.php")
    Call<ResponseModelAnggota> ardUpdateProfil(
            @Part("id_user") RequestBody id_user,
            @Part MultipartBody.Part foto_anggota
    );

    // Endpoint untuk menghapus data user
    @GET("user_delete.php")
    Call<ResponseModelAnggota> ardGetHapusUser(@Query("id_user") Integer id_user);

    // Endpoint untuk update data user
    @FormUrlEncoded
    @POST("user_update.php")
    Call<ResponseModelAnggota> ardUpdateDataUser(
            @Field("id_user") Integer id_user,
            @Field("username") String username
    );
}
