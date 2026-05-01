package src;

public class Buku {

    private String kode;
    private String judul;
    private String jenis;

    public Buku(String kode, String judul, String jenis) {
        this.kode  = kode;
        this.judul = judul;
        this.jenis = jenis;
    }

    public String getKode()  { return kode; }
    public String getJudul() { return judul; }
    public String getJenis() { return jenis; }

    public void setJudul(String judul) { this.judul = judul; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public static Buku fromArray(String[] data) {
        if (data == null || data.length < 3) return null;
        return new Buku(data[0], data[1], data[2]);
    }

    public String[] toArray() {
        return new String[]{ kode, judul, jenis };
    }

    @Override
    public String toString() {
        return "Buku{kode='" + kode + "', judul='" + judul + "'}";
    }
}
