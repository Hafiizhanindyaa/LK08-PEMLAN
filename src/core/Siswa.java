package core;

public class Siswa {

    private String nis;
    private String nama;
    private String alamat;

    public Siswa(String nis, String nama, String alamat) {
        this.nis    = nis;
        this.nama   = nama;
        this.alamat = alamat;
    }

    public String getNis()    { return nis; }
    public String getNama()   { return nama; }
    public String getAlamat() { return alamat; }

    public void setNama(String nama)     { this.nama   = nama; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public static Siswa fromArray(String[] data) {
        if (data == null || data.length < 3) return null;
        return new Siswa(data[0], data[1], data[2]);
    }

    public String[] toArray() {
        return new String[]{ nis, nama, alamat };
    }

    @Override
    public String toString() {
        return "Siswa{nis='" + nis + "', nama='" + nama + "'}";
    }
}
