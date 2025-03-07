package com.example.perpusdig.ui.profil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileNonAnggotaFragment extends Fragment {

    private EditText txtEmail, txtUsername, txtDeleteAcc;
    private ImageView btnBack, btnEditUsername, btnHapusAcc;
    private Button btnSimpan, btnLogout;

    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profil_nonanggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        txtEmail = view.findViewById(R.id.txt_email);
        txtUsername = view.findViewById(R.id.txt_username);
        txtDeleteAcc = view.findViewById(R.id.txt_hapusemail);
        btnEditUsername = view.findViewById(R.id.btn_edit1);
        btnBack = view.findViewById(R.id.back);
        btnSimpan = view.findViewById(R.id.btn_simpan);
        btnHapusAcc = view.findViewById(R.id.btn_hapus);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Set data dari sharedPreferences
        String email = sharedPreferencesManager.getEmail();
        txtEmail.setText(email);
        txtDeleteAcc.setText(email);
        String username = sharedPreferencesManager.getUsername();
        txtUsername.setText(username);

        // Set onClickListener untuk tombol Edit untuk mengaktifkan EditText yang sesuai
        btnEditUsername.setOnClickListener(v -> enableEditText(txtUsername));
        btnSimpan.setEnabled(false);

        btnBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new ProfilNonAnggotaFragment(),false);
        });

        btnSimpan.setOnClickListener(v -> {
            String usn = txtUsername.getText().toString().trim();

            if (usn.isEmpty()) {
                txtUsername.setError("Username tidak boleh kosong");
            } else if (!Pattern.matches("^[a-zA-Z0-9_]+$", usn)) {
                txtUsername.setError("Username hanya boleh mengandung huruf, angka, atau underscore");
            } else if (usn.length() < 5 || usn.length() > 10) {
                txtUsername.setError("Username harus terdiri dari 5 - 10 karakter");
            } else {
                txtUsername.setError(null);
                updateUser();
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

                        ((MainActivity) getActivity()).showFragment(new LoginFragment(),false);
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
                        hapusUser(idUser);
                        Toast.makeText(getActivity(), "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        return view;
    }

    // Metode update data anggota
    private void updateUser() {
        int id = sharedPreferencesManager.getIdUser();
        String usn = txtUsername.getText().toString();

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> updateData = ardData.ardUpdateDataUser(id, usn);

        updateData.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String usn = txtUsername.getText().toString();
                    sharedPreferencesManager.saveUserData(usn, null, null, null);
                    disableEditText(txtUsername);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk menghapus anggota
    public void hapusUser(Integer idUser) {

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> hapusUser = ardData.ardGetHapusUser(idUser);

        hapusUser.enqueue(new Callback<ResponseModelAnggota>() {
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

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        btnSimpan.setEnabled(true);
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        btnSimpan.setEnabled(false);
    }
}