package panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.Buku;
import src.FileHelper;

import java.awt.*;
import java.util.ArrayList;

public class PanelBuku extends JPanel {

    private JTable tabel;
    private DefaultTableModel model;
    private JTextField txtCari;

    public PanelBuku() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initComponents();
        muatData("");
    }

    private void initComponents() {
        add(MainFrame.buatHeader("Data Buku", "Kelola koleksi buku perpustakaan"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(248, 250, 252));
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JPanel kiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        kiri.setBackground(new Color(248, 250, 252));
        JButton btnTambah  = MainFrame.buatTombol("+ Tambah Buku", new Color(34, 197, 94));
        JButton btnEdit    = MainFrame.buatTombol("Edit",           new Color(59, 130, 246));
        JButton btnHapus   = MainFrame.buatTombol("Hapus",          new Color(239, 68, 68));
        JButton btnRefresh = MainFrame.buatTombol("Refresh",        new Color(100, 116, 139));
        kiri.add(btnTambah); kiri.add(btnEdit); kiri.add(btnHapus); kiri.add(btnRefresh);

        JPanel cari = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        cari.setBackground(new Color(248, 250, 252));
        txtCari = MainFrame.buatTextField(18);
        JButton btnCari = MainFrame.buatTombol("Cari", new Color(99, 102, 241));
        cari.add(new JLabel("Cari: ")); cari.add(txtCari); cari.add(btnCari);

        toolbar.add(kiri, BorderLayout.WEST);
        toolbar.add(cari, BorderLayout.EAST);

        String[] kolom = {"No", "Kode Buku", "Judul Buku", "Jenis", "Status"};
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        MainFrame.styleTable(tabel);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(270);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(90);

        // Warnai kolom Status
        tabel.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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

        btnTambah.addActionListener(e -> dialogTambah());
        btnEdit.addActionListener(e -> dialogEdit());
        btnHapus.addActionListener(e -> hapusBuku());
        btnRefresh.addActionListener(e -> { txtCari.setText(""); muatData(""); });
        btnCari.addActionListener(e -> muatData(txtCari.getText().trim().toLowerCase()));
        txtCari.addActionListener(e -> muatData(txtCari.getText().trim().toLowerCase()));
    }

    private void muatData(String filter) {
        model.setRowCount(0);
        ArrayList<String[]> data = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
        int no = 1;
        for (String[] b : data) {
            if (b.length >= 3) {
                boolean cocok = filter.isEmpty()
                    || b[0].toLowerCase().contains(filter)
                    || b[1].toLowerCase().contains(filter)
                    || b[2].toLowerCase().contains(filter);
                if (cocok) {
                    String status = FileHelper.apakahBukuDipinjam(b[0]) ? "Dipinjam" : "Tersedia";
                    model.addRow(new Object[]{no++, b[0], b[1], b[2], status});
                }
            }
        }
    }

    private void dialogTambah() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Buku", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtKode  = MainFrame.buatTextField(18);
        JTextField txtJudul = MainFrame.buatTextField(18);
        JTextField txtJenis = MainFrame.buatTextField(18);

        String[] labels    = {"Kode Buku :", "Judul :", "Jenis :"};
        JTextField[] fields = {txtKode, txtJudul, txtJenis};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel tombol = buatPanelTombol(dialog, () -> {
            String kode  = txtKode.getText().trim().toUpperCase();
            String judul = txtJudul.getText().trim();
            String jenis = txtJenis.getText().trim();
            if (kode.isEmpty() || judul.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Kode dan Judul tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (FileHelper.cariBuku(kode) != null) {
                JOptionPane.showMessageDialog(dialog, "Kode buku " + kode + " sudah ada.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Buku buku = new Buku(kode, judul, jenis);
            FileHelper.tambahBaris(FileHelper.FILE_BUKU, buku.toArray());
            JOptionPane.showMessageDialog(dialog, "Buku \"" + judul + "\" berhasil ditambahkan.");
            muatData(""); return true;
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(tombol, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void dialogEdit() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String kodeLama = (String) model.getValueAt(baris, 1);
        String[] raw    = FileHelper.cariBuku(kodeLama);
        if (raw == null) return;
        Buku buku = Buku.fromArray(raw);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Buku", true);
        dialog.setSize(400, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtJudul = MainFrame.buatTextField(18);
        JTextField txtJenis = MainFrame.buatTextField(18);
        txtJudul.setText(buku.getJudul());
        txtJenis.setText(buku.getJenis());

        String[] labels    = {"Judul :", "Jenis :"};
        JTextField[] fields = {txtJudul, txtJenis};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel tombol = buatPanelTombol(dialog, () -> {
            ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
            for (String[] d : semuaData) {
                if (d.length >= 3 && d[0].equalsIgnoreCase(kodeLama)) {
                    String jb = txtJudul.getText().trim();
                    String jn = txtJenis.getText().trim();
                    if (!jb.isEmpty()) d[1] = jb;
                    if (!jn.isEmpty()) d[2] = jn;
                    break;
                }
            }
            FileHelper.tulisUlang(FileHelper.FILE_BUKU, semuaData);
            JOptionPane.showMessageDialog(dialog, "Data buku berhasil diperbarui.");
            muatData(""); return true;
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(tombol, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void hapusBuku() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String kode  = (String) model.getValueAt(baris, 1);
        String judul = (String) model.getValueAt(baris, 2);

        if (FileHelper.apakahBukuDipinjam(kode)) {
            JOptionPane.showMessageDialog(this, "Tidak bisa dihapus. Buku sedang dalam peminjaman.", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Hapus buku \"" + judul + "\"?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_BUKU);
        ArrayList<String[]> dataBaru  = new ArrayList<>();
        for (String[] d : semuaData) {
            if (!(d.length >= 1 && d[0].equalsIgnoreCase(kode))) dataBaru.add(d);
        }
        FileHelper.tulisUlang(FileHelper.FILE_BUKU, dataBaru);
        JOptionPane.showMessageDialog(this, "Buku berhasil dihapus.");
        muatData("");
    }

    /** Helper: buat panel tombol Simpan/Batal. */
    private JPanel buatPanelTombol(JDialog dialog, java.util.function.BooleanSupplier aksiSimpan) {
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        tombol.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton btnBatal  = MainFrame.buatTombol("Batal",  new Color(100, 116, 139));
        JButton btnSimpan = MainFrame.buatTombol("Simpan", new Color(34, 197, 94));
        tombol.add(btnBatal); tombol.add(btnSimpan);
        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> { if (aksiSimpan.getAsBoolean()) dialog.dispose(); });
        return tombol;
    }
}
