package com.example.perpusdig.ui.beranda;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelEbook;
import com.example.perpusdig.model.ResponseModelEbook;
import com.example.perpusdig.model.ResponseModelPinjamEbook;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEbookFragment extends Fragment {

    private TextView judulEbook, penulisEbook, penerbitEbook, thnTerbitEbook, kategoriEbook, deskripsiEbook,
            info, deskripsi;
    private ImageView sampulEbook, btnBack;
    private Button btnPinjam;
    private ProgressBar loadDetail;
    private NestedScrollView mainContent;
    private LinearLayout layoutInfo, layoutDeskripsi;
    private SharedPreferencess sharedPreferencesManager;

    private int idEbook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_ebook, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        sampulEbook = view.findViewById(R.id.sampul_ebook);
        judulEbook = view.findViewById(R.id.judul_ebook);
        penulisEbook = view.findViewById(R.id.penulis_ebook);
        penerbitEbook = view.findViewById(R.id.penerbit_ebook);
        thnTerbitEbook = view.findViewById(R.id.thnterbit_ebook);
        kategoriEbook = view.findViewById(R.id.kategori_ebook);
        deskripsiEbook = view.findViewById(R.id.deskripsi_ebook);
        btnBack = view.findViewById(R.id.back);
        btnPinjam = view.findViewById(R.id.btn_pinjam);
        loadDetail = view.findViewById(R.id.load_detail);
        mainContent = view.findViewById(R.id.main_content);
        info = view.findViewById(R.id.info);
        deskripsi = view.findViewById(R.id.deskripsi);
        layoutInfo = view.findViewById(R.id.layout_info);
        layoutDeskripsi = view.findViewById(R.id.layout_deskripsi);

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Mengatur listener klik untuk informasi buku
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengubah status selected untuk toggle gambar
                info.setSelected(!info.isSelected());

                if (layoutInfo.getVisibility() == View.GONE) {
                    layoutInfo.setVisibility(View.VISIBLE);
                } else {
                    layoutInfo.setVisibility(View.GONE);
                }
            }
        });

        // Mengatur listener klik untuk deskripsi buku
        deskripsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengubah status selected untuk toggle gambar
                deskripsi.setSelected(!deskripsi.isSelected());

                if (layoutDeskripsi.getVisibility() == View.GONE) {
                    layoutDeskripsi.setVisibility(View.VISIBLE);
                } else {
                    layoutDeskripsi.setVisibility(View.GONE);
                }
            }
        });

        // Terima ID Buku dari arguments
        if (getArguments() != null) {
            idEbook = getArguments().getInt("id_ebook", -1); // -1 sebagai default jika id tidak ditemukan
            if (idEbook != -1) {
                getDetailEbook(idEbook);
            } else {
                Toast.makeText(getActivity(), "ID Buku tidak valid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Tidak ada data yang diterima", Toast.LENGTH_SHORT).show();
        }

        btnPinjam.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_click);
            btnPinjam.startAnimation(anim);

            // Tampilkan dialog konfirmasi
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Baca E-Buku");
            builder.setMessage("Apakah Anda yakin ingin membacanya?");
            builder.setNegativeButton("Tidak", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.setPositiveButton("Ya", (dialogInterface, i) -> {
                // Jika pengguna memilih Ya
                pinjamEbook();
            });
            builder.show();
        });

        return view;
    }

    // Metode untuk mengambil data buku berdasarkan id
    private void getDetailEbook(int id) {
        loadDetail.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelEbook> detailEbook = ardData.ardGetDetailEbook(String.valueOf(id));

        detailEbook.enqueue(new Callback<ResponseModelEbook>() {
            @Override
            public void onResponse(Call<ResponseModelEbook> call, Response<ResponseModelEbook> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModelEbook responseModel = response.body();
                    List<DataModelEbook> detailEbook = responseModel.getData();  // Dapatkan list buku

                    // Ambil buku pertama jika data ada
                    if (detailEbook != null && !detailEbook.isEmpty()) {
                        DataModelEbook ebook = detailEbook.get(0);  // Ambil buku pertama dari list

                        // Atur data ke UI
                        judulEbook.setText(ebook.getJudul());
                        penulisEbook.setText(ebook.getPenulis());
                        penerbitEbook.setText(ebook.getPenerbit());
                        thnTerbitEbook.setText(String.valueOf(ebook.getTahun_terbit()));
                        kategoriEbook.setText(ebook.getKategori());
                        deskripsiEbook.setText(ebook.getSinopsis());

                        // Dekode gambar sampul
                        if (ebook.getSampul() != null && !ebook.getSampul().isEmpty()) {
                            byte[] decodedString = Base64.decode(ebook.getSampul(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            sampulEbook.setImageBitmap(decodedByte);
                        }

                        loadDetail.setVisibility(View.GONE);
                        mainContent.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Data buku tidak ditemukan", Toast.LENGTH_SHORT).show();
                        loadDetail.setVisibility(View.GONE);
                        mainContent.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Gagal memuat detail buku", Toast.LENGTH_SHORT).show();
                    loadDetail.setVisibility(View.GONE);
                    mainContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseModelEbook> call, Throwable t) {
                Log.e("DetailBuku", "Kesalahan: " + t.getMessage());
                Toast.makeText(getActivity(), "Kesalahan server", Toast.LENGTH_SHORT).show();
                loadDetail.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
            }
        });
    }

    // Metode untuk melakukan peminjaman ebook
    private void pinjamEbook() {
        // Ambil data id_user dari SharedPreferences
        int idUser = sharedPreferencesManager.getIdUser();

        // Cek jika data id_user valid
        if (idUser != -1) {
            // Data valid, lanjutkan peminjaman buku
            String judul = judulEbook.getText().toString();
            String kategori = kategoriEbook.getText().toString();
            String statusPeminjaman = "Ditunda";
            int idEbook = this.idEbook;

            // Membuat instance Retrofit
            ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
            Call<ResponseModelPinjamEbook> pinjamEbook = ardData.ardCreatePinjamEbook
                    (judul, kategori, statusPeminjaman, idUser, idEbook);

            // Mengirimkan permintaan
            pinjamEbook.enqueue(new Callback<ResponseModelPinjamEbook>() {
                @Override
                public void onResponse(Call<ResponseModelPinjamEbook> call, Response<ResponseModelPinjamEbook> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String pesan = response.body().getPesan();
                        Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                        getPdf(idEbook);
                    } else {
                        Toast.makeText(getActivity(), "Gagal melakukan peminjaman", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModelPinjamEbook> call, Throwable t) {
                    Toast.makeText(getActivity(), "Gagal menghubungi server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getPdf(int id) {
        // Membuat instance Retrofit untuk mengambil PDF berdasarkan idEbook
        ApiRequestData apiRequestData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelEbook> getPdf = apiRequestData.ardGetDetailEbook(String.valueOf(id));

        getPdf.enqueue(new Callback<ResponseModelEbook>() {
            @Override
            public void onResponse(Call<ResponseModelEbook> call, Response<ResponseModelEbook> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pdfBase64 = response.body().getData().get(0).getPdf();  // Dapatkan PDF dalam format Base64

                    // Kirim data PDF ke BacaEbookFragment
                    if (pdfBase64 != null && !pdfBase64.isEmpty()) {
                        BacaEbookFragment bacaEbookFragment = BacaEbookFragment.newInstance(pdfBase64);

                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, bacaEbookFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    Toast.makeText(getActivity(), "Gagal mengambil PDF", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseModelEbook> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}