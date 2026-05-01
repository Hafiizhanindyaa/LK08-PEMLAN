package panel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private String namaPegawai;

    public MainFrame(String namaPegawai) {
        this.namaPegawai = namaPegawai;
        setTitle("Sistem Perpustakaan SMP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        initComponents();
        showPanel(new PanelSiswa());
    }

    // Mengatur komponen dan layout
    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar kiri layar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setLayout(new BorderLayout());

        // Judul dan nama perpustakaan
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(30, 58, 138));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel lblLogo = new JLabel("PERPUSTAKAAN", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 14));
        lblLogo.setForeground(Color.WHITE);
        JLabel lblSMP = new JLabel("SMP", SwingConstants.CENTER);
        lblSMP.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSMP.setForeground(new Color(147, 197, 253));
        logoPanel.add(lblLogo, BorderLayout.CENTER);
        logoPanel.add(lblSMP,  BorderLayout.SOUTH);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Navigasi menu 
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(15, 23, 42));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[][] menus = {
            {"Data Siswa",   "siswa"},
            {"Data Buku",    "buku"},
            {"Data Pegawai", "pegawai"},
            {"Transaksi",    "transaksi"},
            {"Laporan",      "laporan"},
        };

        ButtonGroup btnGroup = new ButtonGroup();
        for (String[] menu : menus) {
            JToggleButton btn = buatTombolMenu(menu[0], menu[1]);
            btnGroup.add(btn);
            menuPanel.add(btn);
        }
        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Info pengguna + tombol keluar
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(30, 41, 59));
        userPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblUser = new JLabel("Pengguna: " + namaPegawai);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(new Color(148, 163, 184));
        JButton btnKeluar = new JButton("Keluar");
        btnKeluar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnKeluar.setBackground(new Color(220, 38, 38));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnKeluar.setFocusPainted(false);
        btnKeluar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKeluar.addActionListener(e -> {
            int konfirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (konfirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        userPanel.add(lblUser,    BorderLayout.CENTER);
        userPanel.add(btnKeluar,  BorderLayout.EAST);
        sidebar.add(userPanel, BorderLayout.SOUTH);

        // Isi konten (kanan layar)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(248, 250, 252));

        add(sidebar,      BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // Membuat tombol menu
    private JToggleButton buatTombolMenu(String teks, String id) {
        JToggleButton btn = new JToggleButton(teks);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(new Color(148, 163, 184));
        btn.setBackground(new Color(15, 23, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                btn.setBackground(new Color(30, 58, 138));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(15, 23, 42));
                btn.setForeground(new Color(148, 163, 184));
            }
        });

        btn.addActionListener(e -> {
            switch (id) {
                case "siswa":      showPanel(new PanelSiswa());      break;
                case "buku":       showPanel(new PanelBuku());        break;
                case "pegawai":    showPanel(new PanelPegawai());     break;
                case "transaksi":  showPanel(new PanelTransaksi());   break;
                case "laporan":    showPanel(new PanelLaporan());     break;
            }
        });
        return btn;
    }

    // Menampilkan panel konten
    public void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Template untuk header panel konten

    public static JPanel buatHeader(String judul, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 22));
        lblJudul.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        panel.add(lblJudul, BorderLayout.CENTER);
        panel.add(lblSub,   BorderLayout.SOUTH);
        return panel;
    }

    // Template untuk tombol panel konten
    public static JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Template untuk tabel panel konten
    public static void styleTable(JTable tabel) {
        tabel.setFont(new Font("Arial", Font.PLAIN, 13));
        tabel.setRowHeight(30);
        tabel.setGridColor(new Color(226, 232, 240));
        tabel.setSelectionBackground(new Color(219, 234, 254));
        tabel.setSelectionForeground(new Color(15, 23, 42));
        tabel.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabel.getTableHeader().setBackground(new Color(30, 58, 138));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabel.setShowVerticalLines(false);
    }

    // Template untuk text field panel konten
    public static JTextField buatTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }
}
