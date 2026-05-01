package panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.FileHelper;
import src.Siswa;

import java.awt.*;
import java.util.ArrayList;

public class PanelSiswa extends JPanel {

    private JTable tabel;
    private DefaultTableModel model;
    private JTextField txtCari;

    public PanelSiswa() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initComponents();
        muatData("");
    }

    private void initComponents() {
        add(MainFrame.buatHeader("Data Siswa", "Kelola data siswa perpustakaan"), BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(248, 250, 252));
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JPanel kiriPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        kiriPanel.setBackground(new Color(248, 250, 252));

        JButton btnTambah  = MainFrame.buatTombol("+ Tambah Siswa", new Color(34, 197, 94));
        JButton btnEdit    = MainFrame.buatTombol("Edit",            new Color(59, 130, 246));
        JButton btnHapus   = MainFrame.buatTombol("Hapus",           new Color(239, 68, 68));
        JButton btnRefresh = MainFrame.buatTombol("Refresh",         new Color(100, 116, 139));

        kiriPanel.add(btnTambah);
        kiriPanel.add(btnEdit);
        kiriPanel.add(btnHapus);
        kiriPanel.add(btnRefresh);

        JPanel cariPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        cariPanel.setBackground(new Color(248, 250, 252));
        txtCari = MainFrame.buatTextField(18);
        txtCari.setToolTipText("Cari berdasarkan NIS atau Nama");
        JButton btnCari = MainFrame.buatTombol("Cari", new Color(99, 102, 241));
        cariPanel.add(new JLabel("Cari: "));
        cariPanel.add(txtCari);
        cariPanel.add(btnCari);

        toolbar.add(kiriPanel, BorderLayout.WEST);
        toolbar.add(cariPanel, BorderLayout.EAST);

        // Tabel
        String[] kolom = {"No", "NIS", "Nama Siswa", "Alamat"};
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        MainFrame.styleTable(tabel);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(280);

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 25, 25, 25),
            BorderFactory.createLineBorder(new Color(226, 232, 240))));

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setBackground(new Color(248, 250, 252));
        tengah.add(toolbar, BorderLayout.NORTH);
        tengah.add(scroll,  BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        // Event listeners
        btnTambah.addActionListener(e -> dialogTambah());
        btnEdit.addActionListener(e -> dialogEdit());
        btnHapus.addActionListener(e -> hapusSiswa());
        btnRefresh.addActionListener(e -> { txtCari.setText(""); muatData(""); });
        btnCari.addActionListener(e -> muatData(txtCari.getText().trim().toLowerCase()));
        txtCari.addActionListener(e -> muatData(txtCari.getText().trim().toLowerCase()));
    }

    private void muatData(String filter) {
        model.setRowCount(0);
        ArrayList<String[]> data = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        int no = 1;
        for (String[] baris : data) {
            if (baris.length >= 3) {
                if (filter.isEmpty()
                        || baris[0].toLowerCase().contains(filter)
                        || baris[1].toLowerCase().contains(filter)) {
                    model.addRow(new Object[]{no++, baris[0], baris[1], baris[2]});
                }
            }
        }
    }

    private void dialogTambah() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Siswa", true);
        dialog.setSize(400, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtNIS    = MainFrame.buatTextField(18);
        JTextField txtNama   = MainFrame.buatTextField(18);
        JTextField txtAlamat = MainFrame.buatTextField(18);

        String[] labels    = {"NIS :", "Nama :", "Alamat :"};
        JTextField[] fields = {txtNIS, txtNama, txtAlamat};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        tombol.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton btnSimpan = MainFrame.buatTombol("Simpan", new Color(34, 197, 94));
        JButton btnBatal  = MainFrame.buatTombol("Batal",  new Color(100, 116, 139));
        tombol.add(btnBatal);
        tombol.add(btnSimpan);

        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(tombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            String nis    = txtNIS.getText().trim().toUpperCase();
            String nama   = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();

            if (nis.isEmpty() || nama.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "NIS dan Nama tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (FileHelper.cariSiswa(nis) != null) {
                JOptionPane.showMessageDialog(dialog, "NIS " + nis + " sudah terdaftar.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Siswa siswa = new Siswa(nis, nama, alamat);
            FileHelper.tambahBaris(FileHelper.FILE_SISWA, siswa.toArray());
            JOptionPane.showMessageDialog(dialog, "Siswa " + nama + " berhasil ditambahkan.");
            muatData("");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void dialogEdit() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data siswa yang ingin diedit terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nisLama = (String) model.getValueAt(baris, 1);
        String[] raw   = FileHelper.cariSiswa(nisLama);
        if (raw == null) return;
        Siswa siswa = Siswa.fromArray(raw);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Siswa", true);
        dialog.setSize(400, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtNama   = MainFrame.buatTextField(18);
        JTextField txtAlamat = MainFrame.buatTextField(18);
        txtNama.setText(siswa.getNama());
        txtAlamat.setText(siswa.getAlamat());

        String[] labels    = {"Nama :", "Alamat :"};
        JTextField[] fields = {txtNama, txtAlamat};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        tombol.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        JButton btnSimpan = MainFrame.buatTombol("Simpan", new Color(34, 197, 94));
        JButton btnBatal  = MainFrame.buatTombol("Batal",  new Color(100, 116, 139));
        tombol.add(btnBatal);
        tombol.add(btnSimpan);

        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(tombol, BorderLayout.SOUTH);

        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
            for (String[] d : semuaData) {
                if (d.length >= 3 && d[0].equalsIgnoreCase(nisLama)) {
                    String nb = txtNama.getText().trim();
                    String ab = txtAlamat.getText().trim();
                    if (!nb.isEmpty()) d[1] = nb;
                    if (!ab.isEmpty()) d[2] = ab;
                    break;
                }
            }
            FileHelper.tulisUlang(FileHelper.FILE_SISWA, semuaData);
            JOptionPane.showMessageDialog(dialog, "Data siswa berhasil diperbarui.");
            muatData("");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void hapusSiswa() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih siswa yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nis  = (String) model.getValueAt(baris, 1);
        String nama = (String) model.getValueAt(baris, 2);

        if (FileHelper.hitungPinjamanAktif(nis) > 0) {
            JOptionPane.showMessageDialog(this, "Tidak bisa dihapus. Siswa masih memiliki pinjaman aktif.", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Hapus siswa \"" + nama + "\"?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;

        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_SISWA);
        ArrayList<String[]> dataBaru  = new ArrayList<>();
        for (String[] d : semuaData) {
            if (!(d.length >= 1 && d[0].equalsIgnoreCase(nis))) dataBaru.add(d);
        }
        FileHelper.tulisUlang(FileHelper.FILE_SISWA, dataBaru);
        JOptionPane.showMessageDialog(this, "Siswa berhasil dihapus.");
        muatData("");
    }
}
