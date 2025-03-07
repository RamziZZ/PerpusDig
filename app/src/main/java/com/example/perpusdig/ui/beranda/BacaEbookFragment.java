package com.example.perpusdig.ui.beranda;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.perpusdig.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BacaEbookFragment extends Fragment {

    private static final String ARG_PDF_BASE64 = "pdf_base64";

    // Metode untuk membuat instance fragment dengan Base64 PDF sebagai argumen
    public static BacaEbookFragment newInstance(String pdfBase64) {
        BacaEbookFragment fragment = new BacaEbookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_BASE64, pdfBase64);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ebook_baca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PDFView pdfView = view.findViewById(R.id.pdfView);

        if (getArguments() != null && getArguments().containsKey(ARG_PDF_BASE64)) {
            String pdfBase64 = getArguments().getString(ARG_PDF_BASE64);

            if (pdfBase64 != null && !pdfBase64.isEmpty()) {
                byte[] pdfBytes = Base64.decode(pdfBase64, Base64.DEFAULT);

                try {
                    // Simpan PDF ke cache
                    File pdfFile = new File(requireContext().getCacheDir(), "ebook.pdf");
                    FileOutputStream fos = new FileOutputStream(pdfFile);
                    fos.write(pdfBytes);
                    fos.close();

                    // Tampilkan PDF di PDFView
                    pdfView.fromFile(pdfFile)
                            .load();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}