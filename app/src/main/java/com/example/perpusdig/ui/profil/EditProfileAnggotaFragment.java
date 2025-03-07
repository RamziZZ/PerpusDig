package com.example.perpusdig.ui.profil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.perpusdig.ui.login.LoginFragment;

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

public class EditProfileAnggotaFragment extends Fragment {

    private static final int PICK_IMAGE_FOTO = 1;

    private EditText txtEmail, txtNik, txtUsername, txtNama, txtNoTelp, txtAlamat, txtDeleteAcc;
    private ImageView btnBack, btnEditUsername, btnEditNama, btnEditNoTelp, btnEditAlamat, btnHapusAcc;
    private Button btnSimpan, btnLogout, btnUnggah;
    private ImageView profil;
    private Uri uriFoto;

    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profil_anggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        profil = view.findViewById(R.id.image_profil);
        txtEmail = view.findViewById(R.id.txt_email);
        txtNik = view.findViewById(R.id.txt_nik);
        txtUsername = view.findViewById(R.id.txt_username);
        txtNama = view.findViewById(R.id.txt_nama);
        txtNoTelp = view.findViewById(R.id.txt_notelp);
        txtAlamat = view.findViewById(R.id.txt_alamat);
        txtDeleteAcc = view.findViewById(R.id.txt_hapusemail);
        btnBack = view.findViewById(R.id.back);
        btnSimpan = view.findViewById(R.id.btn_simpan);
        btnUnggah = view.findViewById(R.id.btn_profil);
        btnEditUsername = view.findViewById(R.id.btn_edit1);
        btnEditNama = view.findViewById(R.id.btn_edit2);
        btnEditNoTelp = view.findViewById(R.id.btn_edit3);
        btnEditAlamat = view.findViewById(R.id.btn_edit4);
        btnHapusAcc = view.findViewById(R.id.btn_hapus);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Set data dari sharedPreferences
        String fotoAnggota = sharedPreferencesManager.getFotoAnggota();
        if (fotoAnggota != null && !fotoAnggota.isEmpty()) {
            byte[] decodedString = Base64.decode(fotoAnggota, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profil.setImageBitmap(decodedByte);
        }
        String email = sharedPreferencesManager.getEmail();
        txtEmail.setText(email);
        txtDeleteAcc.setText(email);
        String nik = sharedPreferencesManager.getNikAnggota();
        txtNik.setText(nik);
        String username = sharedPreferencesManager.getUsername();
        txtUsername.setText(username);
        String namaAnggota = sharedPreferencesManager.getNamaAnggota();
        txtNama.setText(namaAnggota);
        String telpAnggota = sharedPreferencesManager.getTelpAnggota();
        txtNoTelp.setText(telpAnggota);
        String alamatAnggota = sharedPreferencesManager.getAlamatAnggota();
        txtAlamat.setText(alamatAnggota);

        // Set onClickListener untuk button
        btnUnggah.setOnClickListener(v -> selectImage(PICK_IMAGE_FOTO));
        btnEditUsername.setOnClickListener(v -> enableEditText(txtUsername));
        btnEditNama.setOnClickListener(v -> enableEditText(txtNama));
        btnEditNoTelp.setOnClickListener(v -> enableEditText(txtNoTelp));
        btnEditAlamat.setOnClickListener(v -> enableEditText(txtAlamat));

        // Nonaktifkan tombol simpan di awal
        btnSimpan.setEnabled(false);

        // Menambahkan animasi pada tombol back
        btnBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new ProfilAnggotaFragment(), false);
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    updateAnggota();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Keluar Akun");
                builder.setMessage("Apakah Anda yakin ingin keluar dari akun?");
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Hapus semua data di SharedPreferences
                        sharedPreferencesManager.clearAllData();

                        ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
                        Toast.makeText(getActivity(), "Akun berhasil logout", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        btnHapusAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Hapus Akun");
                builder.setMessage("Apakah Anda yakin ingin menghapus akun ini secara permanen?");
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int idUser = sharedPreferencesManager.getIdUser(); // Mendapatkan ID user yang disimpan
                        hapusAnggota(idUser);
                        Toast.makeText(getActivity(), "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        return view;
    }

    // Metode untuk menghapus anggota
    public void hapusAnggota(Integer idUser) {

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> hapusAnggota = ardData.ardGetHapusUser(idUser);

        hapusAnggota.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int kode = response.body().getKode();

                    if (kode == 1) {
                        // Hapus semua data di SharedPreferences
                        sharedPreferencesManager.clearAllData();
                        ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode update data anggota
    private void updateAnggota() {
        int id = sharedPreferencesManager.getIdUser(); // Ambil ID pengguna
        String usn = txtUsername.getText().toString();
        String nama = txtNama.getText().toString();
        String telp = txtNoTelp.getText().toString();
        String alamat = txtAlamat.getText().toString();

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> updateData = ardData.ardUpdateDataAnggota(id, usn, nama, telp, alamat);

        updateData.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String usn = txtUsername.getText().toString();
                    String nama = txtNama.getText().toString();
                    String telp = txtNoTelp.getText().toString();
                    String alamat = txtAlamat.getText().toString();
                    sharedPreferencesManager.saveUserData(usn, nama, telp, alamat);

                    disableEditText(txtUsername, txtNama, txtNoTelp, txtAlamat);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode update profil anggota
    private void updateProfil() {
        // Ambil idUser dari SharedPreferences
        int id = sharedPreferencesManager.getIdUser();
        RequestBody idUser = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(id));

        // Mengonversi file KTP dan foto anggota menjadi byte array
        byte[] profilBytes = getFileDataFromUri(uriFoto);

        // Membuat RequestBody dari byte array
        RequestBody profilRb = RequestBody.create(MediaType.parse("image/*"), profilBytes);
        MultipartBody.Part profilAnggota = MultipartBody.Part.createFormData("foto_anggota", "anggota.jpg", profilRb);

        // Membuat objek API dan mengirim data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> updateData = ardData.ardUpdateProfil(idUser, profilAnggota);

        updateData.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sharedPreferencesManager.saveProfil(profilBytes);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server | "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_FOTO) {
            if (data != null) {
                uriFoto = data.getData();
                if (uriFoto != null) {  // Pastikan uriFoto tidak null
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriFoto);
                        profil.setImageBitmap(bitmap);  // Menampilkan gambar
                        updateProfil();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("onActivityResult", "URI Foto null");
                }
            } else {
                Log.e("onActivityResult", "Intent data is null");
            }
        }
    }

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

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    // Metode untuk validasi input dari pengguna
    private boolean validateInputs() {
        boolean valid = true;
        String username = txtUsername.getText().toString().trim();

        if (username.isEmpty()) {
            txtUsername.setError("Username tidak boleh kosong");
            valid = false;
        } else if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
            txtUsername.setError("Username hanya boleh mengandung huruf, angka, atau underscore");
            valid = false;
        } else if (username.length() < 5 || username.length() > 10) {
            txtUsername.setError("Username harus terdiri dari 5 - 10 karakter");
            valid = false;
        }

        if (txtNama.getText().toString().isEmpty()) {
            txtNama.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("^[A-Za-z' ]+$", txtNama.getText().toString())) {
            txtNama.setError("Format nama tidak sesuai");
            valid = false;
        }

        if (txtNoTelp.getText().toString().isEmpty()) {
            txtNoTelp.setError("Tidak boleh kosong");
            valid = false;
        }
        if (!Pattern.matches("\\d{11,13}", txtNoTelp.getText().toString())) {
            txtNoTelp.setError("Nomor telepon harus berupa angka 11-13 digit");
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

        return valid;  // Mengembalikan status validasi
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        btnSimpan.setEnabled(true);
    }

    private void disableEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setEnabled(false);
        }
        btnSimpan.setEnabled(false);
    }
}