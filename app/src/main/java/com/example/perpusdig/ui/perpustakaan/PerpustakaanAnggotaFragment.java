package com.example.perpusdig.ui.perpustakaan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.perpusdig.R;
import com.example.perpusdig.adapter.AdapterDataListBuku;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelBuku;
import com.example.perpusdig.model.ResponseModelBuku;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerpustakaanAnggotaFragment extends Fragment {

    private EditText txtSearch;
    private ImageButton btnSearch;
    private LinearLayout layoutNoResult;
    private TextView txtNoResultQuery;

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModelBuku> listBuku = new ArrayList<>();
    private SwipeRefreshLayout refreshData;
    private ProgressBar loadData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perpustakaan_anggota, container, false);

        // Inisialisasi elemen UI
        btnSearch = view.findViewById(R.id.btn_search);
        txtSearch = view.findViewById(R.id.txt_search);
        layoutNoResult = view.findViewById(R.id.layout_noresult);
        txtNoResultQuery = view.findViewById(R.id.buku);
        rvData = view.findViewById(R.id.cardlist);
        refreshData = view.findViewById(R.id.refresh_data);
        loadData = view.findViewById(R.id.load_data);

        // Atur layout manager untuk RecyclerView List Buku
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);
        dataBuku();

        // Menangani refresh data buku saat swipe-to-refresh dilakukan
        refreshData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData.setRefreshing(true);
                dataBuku();
                refreshData.setRefreshing(false);
            }
        });

        // Menambahkan TextWatcher ke EditText untuk menangani perubahan teks search
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Tidak perlu implementasi
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 0) {
                    txtSearch.setHint("Cari buku atau penulis");
                } else {
                    txtSearch.setHint("");
                    String query = charSequence.toString().trim();
                    filterData(query);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak perlu implementasi
            }
        });

        // Menangani klik pada tombol search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = txtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    filterData(query);
                } else {
                    Toast.makeText(getContext(), "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Menambahkan listener pada tombol kategori
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

    // Metode tampil data buku
    public void dataBuku(){
        loadData.setVisibility(View.VISIBLE);

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelBuku> tampilData = ardData.ardGetDataBuku();

        tampilData.enqueue(new Callback<ResponseModelBuku>() {
            @Override
            public void onResponse(Call<ResponseModelBuku> call, Response<ResponseModelBuku> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listBuku = response.body().getData();
                    adData = new AdapterDataListBuku(getActivity(), listBuku);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                    loadData.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelBuku> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal Menghubungi Server", Toast.LENGTH_SHORT).show();
                loadData.setVisibility(View.GONE);
            }
        });
    }

    // Metode ketika pencarian kosong
    private void showNoResult(String query) {
        layoutNoResult.setVisibility(View.VISIBLE);
        txtNoResultQuery.setText("\"" + query + "\"");
        rvData.setVisibility(View.GONE);
    }
    private void hideNoResult() {
        layoutNoResult.setVisibility(View.GONE);
        rvData.setVisibility(View.VISIBLE);
    }

    // Metode untuk menampilkan data berdasarkan judul buku dan penulis
    private void filterData(String query) {
        if (adData != null) {
            AdapterDataListBuku adapter = (AdapterDataListBuku) adData;
            adapter.filterList(query);
            if (adapter.getItemCount() == 0) {
                showNoResult(query);
            } else {
                hideNoResult();
            }
        }
    }

    // Metode untuk menampilkan data berdasarkan kategori
    private void filterByCategory(String category) {
        if (adData != null) {
            AdapterDataListBuku adapter = (AdapterDataListBuku) adData;
            adapter.filterByCategory(category); // Memanggil metode filter pada adapter
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}