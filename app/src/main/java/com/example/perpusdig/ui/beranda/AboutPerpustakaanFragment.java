package com.example.perpusdig.ui.beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;

public class AboutPerpustakaanFragment extends Fragment {

    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_perpustakaan, container, false);

        // Inisialisasi elemen UI
        btnBack = view.findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new BerandaFragment(), false);
        });

        return view; // Mengembalikan tampilan yang telah di-inflate
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}