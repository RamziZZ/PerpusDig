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

import java.util.List;

public class AdapterDataTrendingEbook extends RecyclerView.Adapter<AdapterDataTrendingEbook.HolderData>{
    private Context ctx;
    private List<DataModelEbook> trendingEbook;

    public AdapterDataTrendingEbook(Context ctx, List<DataModelEbook> listEbook) {
        this.ctx = ctx;
        this.trendingEbook = listEbook; // Salin data
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trending, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModelEbook dm = trendingEbook.get((position));

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
        holder.txtPenerbitEbook.setText(dm.getPenerbit());
        holder.txtSinopsisEbook.setText(dm.getSinopsis() + "...");

        /// OnClickListener pada item untuk membuka DetailEbookFragment
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
        return trendingEbook.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        ImageView sampulEbook;
        TextView txtIdEbook, txtJudulEbook, txtPenulisEbook, txtPenerbitEbook, txtSinopsisEbook;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            sampulEbook = itemView.findViewById(R.id.sampul_ebook);
            txtIdEbook = itemView.findViewById(R.id.id_ebook);
            txtJudulEbook = itemView.findViewById(R.id.judul_ebook);
            txtPenulisEbook = itemView.findViewById(R.id.penulis_ebook);
            txtPenerbitEbook = itemView.findViewById(R.id.penerbit_ebook);
            txtSinopsisEbook = itemView.findViewById(R.id.sinopsis_ebook);

        }
    }
}