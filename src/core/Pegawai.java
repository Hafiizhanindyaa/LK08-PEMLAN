package src;

public class Pegawai {

    private String nip;
    private String nama;
    private String tglLahir;
    private String password;

    public Pegawai(String nip, String nama, String tglLahir, String password) {
        this.nip      = nip;
        this.nama     = nama;
        this.tglLahir = tglLahir;
        this.password = password;
    }

    public String getNip()      { return nip; }
    public String getNama()     { return nama; }
    public String getTglLahir() { return tglLahir; }
    public String getPassword() { return password; }

    public void setNama(String nama)         { this.nama     = nama; }
    public void setTglLahir(String tglLahir) { this.tglLahir = tglLahir; }
    public void setPassword(String password) { this.password = password; }

    public static Pegawai fromArray(String[] data) {
        if (data == null || data.length < 4) return null;
        return new Pegawai(data[0], data[1], data[2], data[3]);
    }

    public String[] toArray() {
        return new String[]{ nip, nama, tglLahir, password };
    }

    @Override
    public String toString() {
        return "Pegawai{nip='" + nip + "', nama='" + nama + "'}";
    }
}
