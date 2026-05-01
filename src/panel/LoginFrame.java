package panel;

import javax.swing.*;

import core.FileHelper;

import java.awt.*;
public class LoginFrame extends JFrame {

    private JTextField txtNIP;
    private JPasswordField txtPassword;
    private int sisaPercobaan = 3;

    public LoginFrame() {
        setTitle("Sistem Perpustakaan SMP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // Panel utama dengan gradient
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 58, 138));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));
        JLabel lblJudul = new JLabel("PERPUSTAKAAN SMP", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 20));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Sistem Manajemen Perpustakaan", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(147, 197, 253));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(lblJudul, BorderLayout.CENTER);
        headerPanel.add(lblSub, BorderLayout.SOUTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNIP = new JLabel("NIP:");
        lblNIP.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(lblNIP, gbc);

        txtNIP = new JTextField(15);
        txtNIP.setFont(new Font("Arial", Font.PLAIN, 13));
        txtNIP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtNIP, gbc);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(30, 58, 138));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 0, 5);
        formPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> prosesLogin());
        txtPassword.addActionListener(e -> prosesLogin());
        txtNIP.addActionListener(e -> txtPassword.requestFocus());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void prosesLogin() {
        String nip = txtNIP.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        String[] dataPegawai = FileHelper.cariPegawai(nip);
        if (dataPegawai != null && dataPegawai.length >= 4 && dataPegawai[3].equals(password)) {
            JOptionPane.showMessageDialog(this,
                "Selamat datang, " + dataPegawai[1] + "!",
                "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame(dataPegawai[1]).setVisible(true);
            dispose();
        } else {
            sisaPercobaan--;
            if (sisaPercobaan <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Terlalu banyak percobaan login. Program dihentikan.",
                    "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this,
                    "NIP atau password salah!\nSisa percobaan: " + sisaPercobaan,
                    "Login Gagal", JOptionPane.WARNING_MESSAGE);
                txtPassword.setText("");
                txtNIP.requestFocus();
            }
        }
    }
}
