package com.example.perpusdig.ui.register;

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
import com.example.perpusdig.ui.openingpage.OpeningFragment2;

public class RegisUsnFragment extends Fragment {

    private EditText txtRegisUsn;
    private Button btnNext;
    private ImageView btnBack;
    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_username, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        txtRegisUsn = view.findViewById(R.id.txtregis_usn);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.biru));
            ((MainActivity) getActivity()).showFragment(new OpeningFragment2(), false);
        });

        btnNext.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnNext.startAnimation(anim);

            String username = txtRegisUsn.getText().toString().trim();
            if (!validateUsername(username))
                return;

            sharedPreferencesManager.saveUsername(username);
            Toast.makeText(getActivity(), "Username berhasil dibuat", Toast.LENGTH_SHORT).show();

            ((MainActivity) getActivity()).showFragment(new RegisEmailFragment(), false);
        });

        return view;
    }

    private boolean validateUsername(String username) {
        boolean isValid = true;

        if (username.isEmpty()) {
            txtRegisUsn.setError("Username tidak boleh kosong");
            isValid = false;
        }
        else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            txtRegisUsn.setError("Username hanya boleh mengandung huruf, angka, atau underscore");
            isValid = false;
        }
        else if (username.length() < 5 || username.length() > 10) {
            txtRegisUsn.setError("Username harus terdiri dari 5 - 10 karakter");
            isValid = false;
        }

        return isValid;
    }
}