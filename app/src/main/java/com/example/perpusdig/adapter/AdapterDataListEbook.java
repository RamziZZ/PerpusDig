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
import com.example.perpusdig.model.DataModelEbook;
import com.example.perpusdig.ui.beranda.DetailEbookFragment;

import java.util.ArrayList;
import java.util.List;

public class AdapterDataListEbook extends RecyclerView.Adapter<AdapterDataListEbook.HolderData>{
    private Context ctx;
    private List<DataModelEbook> listEbook;
    private final List<DataModelEbook> listEbookFull; // Salinan data asli

    public AdapterDataListEbook(Context ctx, List<DataModelEbook> listEbook){
        this.ctx = ctx;
        this.listEbook = new ArrayList<>(listEbook); // Salin data
        this.listEbookFull = new ArrayList<>(listEbook); // Salin data asli untuk filter
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ebook, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModelEbook dm = listEbook.get((position));

        // Dekode Base64 ke Bitmap dan tampilkan di ImageView
        String sampulBukuBase64 = dm.getSampul();
        if (sampulBukuBase64 != null) {
            byte[] decodedString = Base64.decode(sampulBukuBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sampulEbook.setImageBitmap(decodedByte);  // Menampilkan gambar di ImageView
        }

        holder.txtIdEbook.setText(String.valueOf(dm.getId_ebook()));
        holder.txtJudulEbook.setText(dm.getJudul());
        holder.txtPenulisEbook.setText(dm.getPenulis());
        holder.txtKategoriEbook.setText(dm.getKategori());

        // OnClickListener pada item untuk membuka DetailEbookFragment
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_ebook", dm.getId_ebook());

            // Buat fragment baru dan tambahkan bundle
            DetailEbookFragment detailEbookFragment = new DetailEbookFragment();
            detailEbookFragment.setArguments(bundle);

            // Panggil showFragment melalui MainActivity
            if (ctx instanceof MainActivity) {
                ((MainActivity) ctx).showFragment(detailEbookFragment, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listEbook.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        ImageView sampulEbook;
        TextView txtIdEbook, txtJudulEbook, txtPenulisEbook, txtKategoriEbook;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            sampulEbook = itemView.findViewById(R.id.book_cover);
            txtIdEbook = itemView.findViewById(R.id.book_id);
            txtJudulEbook = itemView.findViewById(R.id.book_title);
            txtPenulisEbook = itemView.findViewById(R.id.book_author);
            txtKategoriEbook = itemView.findViewById(R.id.book_kategori);
        }
    }

    public void filterList(String query) {
        if (query.isEmpty()) {
            listEbook = new ArrayList<>(listEbookFull); // Reset ke data asli
        } else {
            List<DataModelEbook> filteredList = new ArrayList<>();
            for (DataModelEbook item : listEbookFull) {
                if (item.getJudul().toLowerCase().contains(query.toLowerCase()) ||
                        item.getPenulis().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            listEbook = filteredList;
        }
        notifyDataSetChanged(); // Memperbarui RecyclerView
    }

    public void filterByCategory(String category) {
        if (category.isEmpty()) {
            listEbook = new ArrayList<>(listEbookFull); // Reset ke data asli
        } else {
            List<DataModelEbook> filteredList = new ArrayList<>();
            for (DataModelEbook item : listEbookFull) {
                if (item.getKategori().toLowerCase().contains(category.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            listEbook = filteredList;
        }
        notifyDataSetChanged(); // Memperbarui RecyclerView
    }
}