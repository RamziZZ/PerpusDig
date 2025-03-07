package com.example.perpusdig.ui.profil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.perpusdig.model.DataModelAnggota;
import com.example.perpusdig.model.DataModelPinjamEbook;
import com.example.perpusdig.model.ResponseModelAnggota;
import com.example.perpusdig.model.ResponseModelPinjamEbook;
import com.example.perpusdig.ui.login.LoginFragment;
import com.example.perpusdig.ui.regis_anggota.GagalRegisFragment;
import com.example.perpusdig.ui.regis_anggota.RegisAnggotaFragment;
import com.example.perpusdig.ui.regis_anggota.WaitingRegisFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilNonAnggotaFragment extends Fragment {

    private Button btnEditProfil, btnRegisAnggota, btnProsesVerif, btnGagalVerif;
    private TextView txtUsername, txtJumlahRiwayat;

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
        View view = inflater.inflate(R.layout.profil_nonanggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());
        int idUser = sharedPreferencesManager.getIdUser();

        // Inisialisasi elemen UI
        btnEditProfil = view.findViewById(R.id.btn_edit);
        txtUsername = view.findViewById(R.id.txt_username);
        btnRegisAnggota = view.findViewById(R.id.btn_regisanggota);
        btnProsesVerif = view.findViewById(R.id.btn_prosesverif);
        btnGagalVerif = view.findViewById(R.id.btn_gagalverif);
        txtJumlahRiwayat = view.findViewById(R.id.txt_jumlahriwayat);
        refreshData = view.findViewById(R.id.refresh_data);
        layoutNoResult = view.findViewById(R.id.layout_noresult);
        rvData = view.findViewById(R.id.cardlist);

        String username = sharedPreferencesManager.getUsername();
        txtUsername.setText(username);
        String status = sharedPreferencesManager.getStatusAnggota();
        updateStatus(status);

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
                String status = sharedPreferencesManager.getStatusAnggota();
                riwayatEbook(idUser);
                statusUser(idUser);
                updateStatus(status);
                refreshData.setRefreshing(false);
            }
        });

        btnEditProfil.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new EditProfileNonAnggotaFragment(), false);
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
                        rvData.setVisibility(View.GONE);
                    } else {
                        layoutNoResult.setVisibility(View.GONE);
                        rvData.setVisibility(View.VISIBLE);
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

    // Metode tampil data anggota
    public void statusUser(Integer idUser) {

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelAnggota> statusUser = ardData.ardGetDataAnggota(idUser);

        statusUser.enqueue(new Callback<ResponseModelAnggota>() {
            @Override
            public void onResponse(Call<ResponseModelAnggota> call, Response<ResponseModelAnggota> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Mendapatkan data anggota
                    List<DataModelAnggota> dataAnggotaList = response.body().getData();
                    DataModelAnggota anggota = dataAnggotaList.get(0);

                    String status = anggota.getStatus_verifikasi();
                    Log.d("StatusUser", "Status Verifikasi: " + status);
                    sharedPreferencesManager.saveStatusAnggota(status);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelAnggota> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String status) {
        // Status tidak ada, tampilkan tombol "Daftar Sebagai Anggota"
        if (status == null || status.isEmpty()) {
            btnRegisAnggota.setVisibility(View.VISIBLE);
            btnProsesVerif.setVisibility(View.GONE);
            btnGagalVerif.setVisibility(View.GONE);

            btnRegisAnggota.setOnClickListener(v -> {
                ((MainActivity) getActivity()).showFragment(new RegisAnggotaFragment(), false);
            });

        } else if (status.equalsIgnoreCase("Ditunda")) {
            // Status "Ditunda", tampilkan tombol "Proses Verifikasi"
            btnRegisAnggota.setVisibility(View.GONE);
            btnProsesVerif.setVisibility(View.VISIBLE);
            btnGagalVerif.setVisibility(View.GONE);

            btnProsesVerif.setOnClickListener(v -> {
                ((MainActivity) getActivity()).showFragment(new WaitingRegisFragment(), false);
            });

        } else if (status.equalsIgnoreCase("Ditolak")) {
            // Status "Ditolak", tampilkan tombol "Verifikasi Ditolak"
            btnRegisAnggota.setVisibility(View.GONE);
            btnProsesVerif.setVisibility(View.GONE);
            btnGagalVerif.setVisibility(View.VISIBLE);

            btnGagalVerif.setOnClickListener(v -> {
                ((MainActivity) getActivity()).showFragment(new GagalRegisFragment(), false);
            });

        } else if (status.equalsIgnoreCase("Disetujui")) {
            // Status "Disetujui", langsung ke LoginFragment
            ((MainActivity) getActivity()).showFragment(new LoginFragment(), false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}