package com.example.perpusdig.ui.openingpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.perpusdig.R;
import com.example.perpusdig.MainActivity;

public class OpeningFragment1 extends Fragment {

    private Button btnStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opening_page1, container, false);

        btnStart = view.findViewById(R.id.btn_mulai);

        btnStart.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnStart.startAnimation(anim);
            ((MainActivity) getActivity()).showFragment(new OpeningFragment2(), false);
        });

        return view;
    }
}
