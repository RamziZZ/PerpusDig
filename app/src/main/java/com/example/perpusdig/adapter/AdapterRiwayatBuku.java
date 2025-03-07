package com.example.perpusdig.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpusdig.R;
import com.example.perpusdig.api.ApiClient;
import com.example.perpusdig.api.ApiRequestData;
import com.example.perpusdig.model.DataModelPinjamBuku;
import com.example.perpusdig.model.ResponseModelPinjamBuku;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRiwayatBuku extends RecyclerView.Adapter<AdapterRiwayatBuku.HolderData>{
    private Context ctx;
    private List<DataModelPinjamBuku> riwayatBuku;

    public AdapterRiwayatBuku(Context ctx, List<DataModelPinjamBuku> riwayatBuku) {
        this.ctx = ctx;
        this.riwayatBuku = riwayatBuku; // Salin data
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_aktivitas, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRiwayatBuku.HolderData holder, int position) {
        DataModelPinjamBuku dm = riwayatBuku.get(position);

        // Dekode Base64 ke Bitmap dan tampilkan di ImageView
        String sampulBuku = dm.getSampul_buku();
        if (sampulBuku != null) {
            byte[] decodedString = Base64.decode(sampulBuku, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sampulBuku.setImageBitmap(decodedByte);  // Menampilkan gambar di ImageView
        }

        holder.txtIdPeminjaman.setText(String.valueOf(dm.getId_peminjaman()));
        holder.txtJudulBuku.setText(dm.getJudul());
        holder.txtPenulisBuku.setText(dm.getPenulis_buku());
        String formattedDate1 = formatTanggal(dm.getTanggal_peminjaman());
        holder.txtTanggalPinjam.setText(formattedDate1);

        // Cek nilai tanggal pengembalian
        String tanggalPengembalian = dm.getTanggal_pengembalian();
        if (tanggalPengembalian.equals("0000-00-00")) {
            holder.txtTanggalKembali.setText("-");
        } else {
            String formattedTanggalKembali = formatTanggal(tanggalPengembalian);
            holder.txtTanggalKembali.setText(formattedTanggalKembali);
        }

        // Mengatur teks dan warna status peminjaman
        String statusPeminjaman = dm.getStatus_peminjaman();
        switch (statusPeminjaman) {
            case "Ditunda":
                holder.txtStatus.setText("Menunggu Konfirmasi");
                holder.txtStatus.setTextColor(Color.parseColor("#FFA500")); // Warna Oranye
                holder.btnOpsi.setEnabled(true);
                break;

            case "Disetujui":
                holder.txtStatus.setText("Disetujui");
                holder.txtStatus.setTextColor(Color.parseColor("#008000")); // Warna Hijau
                holder.btnOpsi.setEnabled(false);
                break;

            case "Ditolak":
                holder.txtStatus.setText("Ditolak");
                holder.txtStatus.setTextColor(Color.parseColor("#FF0000")); // Warna Merah
                holder.btnOpsi.setEnabled(false);
                break;

            case "Dipinjam":
                holder.txtStatus.setText("Sedang Dipinjam");
                holder.txtStatus.setTextColor(Color.parseColor("#1450A3")); // Warna Biru
                holder.btnOpsi.setEnabled(false);
                break;

            case "Selesai":
                holder.txtStatus.setText("Selesai");
                holder.txtStatus.setTextColor(Color.parseColor("#808080")); // Warna Abu-abu
                holder.btnOpsi.setEnabled(false);
                break;
        }

        // Listener untuk klik button opsi pengajuan
        holder.btnOpsi.setOnClickListener(view -> {
            // Menampilkan popup menu
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.opsi_pengajuan, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_batalkan) {
                    // Tampilkan dialog konfirmasi
                    showConfirmationDialog(view.getContext(), "Batalkan Pengajuan", "Apakah Anda yakin ingin membatalkan pengajuan?", () -> {
                        String idPeminjaman = dm.getId_peminjaman();
                        hapusRiwayatBuku(idPeminjaman);  // Panggil metode hapus riwayat buku
                        Toast.makeText(view.getContext(), "Pengajuan dibatalkan!", Toast.LENGTH_SHORT).show();
                    });
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return riwayatBuku.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        ImageView sampulBuku;
        TextView txtIdPeminjaman, txtStatus, txtJudulBuku, txtPenulisBuku, txtTanggalPinjam, txtTanggalKembali;
        Button btnOpsi;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            sampulBuku = itemView.findViewById(R.id.book_cover);
            txtIdPeminjaman = itemView.findViewById(R.id.book_id);
            txtStatus = itemView.findViewById(R.id.book_status);
            txtJudulBuku = itemView.findViewById(R.id.book_title);
            txtPenulisBuku = itemView.findViewById(R.id.book_author);
            txtTanggalPinjam = itemView.findViewById(R.id.book_datepinjam);
            txtTanggalKembali = itemView.findViewById(R.id.book_datekembali);
            btnOpsi = itemView.findViewById(R.id.book_opsi);

        }
    }

    // Metode untuk menghapus riwayat buku
    public void hapusRiwayatBuku(String idPeminjaman) {
        // Membuat objek API dan menerima data menggunakan Retrofit
        ApiRequestData ardData = ApiClient.getRetrofitInstance().create(ApiRequestData.class);
        Call<ResponseModelPinjamBuku> hapusRiwayat = ardData.ardGetHapusRiwayat(idPeminjaman);

        hapusRiwayat.enqueue(new Callback<ResponseModelPinjamBuku>() {
            @Override
            public void onResponse(Call<ResponseModelPinjamBuku> call, Response<ResponseModelPinjamBuku> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int kode = response.body().getKode();

                    // Cek apakah penghapusan berhasil
                    if (kode == 1) {
                        // Jika berhasil, perbarui tampilan data riwayat buku
                        riwayatBuku.removeIf(buku -> buku.getId_peminjaman().equals(idPeminjaman));
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModelPinjamBuku> call, Throwable t) {
                Toast.makeText(ctx, "Gagal Menghubungi Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk menampilkan dialog konfirmasi
    private void showConfirmationDialog(Context context, String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Membuat TextView untuk judul
        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setPadding(40, 40, 40, 20); // Padding untuk judul
        titleView.setTextSize(20); // Ukuran teks
        titleView.setTextColor(Color.BLACK); // Warna teks
        titleView.setTypeface(Typeface.DEFAULT_BOLD); // Teks tebal
        titleView.setGravity(Gravity.CENTER); // Teks di tengah

        builder.setCustomTitle(titleView) // Set judul kustom
                .setMessage(message)
                .setPositiveButton("Ya", (dialogInterface, i) -> {
                    // Aksi ketika tombol "Ya" ditekan
                    onConfirm.run();
                })
                .setNegativeButton("Tidak", (dialogInterface, i) -> {
                    // Tutup dialog ketika "Tidak" ditekan
                    dialogInterface.dismiss();
                });

        // Membuat dialog
        AlertDialog dialog = builder.create();

        // Mengubah background dialog menjadi putih
        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE)); // Latar putih
            }

            // Pesan
            TextView messageView = dialog.findViewById(android.R.id.message);
            if (messageView != null) {
                messageView.setTextColor(Color.BLACK); // Warna teks pesan hitam
            }

            // Tombol
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });

        // Tampilkan dialog
        dialog.show();
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
