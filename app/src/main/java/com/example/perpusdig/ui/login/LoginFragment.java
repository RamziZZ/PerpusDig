package com.example.perpusdig.ui.login;

import android.text.method.PasswordTransformationMethod;
import android.os.Bundle;
import android.util.Log;
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

import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.MainActivity;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.LoginRequest;
import com.example.perpusdig.model.ResponseLogin;
import com.example.perpusdig.ui.beranda.BerandaFragment;
import com.example.perpusdig.ui.lupapass.LupaPassFragment1;
import com.example.perpusdig.ui.openingpage.OpeningFragment2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText txtLoginEmail, txtLoginPass;
    private Button btnLogin;
    private TextView btnForgotPassword;
    private ImageView btnBack;
    private SharedPreferencess sharedPreferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        sharedPreferenceManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        txtLoginEmail = view.findViewById(R.id.txtlogin_email);
        txtLoginPass = view.findViewById(R.id.txtlogin_pass);
        btnLogin = view.findViewById(R.id.btn_login);
        btnForgotPassword = view.findViewById(R.id.btnlogin_lupapass);
        btnBack = view.findViewById(R.id.back);

        // Mengatur EditText password untuk pertama kali disembunyikan
        txtLoginPass.setTransformationMethod(new PasswordTransformationMethod()); // Menyembunyikan password pertama kali

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.biru));
            ((MainActivity) getActivity()).showFragment(new OpeningFragment2(), false);
        });

        btnLogin.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnLogin.startAnimation(anim);
            loginUser();
        });

        btnForgotPassword.setOnClickListener(v -> {
            btnForgotPassword.setTextColor(getResources().getColor(R.color.white));
            ((MainActivity) getActivity()).showFragment(new LupaPassFragment1(), true);
        });

        return view;
    }

    // Metode login user
    public void loginUser() {
        String email = txtLoginEmail.getText().toString().trim();
        String password = txtLoginPass.getText().toString().trim();

        // Validasi input user
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat objek LoginRequest
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Membuat objek API dan menerima data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseLogin> loginUser = ardData.loginUser(loginRequest);

        // Panggil API login
        loginUser.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ResponseLogin loginResponse = response.body();

                    // Jika login berhasil
                    if ("success".equalsIgnoreCase(loginResponse.getStatus())) {
                        Toast.makeText(getActivity(), loginResponse.getPesan(), Toast.LENGTH_SHORT).show();

                        // Ambil data id_user dan nik_anggota
                        int idUser = loginResponse.getData().getId_user();
                        String username = loginResponse.getData().getUsername();
                        String email = loginResponse.getData().getEmail_user();
                        String nikAnggota = loginResponse.getData().getNik_anggota();
                        String namaAnggota = loginResponse.getData().getNama_anggota();
                        String telpAnggota = loginResponse.getData().getTelp();
                        String alamatAnggota = loginResponse.getData().getAlamat();
                        String fotoAnggota = loginResponse.getData().getFoto_anggota();
                        String statusAnggota = loginResponse.getData().getStatus_verifikasi();
                        String tglAnggota = loginResponse.getData().getTgl_pendaftaran();

                        Log.d("LoginSuccess", "id_user: " + idUser);

                        // Menyimpan status login di SharedPreferences
                        sharedPreferenceManager.saveLoginStatus(true);
                        sharedPreferenceManager.saveIdUser(idUser);
                        sharedPreferenceManager.saveEmail(email);
                        sharedPreferenceManager.saveUsername(username);
                        sharedPreferenceManager.saveNikAnggota(nikAnggota);
                        sharedPreferenceManager.saveNamaAnggota(namaAnggota);
                        sharedPreferenceManager.saveTelpAnggota(telpAnggota);
                        sharedPreferenceManager.saveAlamatAnggota(alamatAnggota);
                        sharedPreferenceManager.saveFotoAnggota(fotoAnggota);
                        sharedPreferenceManager.saveStatusAnggota(statusAnggota);
                        sharedPreferenceManager.saveTglAnggota(tglAnggota);

                        ((MainActivity) getActivity()).showFragment(new BerandaFragment(), false);
                        ((MainActivity) getActivity()).updateBottomNavigationSelection(new BerandaFragment());

                    } else {
                        Toast.makeText(getActivity(), loginResponse.getPesan(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorMessage = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Gagal terhubung dengan server";
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Terjadi kesalahan saat membaca error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Kesalahan tidak diketahui";
                Toast.makeText(getActivity(), "Gagal terhubung: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}