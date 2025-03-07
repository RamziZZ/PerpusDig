package com.example.perpusdig.ui.perpustakaan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.perpusdig.R;

public class PerpustakaanNonAnggotaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perpustakaan_nonanggota, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}