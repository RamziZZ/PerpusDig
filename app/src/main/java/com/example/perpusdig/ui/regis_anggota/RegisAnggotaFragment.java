package com.example.perpusdig.ui.regis_anggota;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.ResponseModelAnggota;
import com.example.perpusdig.ui.profil.ProfilAnggotaFragment;
import com.example.perpusdig.ui.profil.ProfilNonAnggotaFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisAnggotaFragment extends Fragment {

    private static final int PICK_IMAGE_KTP = 1;
    private static final int PICK_IMAGE_FOTO = 2;
    private static final int MAX_FILE_SIZE_MB = 3; // Batas ukuran file dalam MB
    private static final String[] ACCEPTED_FORMATS = { "jpg", "jpeg" }; // Ekstensi gambar yang diperbolehkan

    private ImageView btnBack;
    private EditText txtNIK, txtNama, txtTelp, txtAlamat;
    private Button btnBatal, fileKtp, fileFoto, btnUpload;
    private TextView statusKtp, statusFoto;
    private Uri uriKtp, uriFoto;
    private String nik, nama, telp, alamat;

    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.regis_anggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        btnBack = view.findViewById(R.id.back);
        txtNIK = view.findViewById(R.id.txt_nik);
        txtNama = view.findViewById(R.id.txt_nama);
        txtTelp = view.findViewById(R.id.txt_telp);
        txtAlamat = view.findViewById(R.id.txt_alamat);
        btnBatal = view.findViewById(R.id.btn_batal);
        fileKtp = view.findViewById(R.id.btn_pilihfile1);
        fileFoto = view.findViewById(R.id.btn_pilihfile2);
        statusKtp = view.findViewById(R.id.info1);
        statusFoto = view.findViewById(R.id.info2);
        btnUpload = view.findViewById(R.id.btn_upload);

        fileKtp.setOnClickListener(v -> selectImage(PICK_IMAGE_KTP));
        fileFoto.setOnClickListener(v -> selectImage(PICK_IMAGE_FOTO));

        btnBack.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnBack.startAnimation(anim);
            ((MainActivity) getActivity()).showFragment(new ProfilNonAnggotaFragment(),false);
        });

        btnBatal.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnBatal.startAnimation(anim);
            ((MainActivity) getActivity()).showFragment(new ProfilNonAnggotaFragment(),false);
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nik = txtNIK.getText().toString();
                nama = txtNama.getText().toString();
                telp = txtTelp.getText().toString();
                alamat = txtAlamat.getText().toString();

                // Validasi input sebelum mengunggah
                if (validateInputs()) {
                    createData();
                }
            }
        });

        return view;
    }

    // Methode simpan data anggota
    private void createData() {
        int idUser = sharedPreferencesManager.getIdUser();

        RequestBody idRb = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUser));
        RequestBody nikRb = RequestBody.create(MediaType.parse("text/plain"), nik);
        RequestBody namaRb = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody telpRb = RequestBody.create(MediaType.parse("text/plain"), telp);
        RequestBody alamatRb = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody statusVerifikasiRb = RequestBody.create(MediaType.parse("text/plain"), "Ditunda");

        // Mengonversi file KTP dan foto anggota menjadi byte array
        byte[] fotoKtpBytes = getFileDataFromUri(uriKtp);
        byte[] fotoAnggotaBytes = getFileDataFromUri(uriFoto);

        // Membuat RequestBody dari byte array
        RequestBody fotoKtpRequestBody = RequestBody.create(MediaType.parse("image/*"), fotoKtpBytes);
        MultipartBody.Part fotoKtpPart = MultipartBody.Part.createFormData("foto_ktp", "ktp.jpg", fotoKtpRequestBody);

        RequestBody fotoAnggotaRequestBody = RequestBody.create(MediaType.parse("image/*"), fotoAnggotaBytes);
        MultipartBody.Part fotoAnggotaPart = MultipartBody.Part.createFormData("foto_anggota", "anggota.jpg", fotoAnggotaRequestBody);

        // Membuat objek API dan mengirim data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> simpanData = ardData.ardCreateDataAnggota
                (nikRb, namaRb, telpRb, alamatRb, fotoKtpPart, fotoAnggotaPart, statusVerifikasiRb, idRb);

        simpanData.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getPesan();

                    if ("NIK anggota sudah digunakan".equalsIgnoreCase(pesan)) {
                        // Tampilkan pesan toast dan hentikan eksekusi lebih lanjut
                        Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        return; // Stop execution
                    }

                    // Jika tidak ada masalah, lanjutkan eksekusi
                    Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                    sharedPreferencesManager.saveStatusAnggota("Ditunda");
                    ((MainActivity) getActivity()).showFragment(new WaitingRegisFragment(), false);
                } else {
                    Toast.makeText(getActivity(), "Gagal melakukan peminjaman", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server | " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mengonversi file menjadi byte array
    private byte[] getFileDataFromUri(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metode untuk membuka file manager agar pengguna dapat memilih file gambar
    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");  // Hanya file gambar yang dapat dipilih
        startActivityForResult(intent, requestCode);
    }

    // Metode untuk mengecek format file
    private boolean isFileFormatValid(String fileExtension) {
        for (String format : ACCEPTED_FORMATS) {
            if (format.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    // Metode untuk mendapatkan format file dari Uri
    private String getFileExtension(Uri uri) {
        String extension = "";
        if (getActivity() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                String fileName = cursor.getString(nameIndex);
                cursor.close();

                if (fileName.contains(".")) {
                    extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                }
            }
        }
        return extension;
    }

    // Metode untuk mengecek ukuran file
    private boolean isFileSizeValid(Uri uri) {
        if (getActivity() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                long fileSizeInBytes = cursor.getLong(sizeIndex);  // Dapatkan ukuran file dalam byte
                cursor.close();

                // Mengubah ukuran file ke MB dan membandingkannya dengan batas maksimum
                double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
                return fileSizeInMB <= MAX_FILE_SIZE_MB;
            }
        }
        return false;
    }

    // Metode untuk mendapatkan nama file dari Uri
    private String getFileName(Uri uri) {
        String fileName = "";
        if (getActivity() != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(nameIndex); // Ambil nama file
                cursor.close();
            }
        }
        return fileName;
    }

    // Menangani hasil pemilihan file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String fileExtension = getFileExtension(selectedImageUri);
            String fileName = getFileName(selectedImageUri);  // Dapatkan nama file

            // Validasi ukuran file dan format file
            if (isFileSizeValid(selectedImageUri) && isFileFormatValid(fileExtension)) {
                if (requestCode == PICK_IMAGE_KTP) {
                    uriKtp = selectedImageUri;
                    statusKtp.setText(fileName);  // Tampilkan nama file jika valid
                    statusKtp.setError(null);     // Pastikan tidak ada error
                } else if (requestCode == PICK_IMAGE_FOTO) {
                    uriFoto = selectedImageUri;
                    statusFoto.setText(fileName); // Tampilkan nama file jika valid
                    statusFoto.setError(null);    // Pastikan tidak ada error
                }
            } else {
                String errorMsg = isFileSizeValid(selectedImageUri) ? "Format file tidak valid (Hanya JPG/JPEG)" : "Ukuran file terlalu besar (maks 3MB)";
                if (requestCode == PICK_IMAGE_KTP) {
                    statusKtp.setError(errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    uriKtp = null;
                } else if (requestCode == PICK_IMAGE_FOTO) {
                    statusFoto.setError(errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    uriFoto = null;
                }
            }
        }
    }

    // Metode untuk validasi input dari pengguna
    private boolean validateInputs() {
        boolean valid = true;

        if (txtNIK.getText().toString().isEmpty()) {
            txtNIK.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("\\d{16}", txtNIK.getText().toString())) {
            txtNIK.setError("NIK harus berupa angka 16 digit");
            valid = false;
        }
        if (txtNama.getText().toString().isEmpty()) {
            txtNama.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("^[A-Za-z' ]+$", txtNama.getText().toString())) {
            txtNama.setError("Nama hanya boleh berisi huruf dan karakter '");
            valid = false;
        }
        if (txtTelp.getText().toString().isEmpty()) {
            txtTelp.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("\\d{11,13}", txtTelp.getText().toString())) {
            txtTelp.setError("Nomor telepon harus berupa angka 11-13 digit");
            valid = false;
        }
        if (txtAlamat.getText().toString().isEmpty()) {
            txtAlamat.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("^[A-Za-z0-9 ,.-]+$", txtAlamat.getText().toString())) {
            txtAlamat.setError("Format alamat tidak sesuai");
            valid = false;
        }

        // Validasi file KTP
        if (uriKtp == null) {
            statusKtp.setError("");
            Toast.makeText(getActivity(), "File tidak boleh kosong", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        // Validasi file Foto
        if (uriFoto == null) {
            statusFoto.setError("");
            Toast.makeText(getActivity(), "File tidak boleh kosong", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}