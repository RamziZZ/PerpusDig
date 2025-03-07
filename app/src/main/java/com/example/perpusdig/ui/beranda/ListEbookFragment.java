package com.example.perpusdig.ui.beranda;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.adapter.AdapterDataListEbook;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelEbook;
import com.example.perpusdig.model.ResponseModelEbook;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListEbookFragment extends Fragment {

    // Deklarasi variabel untuk elemen UI
    private EditText txtSearch;
    private ImageView btnBack, btnSearch;
    private LinearLayout layoutNoResult;
    private TextView txtNoResultQuery;

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DataModelEbook> listEbook = new ArrayList<>();
    private SwipeRefreshLayout refreshData;
    private ProgressBar loadData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_list, container, false);

        // Inisialisasi elemen UI
        btnBack = view.findViewById(R.id.back);
        btnSearch = view.findViewById(R.id.btn_search);
        txtSearch = view.findViewById(R.id.txt_search);
        layoutNoResult = view.findViewById(R.id.layout_noresult);
        txtNoResultQuery = view.findViewById(R.id.buku);
        rvData = view.findViewById(R.id.cardlist);
        refreshData = view.findViewById(R.id.refresh_data);
        loadData = view.findViewById(R.id.load_data);

        // Atur layout manager untuk RecyclerView List Ebook
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);
        dataEbook();

        // Menangani refresh data buku saat swipe-to-refresh dilakukan
        refreshData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData.setRefreshing(true);
                dataEbook();
                refreshData.setRefreshing(false);
            }
        });

        // Menambahkan TextWatcher ke EditText untuk menangani perubahan teks
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = txtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    filterData(query);
                } else {
                    Toast.makeText(getActivity(), "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            btnBack.setColorFilter(getResources().getColor(R.color.baby_blue));
            ((MainActivity) getActivity()).showFragment(new BerandaFragment(), false);
        });

        return view;
    }

    // Metode tampil data ebook
    public void dataEbook() {
        loadData.setVisibility(View.VISIBLE);

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelEbook> tampilData = ardData.ardGetDataEbook();

        tampilData.enqueue(new Callback<ResponseModelEbook>() {
            @Override
            public void onResponse(Call<ResponseModelEbook> call, Response<ResponseModelEbook> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listEbook = response.body().getData();
                    adData = new AdapterDataListEbook(getActivity(), listEbook);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                    loadData.setVisibility(View.GONE);

                    // Setelah data berhasil dimuat, lakukan filter berdasarkan kategori
                    Bundle arguments = getArguments();
                    String category = arguments != null ? arguments.getString("kategori", "") : "";
                    Log.d("EbookListFragment", "Filter berdasarkan kategori: " + category);
                    filterByCategory(category);
                }
            }
            @Override
            public void onFailure(Call<ResponseModelEbook> call, Throwable t) {
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
            AdapterDataListEbook adapter = (AdapterDataListEbook) adData;
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
            AdapterDataListEbook adapter = (AdapterDataListEbook) adData;
            if (category != null && !category.isEmpty()) {
                adapter.filterByCategory(category);
            } else {
                // Tampilkan semua data jika kategori kosong
                adapter.filterByCategory("");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}