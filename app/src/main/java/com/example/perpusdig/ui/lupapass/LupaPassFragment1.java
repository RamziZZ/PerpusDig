package com.example.perpusdig.ui.lupapass;

import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LupaPassFragment1 extends Fragment {

    private EditText txtForgotEmail;
    private Button btnNext;
    private ImageView btnBack;
    private SharedPreferencess sharedPreferencesManager;
    private ApiRequestData apiService;

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
        View view = inflater.inflate(R.layout.lupa_password_email, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        txtForgotEmail = view.findViewById(R.id.txtforgot_email);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.baby_blue));
            ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
        });

        btnNext.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnNext.startAnimation(anim);

            String email = txtForgotEmail.getText().toString().trim();
            if (!validateEmail(email)) return;
            cekEmail(email);
            sharedPreferencesManager.saveEmail(email);

        });
        return view;
    }

    private void cekEmail(String email) {

        LupaPasswordRequest request = new LupaPasswordRequest(email, null);
        apiService.cekEmail(request).enqueue(new Callback<ResponseLupaPassword>() {
            @Override
            public void onResponse(Call<ResponseLupaPassword> call, Response<ResponseLupaPassword> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseLupaPassword lupaResponse = response.body();

                    if ("success".equalsIgnoreCase(lupaResponse.getStatus())) {
                        sharedPreferencesManager.saveEmail(email);
                        Toast.makeText(getActivity(), "Email terdaftar", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).showFragment(new LupaPassFragment2(), false);

                    } else {
                        Toast.makeText(getActivity(), lupaResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Kesalahan: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseLupaPassword> call, Throwable t) {
                Toast.makeText(getActivity(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            txtForgotEmail.setError("Email tidak boleh kosong");
            return false;
        } if (!email.matches("^[a-zA-Z0-9._]+@gmail\\.com$")) {
            txtForgotEmail.setError("Format email tidak valid (@gmail.com)");
            return false;
        } else {
            txtForgotEmail.setError(null);
            return true;
        }
    }
}