package com.example.perpusdig.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpusdig.MainActivity;
import com.example.perpusdig.R;
import com.example.perpusdig.model.DataModelBuku;
import com.example.perpusdig.ui.perpustakaan.DetailBukuFragment;

import java.util.ArrayList;
import java.util.List;

public class AdapterDataListBuku extends RecyclerView.Adapter<AdapterDataListBuku.HolderData>{
    private Context ctx;
    private List<DataModelBuku> listBuku;
    private final List<DataModelBuku> listBukuFull; // Salinan data asli

    public AdapterDataListBuku(Context ctx, List<DataModelBuku> listBuku) {
        this.ctx = ctx;
        this.listBuku = new ArrayList<>(listBuku); // Salin data
        this.listBukuFull = new ArrayList<>(listBuku); // Salin data asli untuk filter
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_perpus, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModelBuku dm = listBuku.get(position);

        // Dekode Base64 ke Bitmap dan tampilkan di ImageView
        String sampulBukuBase64 = dm.getSampul_buku();
        if (sampulBukuBase64 != null) {
            byte[] decodedString = Base64.decode(sampulBukuBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sampulBuku.setImageBitmap(decodedByte);  // Menampilkan gambar di ImageView
        }

        holder.txtIdBuku.setText(String.valueOf(dm.getId_buku()));
        holder.txtJudulBuku.setText(dm.getJudul_buku());
        holder.txtPenulisBuku.setText(dm.getPenulis_buku());
        holder.txtJumlahBuku.setText(String.valueOf(dm.getJumlah_buku()));
        holder.txtKategoriBuku.setText(dm.getKategori_buku());

        // OnClickListener pada item untuk membuka DetailBukuFragment
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_buku", dm.getId_buku());

            // Buat fragment baru dan tambahkan bundle
            DetailBukuFragment detailBukuFragment = new DetailBukuFragment();
            detailBukuFragment.setArguments(bundle);

            // Gunakan showFragment dari MainActivity
            if (ctx instanceof MainActivity) {
                ((MainActivity) ctx).showFragment(detailBukuFragment, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listBuku.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        ImageView sampulBuku;
        TextView txtIdBuku, txtJudulBuku, txtPenulisBuku, txtJumlahBuku, txtKategoriBuku;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            sampulBuku = itemView.findViewById(R.id.book_cover);
            txtIdBuku = itemView.findViewById(R.id.book_id);
            txtJudulBuku = itemView.findViewById(R.id.book_title);
            txtPenulisBuku = itemView.findViewById(R.id.book_author);
            txtJumlahBuku = itemView.findViewById(R.id.book_stock);
            txtKategoriBuku = itemView.findViewById(R.id.book_kategori);

        }
    }

    public void filterList(String query) {
        if (query.isEmpty()) {
            listBuku = new ArrayList<>(listBukuFull); // Reset ke data asli
        } else {
            List<DataModelBuku> filteredList = new ArrayList<>();
            for (DataModelBuku item : listBukuFull) {
                if (item.getJudul_buku().toLowerCase().contains(query.toLowerCase()) ||
                        item.getPenulis_buku().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            listBuku = filteredList;
        }
        notifyDataSetChanged(); // Memperbarui RecyclerView
    }

    public void filterByCategory(String category) {
        if (category.isEmpty()) {
            listBuku = new ArrayList<>(listBukuFull); // Reset ke data asli
        } else {
            List<DataModelBuku> filteredList = new ArrayList<>();
            for (DataModelBuku item : listBukuFull) {
                if (item.getKategori_buku().toLowerCase().contains(category.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            listBuku = filteredList;
        }
        notifyDataSetChanged(); // Memperbarui RecyclerView
    }
}