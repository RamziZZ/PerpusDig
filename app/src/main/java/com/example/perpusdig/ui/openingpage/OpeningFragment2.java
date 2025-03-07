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
import com.example.perpusdig.ui.login.LoginFragment;
import com.example.perpusdig.MainActivity;
import com.example.perpusdig.ui.register.RegisUsnFragment;

public class OpeningFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opening_page2, container, false);

        Button loginButton = view.findViewById(R.id.btn_masuk);
        loginButton.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            loginButton.startAnimation(anim);
            ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
        });

        Button registerButton = view.findViewById(R.id.btn_daftar);
        registerButton.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            loginButton.startAnimation(anim);
            ((MainActivity) getActivity()).showFragment(new RegisUsnFragment(), false);
        });

        return view;
    }
}