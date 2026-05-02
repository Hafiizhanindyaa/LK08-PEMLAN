package panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.Buku;
import src.FileHelper;
import src.Siswa;
import src.Transaksi;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class PanelTransaksi extends JPanel {

    private JTable tabel;
    private DefaultTableModel model;

    public PanelTransaksi() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initComponents();
        muatData();
    }

    private void initComponents() {
        add(MainFrame.buatHeader("Transaksi Peminjaman", "Catat peminjaman dan pengembalian buku"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(new Color(248, 250, 252));
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JButton btnPinjam   = MainFrame.buatTombol("+ Pinjam Buku",        new Color(34, 197, 94));
        JButton btnKembali  = MainFrame.buatTombol("Kembalikan",            new Color(59, 130, 246));
        JButton btnCekSiswa = MainFrame.buatTombol("Cek Pinjaman Siswa",   new Color(99, 102, 241));
        JButton btnRefresh  = MainFrame.buatTombol("Refresh",               new Color(100, 116, 139));
        toolbar.add(btnPinjam); toolbar.add(btnKembali); toolbar.add(btnCekSiswa); toolbar.add(btnRefresh);

        String[] kolom = {"Kode Transaksi", "NIS", "Kode Buku", "Tgl Pinjam", "Jatuh Tempo", "Status"};
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        MainFrame.styleTable(tabel);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(110);

        // Warnai kolom Status
        tabel.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Dipinjam".equals(val)) {
                    setForeground(new Color(239, 68, 68));
                } else {
                    setForeground(new Color(34, 197, 94));
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 25, 25, 25),
            BorderFactory.createLineBorder(new Color(226, 232, 240))));

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setBackground(new Color(248, 250, 252));
        tengah.add(toolbar, BorderLayout.NORTH);
        tengah.add(scroll,  BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        btnPinjam.addActionListener(e -> dialogPinjam());
        btnKembali.addActionListener(e -> dialogKembali());
        btnCekSiswa.addActionListener(e -> dialogCekSiswa());
        btnRefresh.addActionListener(e -> muatData());
    }

    private void muatData() {
        model.setRowCount(0);
        ArrayList<String[]> data = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        for (String[] b : data) {
            if (b.length >= 6) {
                String status = b[5].equals("0") ? "Dipinjam" : "Dikembalikan";
                model.addRow(new Object[]{b[0], b[1], b[2], b[3], b[4], status});
            }
        }
    }

    private void dialogPinjam() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pinjam Buku", true);
        dialog.setSize(450, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtKodeTrx   = MainFrame.buatTextField(18);
        JTextField txtNIS        = MainFrame.buatTextField(18);
        JTextField txtKodeBuku   = MainFrame.buatTextField(18);
        JTextField txtTglPinjam  = MainFrame.buatTextField(18);
        JTextField txtTglTempo   = MainFrame.buatTextField(18);
        txtTglPinjam.setText(LocalDate.now().format(Transaksi.FORMAT_TANGGAL));

        String[] labels    = {"Kode Transaksi :", "NIS Siswa :", "Kode Buku :", "Tgl Pinjam (dd-MM-yyyy) :", "Jatuh Tempo (dd-MM-yyyy) :"};
        JTextField[] fields = {txtKodeTrx, txtNIS, txtKodeBuku, txtTglPinjam, txtTglTempo};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.4;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.6;
            form.add(fields[i], gbc);
        }

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        tombol.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton btnBatal  = MainFrame.buatTombol("Batal",  new Color(100, 116, 139));
        JButton btnSimpan = MainFrame.buatTombol("Simpan", new Color(34, 197, 94));
        tombol.add(btnBatal); tombol.add(btnSimpan);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(tombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            String kodeTrx   = txtKodeTrx.getText().trim().toUpperCase();
            String nis        = txtNIS.getText().trim().toUpperCase();
            String kodeBuku   = txtKodeBuku.getText().trim().toUpperCase();
            String strPinjam  = txtTglPinjam.getText().trim();
            String strTempo   = txtTglTempo.getText().trim();

            if (kodeTrx.isEmpty() || nis.isEmpty() || kodeBuku.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
            }
            // Cek kode transaksi unik
            for (String[] b : FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI)) {
                if (b.length >= 1 && b[0].equalsIgnoreCase(kodeTrx)) {
                    JOptionPane.showMessageDialog(dialog, "Kode transaksi sudah digunakan.", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
                }
            }

            String[] rawSiswa = FileHelper.cariSiswa(nis);
            if (rawSiswa == null) { JOptionPane.showMessageDialog(dialog, "Siswa tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
            Siswa siswa = Siswa.fromArray(rawSiswa);

            if (FileHelper.hitungPinjamanAktif(nis) >= 2) { JOptionPane.showMessageDialog(dialog, siswa.getNama() + " sudah meminjam 2 buku.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }

            String[] rawBuku = FileHelper.cariBuku(kodeBuku);
            if (rawBuku == null) { JOptionPane.showMessageDialog(dialog, "Buku tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
            Buku buku = Buku.fromArray(rawBuku);

            if (FileHelper.apakahBukuDipinjam(kodeBuku)) { JOptionPane.showMessageDialog(dialog, "Buku sedang dipinjam orang lain.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }

            LocalDate tglPinjam, tglTempo;
            try { tglPinjam = LocalDate.parse(strPinjam, Transaksi.FORMAT_TANGGAL); } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Format tanggal pinjam salah (dd-MM-yyyy).", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
            try { tglTempo  = LocalDate.parse(strTempo,  Transaksi.FORMAT_TANGGAL); } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Format jatuh tempo salah (dd-MM-yyyy).", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
            if (!tglTempo.isAfter(tglPinjam)) { JOptionPane.showMessageDialog(dialog, "Jatuh tempo harus setelah tanggal pinjam.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }

            Transaksi trx = new Transaksi(kodeTrx, nis, kodeBuku,
                tglPinjam.format(Transaksi.FORMAT_TANGGAL),
                tglTempo.format(Transaksi.FORMAT_TANGGAL), "0");
            FileHelper.tambahBaris(FileHelper.FILE_TRANSAKSI, trx.toArray());

            int sisaKuota = 2 - FileHelper.hitungPinjamanAktif(nis);
            JOptionPane.showMessageDialog(dialog,
                "Peminjaman berhasil!\n\n" +
                "Nama Siswa : " + siswa.getNama() + "\n" +
                "Buku       : " + buku.getJudul() + "\n" +
                "Pinjam     : " + tglPinjam.format(Transaksi.FORMAT_TANGGAL) + "\n" +
                "Jatuh Tempo: " + tglTempo.format(Transaksi.FORMAT_TANGGAL) + "\n" +
                "Sisa Kuota : " + sisaKuota + " buku lagi",
                "Struk Peminjaman", JOptionPane.INFORMATION_MESSAGE);
            muatData();
            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private void dialogKembali() {
        String kodeTrx = JOptionPane.showInputDialog(this, "Masukkan Kode Transaksi:", "Kembalikan Buku", JOptionPane.QUESTION_MESSAGE);
        if (kodeTrx == null || kodeTrx.trim().isEmpty()) return;
        kodeTrx = kodeTrx.trim().toUpperCase();

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        String[] target = null;
        for (String[] b : semuaData) {
            if (b.length >= 6 && b[0].equalsIgnoreCase(kodeTrx)) { target = b; break; }
        }
        if (target == null) { JOptionPane.showMessageDialog(this, "Kode transaksi tidak ditemukan.", "Gagal", JOptionPane.ERROR_MESSAGE); return; }
        if (target[5].equals("1")) { JOptionPane.showMessageDialog(this, "Buku ini sudah tercatat dikembalikan.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        String strTglKembali = JOptionPane.showInputDialog(this, "Tanggal Pengembalian (dd-MM-yyyy):", LocalDate.now().format(Transaksi.FORMAT_TANGGAL));
        if (strTglKembali == null || strTglKembali.trim().isEmpty()) return;

        LocalDate tglKembali;
        try { tglKembali = LocalDate.parse(strTglKembali.trim(), Transaksi.FORMAT_TANGGAL); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Format tanggal salah.", "Gagal", JOptionPane.ERROR_MESSAGE); return; }

        LocalDate jatuhTempo   = LocalDate.parse(target[4], Transaksi.FORMAT_TANGGAL);
        long hariTerlambat     = ChronoUnit.DAYS.between(jatuhTempo, tglKembali);
        if (hariTerlambat < 0) hariTerlambat = 0;
        long denda = hariTerlambat * Transaksi.DENDA_PER_HARI;

        String[] rawBuku  = FileHelper.cariBuku(target[2]);
        String[] rawSiswa = FileHelper.cariSiswa(target[1]);
        String judulBuku  = (rawBuku  != null) ? rawBuku[1]  : target[2];
        String namaSiswa  = (rawSiswa != null) ? rawSiswa[1] : target[1];

        target[5] = "1";
        FileHelper.tulisUlang(FileHelper.FILE_TRANSAKSI, semuaData);

        String struk = "Pengembalian berhasil!\n\n" +
            "Kode Transaksi : " + kodeTrx + "\n" +
            "Nama Siswa     : " + namaSiswa + "\n" +
            "Buku           : " + judulBuku + "\n" +
            "Jatuh Tempo    : " + target[4] + "\n" +
            "Tgl Kembali    : " + tglKembali.format(Transaksi.FORMAT_TANGGAL) + "\n" +
            (hariTerlambat > 0
                ? "Status         : TERLAMBAT " + hariTerlambat + " hari\nDenda          : Rp " + denda
                : "Status         : Tepat Waktu\nDenda          : Rp 0");
        JOptionPane.showMessageDialog(this, struk, "Struk Pengembalian", JOptionPane.INFORMATION_MESSAGE);
        muatData();
    }

    private void dialogCekSiswa() {
        String nis = JOptionPane.showInputDialog(this, "Masukkan NIS Siswa:", "Cek Pinjaman Siswa", JOptionPane.QUESTION_MESSAGE);
        if (nis == null || nis.trim().isEmpty()) return;
        nis = nis.trim().toUpperCase();

        String[] rawSiswa = FileHelper.cariSiswa(nis);
        if (rawSiswa == null) { JOptionPane.showMessageDialog(this, "Siswa tidak ditemukan.", "Gagal", JOptionPane.ERROR_MESSAGE); return; }
        Siswa siswa = Siswa.fromArray(rawSiswa);

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_TRANSAKSI);
        StringBuilder sb = new StringBuilder();
        sb.append("Nama   : ").append(siswa.getNama()).append("\n");
        sb.append("Kuota  : ").append(FileHelper.hitungPinjamanAktif(nis)).append(" / 2 buku dipinjam\n\n");
        sb.append("Pinjaman Aktif:\n");
        sb.append("-------------------------------------\n");

        boolean ada = false;
        for (String[] b : semuaData) {
            if (b.length >= 6 && b[1].equalsIgnoreCase(nis) && b[5].equals("0")) {
                String[] rawBuku = FileHelper.cariBuku(b[2]);
                String judul = (rawBuku != null) ? rawBuku[1] : b[2];
                LocalDate tempo  = LocalDate.parse(b[4], Transaksi.FORMAT_TANGGAL);
                long terlambat   = ChronoUnit.DAYS.between(tempo, LocalDate.now());
                sb.append("- ").append(judul).append("\n");
                sb.append("  Jatuh Tempo: ").append(b[4]);
                if (terlambat > 0) sb.append(" (!) TERLAMBAT ").append(terlambat).append(" hari");
                sb.append("\n\n");
                ada = true;
            }
        }
        if (!ada) sb.append("Tidak ada pinjaman aktif.");

        JOptionPane.showMessageDialog(this, sb.toString(), "Status Pinjaman Siswa", JOptionPane.INFORMATION_MESSAGE);
    }
}
