package com.example.perpusdig.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpusdig.R;
import com.example.perpusdig.model.DataModelPinjamEbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterRiwayatEbook extends RecyclerView.Adapter<AdapterRiwayatEbook.HolderData> {

    private final Context ctx;
    private final List<DataModelPinjamEbook> riwayatEbook;

    public AdapterRiwayatEbook(Context ctx, List<DataModelPinjamEbook> riwayatEbook) {
        this.ctx = ctx;
        this.riwayatEbook = riwayatEbook;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_riwayat, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        // Ambil dua buku per baris
        DataModelPinjamEbook book1 = riwayatEbook.get(position * 2); // Buku pertama
        DataModelPinjamEbook book2 = (position * 2 + 1 < riwayatEbook.size()) ? riwayatEbook.get(position * 2 + 1) : null; // Buku kedua (jika ada)

        // Bind data buku pertama
        holder.txtJudul1.setText(book1.getJudul());
        holder.txtPenulis1.setText(book1.getPenulis());
        String formattedDate1 = formatTanggal(book1.getTanggal_peminjaman());
        holder.txtTanggal1.setText(formattedDate1);

        // Decode Base64 untuk gambar buku pertama dan tampilkan di ImageView
        String sampulEbook1 = book1.getSampul();
        if (sampulEbook1 != null) {
            byte[] decodedString = android.util.Base64.decode(sampulEbook1, android.util.Base64.DEFAULT);
            android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sampulEbook1.setImageBitmap(decodedByte);
        }

        // Bind data buku kedua jika ada
        if (book2 != null) {
            holder.txtJudul2.setText(book2.getJudul());
            holder.txtPenulis2.setText(book2.getPenulis());
            String formattedDate2 = formatTanggal(book2.getTanggal_peminjaman());
            holder.txtTanggal2.setText(formattedDate2);

            // Decode Base64 untuk gambar buku kedua dan tampilkan di ImageView
            String sampulEbook2 = book2.getSampul();
            if (sampulEbook2 != null) {
                byte[] decodedString = android.util.Base64.decode(sampulEbook2, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.sampulEbook2.setImageBitmap(decodedByte);
            }

            holder.card1.setVisibility(View.VISIBLE);
        } else {
            // Jika tidak ada buku kedua, sembunyikan elemen terkait buku kedua
            holder.card2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        // Jumlah baris adalah jumlah buku dibagi 2 (dibulatkan ke atas)
        return (int) Math.ceil(riwayatEbook.size() / 2.0);
    }

    public int getJumlahEbook() {
        return riwayatEbook.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        // Elemen untuk buku pertama
        CardView card1;
        ImageView sampulEbook1;
        TextView txtJudul1, txtPenulis1, txtTanggal1;

        // Elemen untuk buku kedua
        CardView card2;
        ImageView sampulEbook2;
        TextView txtJudul2, txtPenulis2, txtTanggal2;

        public HolderData(View itemView) {
            super(itemView);
            card1 = itemView.findViewById(R.id.card1);
            sampulEbook1 = itemView.findViewById(R.id.book_cover);
            txtJudul1 = itemView.findViewById(R.id.book_title);
            txtPenulis1 = itemView.findViewById(R.id.book_author);
            txtTanggal1 = itemView.findViewById(R.id.book_date);

            card2 = itemView.findViewById(R.id.card2);
            sampulEbook2 = itemView.findViewById(R.id.book_cover2);
            txtJudul2 = itemView.findViewById(R.id.book_title2);
            txtPenulis2 = itemView.findViewById(R.id.book_author2);
            txtTanggal2 = itemView.findViewById(R.id.book_date2);
        }
    }

    private String formatTanggal(String tanggalAsli) {
        try {
            // Format tanggal dari API (format asal)
            SimpleDateFormat sdfAsal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Format tanggal yang diinginkan
            SimpleDateFormat sdfTujuan = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            // Parsing dan format ulang
            Date tanggal = sdfAsal.parse(tanggalAsli);
            return sdfTujuan.format(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
            // Jika parsing gagal, kembalikan tanggal asli
            return tanggalAsli;
        }
    }
}