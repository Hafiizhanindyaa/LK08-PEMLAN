package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {

    public static final String FOLDER_DATA = "data";
    public static final String FILE_SISWA     = "siswa.txt";
    public static final String FILE_BUKU      = "buku.txt";
    public static final String FILE_PEGAWAI   = "pegawai.txt";
    public static final String FILE_TRANSAKSI = "transaksi.txt";
    public static final String PEMISAH = "|";

    private static String pathFile(String namaFile) {
        return FOLDER_DATA + File.separator + namaFile;
    }


    // Buat folder jika belum ada
    private static void buatFolderDataJikaBelumAda() {
        File folder = new File(FOLDER_DATA);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ArrayList
    public static ArrayList<String[]> bacaSemua(String namaFile) {
        ArrayList<String[]> hasilBaca = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathFile(namaFile)));
            String baris;
            while ((baris = reader.readLine()) != null) {
                baris = baris.trim();
                if (!baris.isEmpty()) {
                    hasilBaca.add(baris.split("\\|", -1));
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal membaca " + namaFile + ": " + e.getMessage());
        }
        return hasilBaca;
    }

    // Menambahkan baris
    public static void tambahBaris(String namaFile, String[] kolom) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile(namaFile), true));
            writer.write(gabungKolom(kolom));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan ke " + namaFile + ": " + e.getMessage());
        }
    }

    // Menuliskan ulang
    public static void tulisUlang(String namaFile, ArrayList<String[]> semuaData) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile(namaFile), false));
            for (String[] baris : semuaData) {
                writer.write(gabungKolom(baris));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan ke " + namaFile + ": " + e.getMessage());
        }
    }

    // Menggabungkan kolom
    private static String gabungKolom(String[] kolom) {
        String hasil = "";
        for (int i = 0; i < kolom.length; i++) {
            hasil = (i == 0) ? kolom[i] : hasil + PEMISAH + kolom[i];
        }
        return hasil;
    }

    // Cari pegawai
    public static String[] cariPegawai(String nip) {
        ArrayList<String[]> semuaPegawai = bacaSemua(FILE_PEGAWAI);
        for (String[] baris : semuaPegawai) {
            if (baris.length >= 4 && baris[0].equalsIgnoreCase(nip)) return baris;
        }
        return null;
    }

    // Cari siswa
    public static String[] cariSiswa(String nis) {
        ArrayList<String[]> semuaSiswa = bacaSemua(FILE_SISWA);
        for (String[] baris : semuaSiswa) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(nis)) return baris;
        }
        return null;
    }

    // Cari buku
    public static String[] cariBuku(String kodeBuku) {
        ArrayList<String[]> semuaBuku = bacaSemua(FILE_BUKU);
        for (String[] baris : semuaBuku) {
            if (baris.length >= 3 && baris[0].equalsIgnoreCase(kodeBuku)) return baris;
        }
        return null;
    }

    // Hitung Pinjaman Aktif
    public static int hitungPinjamanAktif(String nis) {
        ArrayList<String[]> semuaTransaksi = bacaSemua(FILE_TRANSAKSI);
        int jumlah = 0;
        for (String[] baris : semuaTransaksi) {
            if (baris.length >= 6 && baris[1].equalsIgnoreCase(nis) && baris[5].equals("0")) jumlah++;
        }
        return jumlah;
    }

    public static boolean apakahBukuDipinjam(String kodeBuku) {
        ArrayList<String[]> semuaTransaksi = bacaSemua(FILE_TRANSAKSI);
        for (String[] baris : semuaTransaksi) {
            if (baris.length >= 6 && baris[2].equalsIgnoreCase(kodeBuku) && baris[5].equals("0")) return true;
        }
        return false;
    }

    public static void setupAwal() {
        buatFolderDataJikaBelumAda();
        if (bacaSemua(FILE_PEGAWAI).isEmpty()) {
            tambahBaris(FILE_PEGAWAI, new String[]{"P001", "Admin Perpus", "01-01-1990", "admin123"});
        }
    }
}

