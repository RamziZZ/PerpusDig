package com.example.perpusdig.ui.lupapass;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
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
import com.example.perpusdig.model.LupaPasswordRequest;
import com.example.perpusdig.model.ResponseLupaPassword;
import com.example.perpusdig.ui.login.LoginFragment;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LupaPassFragment2 extends Fragment {

    private EditText txtForgotPass, txtForgotPass2;
    private Button btnSave;
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
        View view = inflater.inflate(R.layout.lupa_password, container, false);

        // Inisialisasi elemen UI
        txtForgotPass = view.findViewById(R.id.txtforgot_pass);
        txtForgotPass2 = view.findViewById(R.id.txtforgot_pass2);
        btnSave = view.findViewById(R.id.btn_save);
        btnBack = view.findViewById(R.id.back);

        // Mengatur EditText password untuk pertama kali disembunyikan
        txtForgotPass.setTransformationMethod(new PasswordTransformationMethod()); // Menyembunyikan password pertama kali
        txtForgotPass2.setTransformationMethod(new PasswordTransformationMethod()); // Menyembunyikan konfirmasi password pertama kali

        btnSave.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnSave.startAnimation(anim);
            lupaPassword();
        });

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.baby_blue));
            ((MainActivity) getActivity()).showFragment(new LupaPassFragment1(), false);
        });

        return view;
    }

    // Metode lupa password
    private void lupaPassword() {
        String password = txtForgotPass.getText().toString().trim();
        String confirmPassword = txtForgotPass2.getText().toString().trim();

        // Validasi input password
        if (!validateInputs(password, confirmPassword)) return;

        // Ambil email dari SharedPreferences
        String email = sharedPreferencesManager.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(requireContext(), "Email tidak ditemukan. Ulangi proses lupa password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat permintaan reset password
        LupaPasswordRequest lupaPassRequest = new LupaPasswordRequest(email, password);

        // Panggil API untuk reset password
        apiService.lupapassword(lupaPassRequest).enqueue(new Callback<ResponseLupaPassword>() {
            @Override
            public void onResponse(Call<ResponseLupaPassword> call, Response<ResponseLupaPassword> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseLupaPassword lupaPassResponse = response.body();
                    if ("success".equalsIgnoreCase(lupaPassResponse.getStatus())) {
                        sharedPreferencesManager.clearAllData();
                        ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
                        Toast.makeText(getActivity(), "Password berhasil diubah.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Gagal: " + lupaPassResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Reset gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseLupaPassword> call, Throwable t) {
                Toast.makeText(getActivity(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String password, String confirmPassword) {
        boolean valid = true;

        // Validasi Password
        if (password.isEmpty()) {
            txtForgotPass.setError("Password tidak boleh kosong");
            valid = false;
        } else if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\*).{8,}$", password)) {
            txtForgotPass.setError("Password harus minimal 8 karakter, mengandung huruf besar, huruf kecil, angka, dan simbol *");
            valid = false;
        } else {
            txtForgotPass.setError(null); // Hapus error jika valid
        }

        // Validasi Konfirmasi Password
        if (confirmPassword.isEmpty()) {
            txtForgotPass2.setError("Konfirmasi password tidak boleh kosong");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            txtForgotPass2.setError("Konfirmasi password tidak sesuai");
            valid = false;
        } else {
            txtForgotPass2.setError(null); // Hapus error jika valid
        }
        return valid;
    }
}