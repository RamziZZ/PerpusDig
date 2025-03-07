package com.example.perpusdig.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpusdig.R;
import com.example.perpusdig.model.DataModelPinjamEbook;

import java.util.List;

public class AdapterRiwayatSampulEbook extends RecyclerView.Adapter<AdapterRiwayatSampulEbook.HolderData> {
    private Context ctx;
    private List<DataModelPinjamEbook> sampulEbook;

    public AdapterRiwayatSampulEbook(Context ctx, List<DataModelPinjamEbook> sampulEbook) {
        this.ctx = ctx;
        this.sampulEbook = sampulEbook;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_riwayat_ebook, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataModelPinjamEbook dm = sampulEbook.get(position);

        // Dekode Base64 ke Bitmap dan tampilkan di ImageView
        String sampulEbook = dm.getSampul();
        if (sampulEbook != null) {
            byte[] decodedString = Base64.decode(sampulEbook, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sampulEbook.setImageBitmap(decodedByte);
        }
    }

    @Override
    public int getItemCount() {
        return sampulEbook.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        ImageView sampulEbook;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            sampulEbook = itemView.findViewById(R.id.book_cover);
        }
    }
}
