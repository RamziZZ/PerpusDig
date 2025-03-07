package com.example.perpusdig;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.perpusdig.databinding.ActivityMainBinding;
import com.example.perpusdig.ui.login.LoginFragment;
import com.example.perpusdig.ui.openingpage.OpeningFragment;
import com.example.perpusdig.ui.beranda.BerandaFragment;
import com.example.perpusdig.ui.openingpage.OpeningFragment1;
import com.example.perpusdig.ui.openingpage.OpeningFragment2;
import com.example.perpusdig.ui.perpustakaan.PerpustakaanAnggotaFragment;
import com.example.perpusdig.ui.perpustakaan.PerpustakaanNonAnggotaFragment;
import com.example.perpusdig.ui.aktivitas.AktivitasAnggotaFragment;
import com.example.perpusdig.ui.aktivitas.AktivitasNonAnggotaFragment;
import com.example.perpusdig.ui.profil.ProfilAnggotaFragment;
import com.example.perpusdig.ui.profil.ProfilNonAnggotaFragment;
import com.example.perpusdig.ui.regis_anggota.WaitingRegisFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferencess sharedPreferencesManager;
    private boolean doubleBackToExitPressedOnce = false; // Variabel untuk keluar aplikasi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferencesManager = new SharedPreferencess(this);

        // Cek koneksi jaringan
        if (!isNetworkAvailable()) {
            showNoNetworkDialog();
        }

        // Periksa status login
        if (isLoggedIn()) {
            // Jika sudah login, arahkan ke BerandaFragment dan tampilkan BottomNavigationView
            showFragment(new BerandaFragment(), false);
        } else {
            // Jika belum login, tampilkan OpeningFragment dan sembunyikan BottomNavigationView
            showFragment(new OpeningFragment(), false);
        }

        // Listener untuk perubahan back stack fragment
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            // Periksa apakah fragment saat ini memerlukan navbar
            if (currentFragment instanceof BerandaFragment
                    || currentFragment instanceof PerpustakaanAnggotaFragment
                    || currentFragment instanceof AktivitasAnggotaFragment
                    || currentFragment instanceof ProfilAnggotaFragment
                    || currentFragment instanceof PerpustakaanNonAnggotaFragment
                    || currentFragment instanceof AktivitasNonAnggotaFragment
                    || currentFragment instanceof ProfilNonAnggotaFragment) {
                // Tampilkan navbar
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                // Sembunyikan navbar untuk fragment lainnya
                binding.bottomNavigationView.setVisibility(View.GONE);
            }
        });

        setupBottomNavigationView();
    }

    // Metode untuk memeriksa apakah user sudah login
    private boolean isLoggedIn() {
        return sharedPreferencesManager.isLoggedIn();
    }

    // Metode untuk memeriksa apakah user adalah anggota
    private boolean isUserAnggota() {
        int idUser = sharedPreferencesManager.getIdUser();
        String nikAnggota = sharedPreferencesManager.getNikAnggota();
        return idUser != -1 && !nikAnggota.isEmpty();
    }

    // Metode untuk navbar
    private void setupBottomNavigationView() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.beranda) {
                selectedFragment = new BerandaFragment();
            } else if (item.getItemId() == R.id.library) {
                if (isUserAnggota()) {
                    selectedFragment = new PerpustakaanAnggotaFragment();
                } else {
                    selectedFragment = new PerpustakaanNonAnggotaFragment();
                }
            } else if (item.getItemId() == R.id.activity) {
                if (isUserAnggota()) {
                    selectedFragment = new AktivitasAnggotaFragment();
                } else {
                    selectedFragment = new AktivitasNonAnggotaFragment();
                }
            } else if (item.getItemId() == R.id.profile) {
                if (isUserAnggota()) {
                    selectedFragment = new ProfilAnggotaFragment();
                } else {
                    selectedFragment = new ProfilNonAnggotaFragment();
                }
            }

            if (selectedFragment != null) {
                showFragment(selectedFragment, false);
                return true;
            }
            return false;
        });
    }

    public void showFragment(Fragment fragment, boolean useCustomAnimation) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (useCustomAnimation) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,  // Animasi masuk
                    R.anim.slide_out_left, // Animasi keluar
                    R.anim.slide_in_left,  // Animasi masuk saat kembali
                    R.anim.slide_out_right // Animasi keluar saat kembali
            );
        }

        fragmentTransaction.replace(R.id.fragment_container, fragment); // Ganti fragment
        fragmentTransaction.addToBackStack(null); // Tambahkan ke back stack
        fragmentTransaction.commit(); // Komit transaksi
    }

    public void updateBottomNavigationSelection(Fragment fragment) {
        if (fragment instanceof BerandaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.beranda);
        } else if (fragment instanceof PerpustakaanAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.library);
        } else if (fragment instanceof AktivitasAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.activity);
        } else if (fragment instanceof ProfilAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        } else if (fragment instanceof PerpustakaanNonAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.library);
        } else if (fragment instanceof AktivitasNonAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.activity);
        } else if (fragment instanceof ProfilNonAnggotaFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        }
    }

    // Metode saat tombol back ditekan
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof LoginFragment) {
            showFragment(new OpeningFragment2(), false);

        } else if (currentFragment instanceof OpeningFragment2) {
            showFragment(new OpeningFragment1(), false);

        } else if (currentFragment instanceof OpeningFragment1) {
            finish();

        } else if (currentFragment instanceof BerandaFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan dua kali untuk keluar dari aplikasi", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1000);

        } else if (currentFragment instanceof WaitingRegisFragment) {
            showFragment(new ProfilNonAnggotaFragment(), false);

        } else {
            // Pop back stack untuk kembali ke fragment sebelumnya
            if (getSupportFragmentManager().popBackStackImmediate()) {  // Periksa jika popBackStack berhasil
                Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                updateBottomNavigationSelection(activeFragment);
            }
        }
    }

    // Metode untuk memeriksa apakah jaringan tersedia
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // Metode untuk menampilkan dialog jika jaringan tidak tersedia
    private void showNoNetworkDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Jaringan Tidak Tersedia")
                .setMessage("Tidak ada koneksi internet. Harap periksa jaringan Anda.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}