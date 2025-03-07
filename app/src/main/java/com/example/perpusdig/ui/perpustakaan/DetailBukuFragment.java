package com.example.perpusdig.ui.perpustakaan;

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

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.SharedPreferencess;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelBuku;
import com.example.perpusdig.model.ResponseModelPinjamBuku;
import com.example.perpusdig.model.ResponseModelBuku;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBukuFragment extends Fragment {

    private TextView isbnBuku, judulBuku, penulisBuku, penerbitBuku, thnTerbitBuku, kategoriBuku, jumlahBuku, deskripsiBuku,
            info, deskripsi;
    private ImageView sampulBuku, btnBack;
    private Button btnPinjam;
    private ProgressBar loadDetail;
    private NestedScrollView mainContent;
    private LinearLayout layoutInfo, layoutDeskripsi;
    private SharedPreferencess sharedPreferencesManager;

    private int idBuku;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_buku, container, false);

        sharedPreferencesManager = new SharedPreferencess(getActivity());

        // Inisialisasi elemen UI
        sampulBuku = view.findViewById(R.id.sampul_buku);
        isbnBuku = view.findViewById(R.id.isbn_buku);
        judulBuku = view.findViewById(R.id.judul_buku);
        penulisBuku = view.findViewById(R.id.penulis_buku);
        penerbitBuku = view.findViewById(R.id.penerbit_buku);
        thnTerbitBuku = view.findViewById(R.id.thnterbit_buku);
        kategoriBuku = view.findViewById(R.id.kategori_buku);
        jumlahBuku = view.findViewById(R.id.jumlah_buku);
        deskripsiBuku = view.findViewById(R.id.deskripsi_buku);
        btnBack = view.findViewById(R.id.back);
        btnPinjam = view.findViewById(R.id.btn_pinjam);
        loadDetail = view.findViewById(R.id.load_detail);
        mainContent = view.findViewById(R.id.main_content);
        info = view.findViewById(R.id.info);
        deskripsi = view.findViewById(R.id.deskripsi);
        layoutInfo = view.findViewById(R.id.layout_info);
        layoutDeskripsi = view.findViewById(R.id.layout_deskripsi);

        btnBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragment(new PerpustakaanAnggotaFragment(),false);
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
            idBuku = getArguments().getInt("id_buku", -1); // -1 sebagai default jika id tidak ditemukan
            if (idBuku != -1) {
                // Panggil fungsi untuk mendapatkan detail buku
                getDetailBuku(idBuku);
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
            builder.setTitle("Pinjam Buku");
            builder.setMessage("Apakah Anda yakin ingin melakukan pengajuan peminjaman buku?");
            builder.setNegativeButton("Tidak", (dialogInterface, i) -> {
                // Jika pengguna memilih Tidak
                Toast.makeText(getActivity(), "Pengajuan peminjaman dibatalkan", Toast.LENGTH_SHORT).show();
            });
            builder.setPositiveButton("Ya", (dialogInterface, i) -> {
                // Jika pengguna memilih Ya
                pinjamBuku();
            });
            builder.show();
        });

        return view;
    }

    // Metode untuk mengambil data buku berdasarkan id
    private void getDetailBuku(int id) {
        loadDetail.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelBuku> detailBuku = ardData.ardGetDetailBuku(String.valueOf(id));

        detailBuku.enqueue(new Callback<ResponseModelBuku>() {
            @Override
            public void onResponse(Call<ResponseModelBuku> call, Response<ResponseModelBuku> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModelBuku responseModel = response.body();
                    List<DataModelBuku> detailBuku = responseModel.getData();  // Dapatkan list buku

                    // Ambil buku pertama jika data ada
                    if (detailBuku != null && !detailBuku.isEmpty()) {
                        DataModelBuku buku = detailBuku.get(0);  // Ambil buku pertama dari list

                        // Atur data ke UI
                        isbnBuku.setText(buku.getIsbn());
                        judulBuku.setText(buku.getJudul_buku());
                        penulisBuku.setText(buku.getPenulis_buku());
                        penerbitBuku.setText(buku.getPenerbit_buku());
                        thnTerbitBuku.setText(String.valueOf(buku.getTahun_terbit_buku()));
                        kategoriBuku.setText(buku.getKategori_buku());
                        jumlahBuku.setText(String.valueOf(buku.getJumlah_buku()));
                        deskripsiBuku.setText(buku.getDeskripsi());

                        // Dekode gambar sampul
                        if (buku.getSampul_buku() != null && !buku.getSampul_buku().isEmpty()) {
                            byte[] decodedString = Base64.decode(buku.getSampul_buku(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            sampulBuku.setImageBitmap(decodedByte);
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
            public void onFailure(Call<ResponseModelBuku> call, Throwable t) {
                Log.e("DetailBuku", "Kesalahan: " + t.getMessage());
                Toast.makeText(getActivity(), "Kesalahan server", Toast.LENGTH_SHORT).show();
                loadDetail.setVisibility(View.GONE);
                mainContent.setVisibility(View.GONE);
            }
        });
    }

    // Metode untuk melakukan peminjaman buku
    private void pinjamBuku() {
        // Ambil data id_user dan nik_anggota dari SharedPreferences
        int idUser = sharedPreferencesManager.getIdUser();
        String nikAnggota = sharedPreferencesManager.getNikAnggota();

        // Cek jika data id_user dan nik_anggota valid
        if (idUser != -1 && nikAnggota != null && !nikAnggota.isEmpty()) {
            // Data valid, lanjutkan peminjaman buku
            String judul = judulBuku.getText().toString();
            String kategori = kategoriBuku.getText().toString();
            String statusPeminjaman = "Ditunda"; // Status default
            int idBuku = this.idBuku; // ID buku yang diterima dari arguments

            // Membuat instance Retrofit
            ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
            Call<ResponseModelPinjamBuku> pinjamBuku = ardData.ardCreatePinjamBuku
                    (judul, kategori, statusPeminjaman, idUser, idBuku, nikAnggota);

            // Mengirimkan permintaan
            pinjamBuku.enqueue(new Callback<ResponseModelPinjamBuku>() {
                @Override
                public void onResponse(Call<ResponseModelPinjamBuku> call, Response<ResponseModelPinjamBuku> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String pesan = response.body().getPesan();
                        Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Gagal melakukan peminjaman", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModelPinjamBuku> call, Throwable t) {
                    Toast.makeText(getActivity(), "Gagal menghubungi server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}