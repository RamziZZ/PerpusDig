package com.example.perpusdig.ui.register;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.perpusdig.model.RegisterRequest;
import com.example.perpusdig.model.ResponseRegister;
import com.example.perpusdig.ui.beranda.BerandaFragment;
import com.example.perpusdig.ui.login.LoginFragment;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisEmailFragment extends Fragment {

    private EditText txtRegisEmail, txtRegisPass, txtRegisPass2;
    private Button btnRegister;
    private ImageView btnBack;
    private SharedPreferencess sharedPreferencesManager;
    private ApiRequestData apiService;

    // Retrofit API
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencess(getActivity());

        Retrofit retrofit = ApiClient.getRetrofitInstance();
        apiService = retrofit.create(ApiRequestData.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_email, container, false);

        // Inisialisasi elemen UI
        txtRegisEmail = view.findViewById(R.id.txtregis_email);
        txtRegisPass = view.findViewById(R.id.txtregis_pass);
        txtRegisPass2 = view.findViewById(R.id.txtregis_pass2);
        btnRegister = view.findViewById(R.id.btn_regis);
        btnBack = view.findViewById(R.id.back);

        // Mengatur EditText password untuk pertama kali disembunyikan
        txtRegisPass.setTransformationMethod(new PasswordTransformationMethod());
        txtRegisPass2.setTransformationMethod(new PasswordTransformationMethod());

        btnRegister.setOnClickListener(v -> {Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnRegister.startAnimation(anim);
            regisAkun();
        });

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.biru));
            ((MainActivity) getActivity()).showFragment(new RegisUsnFragment(), false);
        });

        return view;
    }

    // Metode regis user
    private void regisAkun() {
        String email = txtRegisEmail.getText().toString().trim();
        String password = txtRegisPass.getText().toString().trim();
        String confirmPassword = txtRegisPass2.getText().toString().trim();

        if (!validateInputs(email, password, confirmPassword)) return;

        String username = sharedPreferencesManager.getUsername();
        if (username == null || username.isEmpty()) {
            Toast.makeText(getActivity(), "Username tidak ditemukan. Ulangi langkah registrasi.", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(username, email, password);
        apiService.register(registerRequest).enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseRegister registerResponse = response.body();
                    if ("success".equalsIgnoreCase(registerResponse.getStatus())) {
                        sharedPreferencesManager.clearAllData();
                        ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
                        Toast.makeText(getActivity(), "Akun berhasil dibuat", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Gagal: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Registrasi gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                Toast.makeText(getActivity(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        boolean valid = true;

        // Validasi Email
        if (email.isEmpty()) {
            txtRegisEmail.setError("Email tidak boleh kosong");
            valid = false;
        } else if (!Pattern.matches("^[a-zA-Z0-9._]+@gmail\\.com$", email)) {
            txtRegisEmail.setError("Format email harus menggunakan domain @gmail.com");
            valid = false;
        }

        // Validasi Password
        if (password.isEmpty()) {
            txtRegisPass.setError("Password tidak boleh kosong");
            valid = false;
        } else if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\*).{8,}$", password)) {
            txtRegisPass.setError("Password harus minimal 8 karakter, mengandung huruf besar, huruf kecil, angka, dan simbol *");
            valid = false;
        }

        // Validasi Konfirmasi Password
        if (confirmPassword.isEmpty()) {
            txtRegisPass2.setError("Konfirmasi password tidak boleh kosong");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            txtRegisPass2.setError("Password dan konfirmasi password tidak sama");
            valid = false;
        }

        return valid;
    }
}