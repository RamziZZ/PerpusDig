package com.example.perpusdig.ui.openingpage;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.perpusdig.R;
import com.example.perpusdig.MainActivity;

public class OpeningFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opening_page, container, false);

        // Mengatur Handler untuk berpindah ke OpeningFragment2 setelah 5 detik
        new Handler().postDelayed(() -> {
            ((MainActivity) getActivity()).showFragment(new OpeningFragment1(), false);
        }, 5000); // 5000 milidetik = 5 detik

        return view;
    }
}