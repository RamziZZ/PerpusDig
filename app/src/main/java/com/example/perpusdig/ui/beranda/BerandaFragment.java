package com.example.perpusdig.ui.beranda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.adapter.AdapterDataTrendingEbook;
import com.example.perpusdig.adapter.AdapterRiwayatSampulEbook;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelEbook;
import com.example.perpusdig.model.DataModelPinjamEbook;
import com.example.perpusdig.model.ResponseModelEbook;
import com.example.perpusdig.model.ResponseModelPinjamEbook;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    private TextView txtSearch;
    private ImageButton btnAbout;

    private RecyclerView rvDataRiwayat;
    private RecyclerView rvDataTrending;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModelEbook> trendingEbook = new ArrayList<>();
    private List<DataModelPinjamEbook> riwayatEbook = new ArrayList<>();
    private SharedPreferencess sharedPreferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beranda, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());
        int idUser = sharedPreferencesManager.getIdUser();

        // Inisialisasi elemen UI
        txtSearch = view.findViewById(R.id.txt_search);
        btnAbout = view.findViewById(R.id.btn_about);
        rvDataRiwayat = view.findViewById(R.id.riwayatbacaan);
        rvDataTrending = view.findViewById(R.id.trending);

        // Atur layout manager untuk RecyclerView Riwayat Bacaan
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvDataRiwayat.setLayoutManager(lmData);
        riwayatSampulEbook(idUser);

        // Atur layout manager untuk RecyclerView Trending
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvDataTrending.setLayoutManager(lmData);
        trendingEbook();

        txtSearch.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new ListEbookFragment(), false);
        });

        btnAbout.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new AboutPerpustakaanFragment(), true);
        });

        // Tombol kategori buku
        view.findViewById(R.id.buku_fiksi).setOnClickListener(v -> filterByCategory("Fiksi"));
        view.findViewById(R.id.ensiklopedia).setOnClickListener(v -> filterByCategory("Ensiklopedia"));
        view.findViewById(R.id.komik).setOnClickListener(v -> filterByCategory("Komik"));
        view.findViewById(R.id.filsafat).setOnClickListener(v -> filterByCategory("Filsafat"));
        view.findViewById(R.id.biografi_otobiografi).setOnClickListener(v -> filterByCategory("Biografi dan Otobiografi"));
        view.findViewById(R.id.hukum_politik).setOnClickListener(v -> filterByCategory("Hukum dan Politik"));
        view.findViewById(R.id.bisnis).setOnClickListener(v -> filterByCategory("Bisnis"));
        view.findViewById(R.id.self_improvement).setOnClickListener(v -> filterByCategory("Self improvement"));

        return view;
    }

    // Metode tampil riwayat ebook
    public void riwayatSampulEbook(Integer idUser) {
        // Membuat objek API dan menerima data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelPinjamEbook> tampilRiwayat = ardData.ardGetRiwayatEbook(idUser);

        tampilRiwayat.enqueue(new Callback<ResponseModelPinjamEbook>() {
            @Override
            public void onResponse(Call<ResponseModelPinjamEbook> call, Response<ResponseModelPinjamEbook> response) {
                if (response.isSuccessful() && response.body() != null) {

                    riwayatEbook = response.body().getData();
                    adData = new AdapterRiwayatSampulEbook(getActivity(), riwayatEbook);
                    rvDataRiwayat.setAdapter(adData);
                    adData.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ResponseModelPinjamEbook> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode tampil trending ebook
    public void trendingEbook(){
        // Membuat objek API dan menerima data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelEbook> tampilTrending = ardData.ardGetTrendingEbook();

        tampilTrending.enqueue(new Callback<ResponseModelEbook>() {
            @Override
            public void onResponse(Call<ResponseModelEbook> call, Response<ResponseModelEbook> response) {
                if (response.isSuccessful() && response.body() != null) {

                    trendingEbook = response.body().getData();
                    adData = new AdapterDataTrendingEbook(getActivity(), trendingEbook);
                    rvDataTrending.setAdapter(adData);
                    adData.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ResponseModelEbook> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk mencari buku berdasarkan kategori
    private void filterByCategory(String category) {
        // Buat fragment dan siapkan argumen
        ListEbookFragment ebookListFragment = new ListEbookFragment();
        Bundle bundle = new Bundle();
        bundle.putString("kategori", category); // Kirim data kategori
        ebookListFragment.setArguments(bundle); // Set argumen ke fragment

        ((MainActivity) getActivity()).showFragment(ebookListFragment, false);
    }
}