package com.example.perpusdig.ui.regis_anggota;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.ui.profil.ProfilNonAnggotaFragment;

public class GagalRegisFragment extends Fragment {

    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.regis_anggota_ditolak, container, false);

        // Inisialisasi elemen UI
        btnBack = view.findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnBack.startAnimation(anim);

            ((MainActivity) getActivity()).showFragment(new ProfilNonAnggotaFragment(), false);
        });

        return view;
    }
}