package panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.FileHelper;
import src.Transaksi;
import src.Siswa;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class PanelLaporan extends JPanel {

    private JPanel areaLaporan;
    private JTable tabel;
    private DefaultTableModel model;

    public PanelLaporan() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initComponents();
    }

    private void initComponents() {
        add(MainFrame.buatHeader("Laporan", "Laporan statistik dan kondisi peminjaman"), BorderLayout.NORTH);

        // Tab buttons laporan
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        tabPanel.setBackground(new Color(248, 250, 252));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        JButton btnBelumKembali = MainFrame.buatTombol("Belum Dikembalikan",  new Color(239, 68, 68));
        JButton btnJatuhTempo   = MainFrame.buatTombol("(!) Jatuh Tempo",     new Color(245, 158, 11));
        JButton btnPerSiswa     = MainFrame.buatTombol("Riwayat per Siswa",   new Color(99, 102, 241));
        JButton btnPopuler      = MainFrame.buatTombol("Buku Terpopuler",     new Color(34, 197, 94));

        tabPanel.add(btnBelumKembali); tabPanel.add(btnJatuhTempo);
        tabPanel.add(btnPerSiswa);     tabPanel.add(btnPopuler);

        // Area isi laporan
        areaLaporan = new JPanel(new BorderLayout());
        areaLaporan.setBackground(new Color(248, 250, 252));
        areaLaporan.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JLabel lblHint = new JLabel("Pilih jenis laporan di atas.", SwingConstants.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 14));
        lblHint.setForeground(new Color(100, 116, 139));
        areaLaporan.add(lblHint, BorderLayout.CENTER);

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setBackground(new Color(248, 250, 252));
        tengah.add(tabPanel,    BorderLayout.NORTH);
        tengah.add(areaLaporan, BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        btnBelumKembali.addActionListener(e -> tampilLaporanBelumKembali());
        btnJatuhTempo.addActionListener(e -> tampilLaporanJatuhTempo());
        btnPerSiswa.addActionListener(e -> tampilLaporanPerSiswa());
        btnPopuler.addActionListener(e -> tampilLaporanPopuler());
    }

    private void tampilTabel(String[] kolom, Object[][] data, String ringkasan) {
        areaLaporan.removeAll();
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Object[] row : data) model.addRow(row);

        tabel = new JTable(model);
        MainFrame.styleTable(tabel);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JLabel lblRing = new JLabel(ringkasan);
        lblRing.setFont(new Font("Arial", Font.BOLD, 13));
        lblRing.setForeground(new Color(15, 23, 42));
        lblRing.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        areaLaporan.add(scroll,   BorderLayout.CENTER);
        areaLaporan.add(lblRing,  BorderLayout.SOUTH);
        areaLaporan.revalidate();
        areaLaporan.repaint();
    }

    private void tampilLaporanBelumKembali() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        ArrayList<Object[]> rows = new ArrayList<>();
        for (String[] b : semuaData) {
            if (b.length >= 6 && b[5].equals("0")) {
                String[] rawBuku = FileHelper.cariBuku(b[2]);
                String judul = (rawBuku != null) ? rawBuku[1] : "-";
                rows.add(new Object[]{b[1], b[2], judul, b[4]});
            }
        }
        tampilTabel(new String[]{"NIS", "Kode Buku", "Judul Buku", "Jatuh Tempo"},
            rows.toArray(new Object[0][]), "Total: " + rows.size() + " buku belum dikembalikan.");
    }

    private void tampilLaporanJatuhTempo() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        LocalDate hariIni = LocalDate.now();
        ArrayList<Object[]> rows = new ArrayList<>();
        long totalDenda = 0;
        for (String[] b : semuaData) {
            if (b.length >= 6 && b[5].equals("0")) {
                LocalDate tempo = LocalDate.parse(b[4], Transaksi.FORMAT_TANGGAL);
                long terlambat  = ChronoUnit.DAYS.between(tempo, hariIni);
                if (terlambat > 0) {
                    long denda = terlambat * Transaksi.DENDA_PER_HARI;
                    totalDenda += denda;
                    String[] rawSiswa = FileHelper.cariSiswa(b[1]);
                    String nama = (rawSiswa != null) ? rawSiswa[1] : "-";
                    rows.add(new Object[]{b[1], nama, b[2], b[4], terlambat + " hari", "Rp " + denda});
                }
            }
        }
        tampilTabel(new String[]{"NIS", "Nama Siswa", "Kode Buku", "Jatuh Tempo", "Terlambat", "Denda"},
            rows.toArray(new Object[0][]),
            rows.size() + " peminjam terlambat | Total Denda: Rp " + totalDenda);
    }

    private void tampilLaporanPerSiswa() {
        String nis = JOptionPane.showInputDialog(this, "Masukkan NIS Siswa:", "Riwayat per Siswa", JOptionPane.QUESTION_MESSAGE);
        if (nis == null || nis.trim().isEmpty()) return;
        nis = nis.trim().toUpperCase();

        String[] rawSiswa = FileHelper.cariSiswa(nis);
        if (rawSiswa == null) { JOptionPane.showMessageDialog(this, "Siswa tidak ditemukan.", "Gagal", JOptionPane.ERROR_MESSAGE); return; }
        Siswa siswa = Siswa.fromArray(rawSiswa);

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        ArrayList<Object[]> rows = new ArrayList<>();
        int sudahKembali = 0;
        for (String[] b : semuaData) {
            if (b.length >= 6 && b[1].equalsIgnoreCase(nis)) {
                String[] rawBuku = FileHelper.cariBuku(b[2]);
                String judul = (rawBuku != null) ? rawBuku[1] : "-";
                String status = b[5].equals("0") ? "Belum Kembali" : "Sudah Kembali";
                if (b[5].equals("1")) sudahKembali++;
                rows.add(new Object[]{b[0], judul, b[3], b[4], status});
            }
        }
        int total = rows.size();
        tampilTabel(new String[]{"Kode Transaksi", "Judul Buku", "Tgl Pinjam", "Jatuh Tempo", "Status"},
            rows.toArray(new Object[0][]),
            "Riwayat " + siswa.getNama() + " | Total: " + total + " | Selesai: " + sudahKembali + " | Aktif: " + (total - sudahKembali));
    }

    private void tampilLaporanPopuler() {
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        if (semuaData.isEmpty()) { JOptionPane.showMessageDialog(this, "Belum ada data transaksi.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        ArrayList<String> daftarKode = new ArrayList<>();
        for (String[] b : semuaData) {
            if (b.length >= 3) {
                boolean ada = false;
                for (String k : daftarKode) { if (k.equalsIgnoreCase(b[2])) { ada = true; break; } }
                if (!ada) daftarKode.add(b[2]);
            }
        }
        int[] jumlah = new int[daftarKode.size()];
        for (String[] b : semuaData) {
            if (b.length >= 3) {
                for (int i = 0; i < daftarKode.size(); i++) {
                    if (daftarKode.get(i).equalsIgnoreCase(b[2])) { jumlah[i]++; break; }
                }
            }
        }
        // Bubble sort descending
        for (int i = 0; i < daftarKode.size() - 1; i++) {
            for (int j = 0; j < daftarKode.size() - 1 - i; j++) {
                if (jumlah[j] < jumlah[j + 1]) {
                    int tmpJ = jumlah[j]; jumlah[j] = jumlah[j+1]; jumlah[j+1] = tmpJ;
                    String tmpK = daftarKode.get(j); daftarKode.set(j, daftarKode.get(j+1)); daftarKode.set(j+1, tmpK);
                }
            }
        }
        ArrayList<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < daftarKode.size(); i++) {
            String[] rawBuku = FileHelper.cariBuku(daftarKode.get(i));
            String judul = (rawBuku != null) ? rawBuku[1] : "-";
            rows.add(new Object[]{i + 1, daftarKode.get(i), judul, jumlah[i] + " kali"});
        }
        tampilTabel(new String[]{"Rank", "Kode Buku", "Judul Buku", "Total Dipinjam"},
            rows.toArray(new Object[0][]), "Top buku berdasarkan total peminjaman.");
    }
}