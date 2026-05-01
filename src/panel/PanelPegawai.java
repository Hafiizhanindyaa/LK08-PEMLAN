package panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import core.FileHelper;
import core.Pegawai;

import java.awt.*;
import java.util.ArrayList;

public class PanelPegawai extends JPanel {

    private JTable tabel;
    private DefaultTableModel model;

    public PanelPegawai() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initComponents();
        muatData();
    }

    private void initComponents() {
        add(MainFrame.buatHeader("Data Pegawai", "Kelola akun pegawai perpustakaan"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(new Color(248, 250, 252));
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JButton btnTambah  = MainFrame.buatTombol("+ Tambah Pegawai", new Color(34, 197, 94));
        JButton btnEdit    = MainFrame.buatTombol("Edit",             new Color(59, 130, 246));
        JButton btnHapus   = MainFrame.buatTombol("Hapus",            new Color(239, 68, 68));
        JButton btnRefresh = MainFrame.buatTombol("Refresh",          new Color(100, 116, 139));
        toolbar.add(btnTambah); toolbar.add(btnEdit); toolbar.add(btnHapus); toolbar.add(btnRefresh);

        String[] kolom = {"No", "NIP", "Nama Pegawai", "Tanggal Lahir"};
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        MainFrame.styleTable(tabel);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(230);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(130);

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
        btnHapus.addActionListener(e -> hapusPegawai());
        btnRefresh.addActionListener(e -> muatData());
    }

    private void muatData() {
        model.setRowCount(0);
        ArrayList<String[]> data = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);
        int no = 1;
        for (String[] b : data) {
            if (b.length >= 3) {
                model.addRow(new Object[]{no++, b[0], b[1], b[2]});
            }
        }
    }

    private void dialogTambah() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Pegawai", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtNIP  = MainFrame.buatTextField(18);
        JTextField txtNama = MainFrame.buatTextField(18);
        JTextField txtTgl  = MainFrame.buatTextField(18);
        JPasswordField txtPass = new JPasswordField(18);
        txtPass.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        String[] labels    = {"NIP :", "Nama :", "Tgl Lahir :", "Password :"};
        JComponent[] comps = {txtNIP, txtNama, txtTgl, txtPass};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(comps[i], gbc);
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
            String nip  = txtNIP.getText().trim().toUpperCase();
            String nama = txtNama.getText().trim();
            String tgl  = txtTgl.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            if (nip.isEmpty() || nama.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "NIP, Nama, dan Password tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (FileHelper.cariPegawai(nip) != null) {
                JOptionPane.showMessageDialog(dialog, "NIP " + nip + " sudah terdaftar.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Pegawai pegawai = new Pegawai(nip, nama, tgl, pass);
            FileHelper.tambahBaris(FileHelper.FILE_PEGAWAI, pegawai.toArray());
            JOptionPane.showMessageDialog(dialog, "Pegawai " + nama + " berhasil ditambahkan.");
            muatData();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void dialogEdit() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nipLama = (String) model.getValueAt(baris, 1);
        String[] raw   = FileHelper.cariPegawai(nipLama);
        if (raw == null) return;
        Pegawai pegawai = Pegawai.fromArray(raw);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Pegawai", true);
        dialog.setSize(420, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtNama = MainFrame.buatTextField(18);
        JTextField txtTgl  = MainFrame.buatTextField(18);
        JPasswordField txtPass = new JPasswordField(18);
        txtPass.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        txtNama.setText(pegawai.getNama());
        txtTgl.setText(pegawai.getTglLahir());

        String[] labels    = {"Nama :", "Tgl Lahir :", "Password Baru :"};
        JComponent[] comps = {txtNama, txtTgl, txtPass};
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(comps[i], gbc);
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
            ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);
            for (String[] d : semuaData) {
                if (d.length >= 4 && d[0].equalsIgnoreCase(nipLama)) {
                    String nb = txtNama.getText().trim();
                    String tb = txtTgl.getText().trim();
                    String pb = new String(txtPass.getPassword()).trim();
                    if (!nb.isEmpty()) d[1] = nb;
                    if (!tb.isEmpty()) d[2] = tb;
                    if (!pb.isEmpty()) d[3] = pb;
                    break;
                }
            }
            FileHelper.tulisUlang(FileHelper.FILE_PEGAWAI, semuaData);
            JOptionPane.showMessageDialog(dialog, "Data pegawai berhasil diperbarui.");
            muatData();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void hapusPegawai() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ArrayList<String[]> semuaData = FileHelper.bacaSemua(FileHelper.FILE_PEGAWAI);
        if (semuaData.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Tidak bisa dihapus. Minimal harus ada 1 pegawai aktif.", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nip  = (String) model.getValueAt(baris, 1);
        String nama = (String) model.getValueAt(baris, 2);
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Hapus pegawai \"" + nama + "\"?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;

        ArrayList<String[]> dataBaru = new ArrayList<>();
        for (String[] d : semuaData) {
            if (!(d.length >= 1 && d[0].equalsIgnoreCase(nip))) dataBaru.add(d);
        }
        FileHelper.tulisUlang(FileHelper.FILE_PEGAWAI, dataBaru);
        JOptionPane.showMessageDialog(this, "Pegawai berhasil dihapus.");
        muatData();
    }
}
