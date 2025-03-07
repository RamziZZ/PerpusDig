package com.example.perpusdig.ui.beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.perpusdig.R;

public class NotifikasiFragment extends Fragment {

    private ImageView btnBack;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifikasi, container, false);

        // Inisialisasi view
        btnBack = view.findViewById(R.id.back);
        listView = view.findViewById(R.id.list_item);

        // Mengatur listener klik untuk tombol back
        btnBack.setOnClickListener(v -> {
            // Animasi saat tombol ditekan
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnBack.startAnimation(anim);

            // Kembali ke fragment sebelumnya
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view; // Mengembalikan tampilan yang telah di-inflate
    }

    @Override
    public void onResume() {
        super.onResume();
        // Menambahkan animasi saat fragment ini muncul
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}