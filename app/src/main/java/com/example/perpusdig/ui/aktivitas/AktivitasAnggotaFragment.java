package com.example.perpusdig.ui.aktivitas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.adapter.AdapterRiwayatBuku;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelPinjamBuku;
import com.example.perpusdig.model.ResponseModelPinjamBuku;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AktivitasAnggotaFragment extends Fragment {

    private SwipeRefreshLayout refreshData;
    private ProgressBar loadData;
    private LinearLayout layoutNoResult;

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModelPinjamBuku> riwayatBuku = new ArrayList<>();
    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aktivitas_anggota, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());
        int idUser = sharedPreferencesManager.getIdUser();

        // Inisialisasi elemen UI
        refreshData = view.findViewById(R.id.refresh_data);
        loadData = view.findViewById(R.id.load_data);
        layoutNoResult = view.findViewById(R.id.layout_noresult);
        rvData = view.findViewById(R.id.cardlist);

        // Atur layout manager untuk RecyclerView Riwayat Buku
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);
        riwayatBuku(idUser);

        // Menangani refresh data buku saat swipe-to-refresh dilakukan
        refreshData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData.setRefreshing(true);
                int idUser = sharedPreferencesManager.getIdUser(); // Mendapatkan ID user yang disimpan
                riwayatBuku(idUser);
                refreshData.setRefreshing(false);
            }
        });

        return view;
    }

    // Metode tampil riwayat buku
    public void riwayatBuku(Integer idUser){
        loadData.setVisibility(View.VISIBLE);

        // Membuat objek API dan menerima data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelPinjamBuku> tampilRiwayat = ardData.ardGetRiwayatBuku(idUser);

        tampilRiwayat.enqueue(new Callback<ResponseModelPinjamBuku>() {
            @Override
            public void onResponse(Call<ResponseModelPinjamBuku> call, Response<ResponseModelPinjamBuku> response) {
                if (response.isSuccessful() && response.body() != null) {

                    riwayatBuku = response.body().getData();

                    if (riwayatBuku == null || riwayatBuku.isEmpty()) {
                        // Jika data kosong, tampilkan layout_noresult dan sembunyikan refresh_data
                        layoutNoResult.setVisibility(View.VISIBLE);
                        rvData.setVisibility(View.GONE);
                    } else {
                        // Jika ada data, tampilkan refresh_data dan sembunyikan layout_noresult
                        layoutNoResult.setVisibility(View.GONE);
                        rvData.setVisibility(View.VISIBLE);
                    }

                    adData = new AdapterRiwayatBuku(getActivity(), riwayatBuku);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                    loadData.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelPinjamBuku> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server", Toast.LENGTH_SHORT).show();
                loadData.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}