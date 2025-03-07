package com.example.perpusdig.ui.profil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.adapter.AdapterRiwayatEbook;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelPinjamEbook;
import com.example.perpusdig.model.ResponseModelPinjamEbook;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilAnggotaFragment extends Fragment {

    private Button btnEditProfil;
    private TextView txtNamaAnggota, txtUsername, txtTgl, txtJumlahRiwayat;
    private ImageView profil;

    private SwipeRefreshLayout refreshData;
    private LinearLayout layoutNoResult;

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModelPinjamEbook> riwayatEbook = new ArrayList<>();
    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil_anggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());
        int idUser = sharedPreferencesManager.getIdUser();

        // Inisialisasi elemen UI
        btnEditProfil = view.findViewById(R.id.btn_edit);
        txtNamaAnggota = view.findViewById(R.id.txt_namaanggota);
        txtUsername = view.findViewById(R.id.txt_username);
        txtTgl = view.findViewById(R.id.txt_tgl);
        txtJumlahRiwayat = view.findViewById(R.id.txt_jumlahriwayat);
        profil = view.findViewById(R.id.img_profil);
        refreshData = view.findViewById(R.id.refresh_data);
        layoutNoResult = view.findViewById(R.id.layout_noresult);
        rvData = view.findViewById(R.id.cardlist);

        // Set data dari sharedPreferences
        String fotoAnggota = sharedPreferencesManager.getFotoAnggota();
        if (fotoAnggota != null && !fotoAnggota.isEmpty()) {
            byte[] decodedString = Base64.decode(fotoAnggota, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profil.setImageBitmap(decodedByte);
        }
        String namaAnggota = sharedPreferencesManager.getNamaAnggota();
        txtNamaAnggota.setText(namaAnggota);
        String username = sharedPreferencesManager.getUsername();
        txtUsername.setText(username);
        String tglAnggota = sharedPreferencesManager.getTglAnggota();
        txtTgl.setText(tglAnggota);

        // Atur layout manager untuk RecyclerView Riwayat Ebook
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);
        riwayatEbook(idUser);

        // Menangani refresh data buku saat swipe-to-refresh dilakukan
        refreshData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData.setRefreshing(true);
                int idUser = sharedPreferencesManager.getIdUser();
                riwayatEbook(idUser);
                refreshData.setRefreshing(false);
            }
        });

        btnEditProfil.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnEditProfil.startAnimation(anim);

            ((MainActivity) getActivity()).showFragment(new EditProfileAnggotaFragment(), false);
        });

        return view;
    }

    // Metode tampil riwayat ebook
    public void riwayatEbook(Integer idUser) {

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelPinjamEbook> tampilRiwayat = ardData.ardGetRiwayatEbook(idUser);

        tampilRiwayat.enqueue(new Callback<ResponseModelPinjamEbook>() {
            @Override
            public void onResponse(Call<ResponseModelPinjamEbook> call, Response<ResponseModelPinjamEbook> response) {
                if (response.isSuccessful() && response.body() != null) {

                    riwayatEbook = response.body().getData();
                    txtJumlahRiwayat.setText(String.valueOf(riwayatEbook.size()));

                    if (riwayatEbook == null || riwayatEbook.isEmpty()) {
                        layoutNoResult.setVisibility(View.VISIBLE);
                        refreshData.setVisibility(View.GONE);
                    } else {
                        layoutNoResult.setVisibility(View.GONE);
                        refreshData.setVisibility(View.VISIBLE);
                    }

                    // Menggunakan AdapterRiwayatEbook untuk menampilkan data
                    adData = new AdapterRiwayatEbook(getActivity(), riwayatEbook);
                    rvData.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Data kosong atau response tidak valid", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseModelPinjamEbook> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}