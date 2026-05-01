package core;

import java.time.format.DateTimeFormatter;

public class Transaksi {

    public static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final long DENDA_PER_HARI = 1000;

    private String kodeTrx;
    private String nis;
    private String kodeBuku;
    private String tglPinjam;
    private String tglTempo;
    private String status;  
    public Transaksi(String kodeTrx, String nis, String kodeBuku,
                     String tglPinjam, String tglTempo, String status) {
        this.kodeTrx  = kodeTrx;
        this.nis      = nis;
        this.kodeBuku = kodeBuku;
        this.tglPinjam = tglPinjam;
        this.tglTempo  = tglTempo;
        this.status    = status;
    }

    public String getKodeTrx()  { return kodeTrx; }
    public String getNis()      { return nis; }
    public String getKodeBuku() { return kodeBuku; }
    public String getTglPinjam(){ return tglPinjam; }
    public String getTglTempo() { return tglTempo; }
    public String getStatus()   { return status; }

    public void setStatus(String status) { this.status = status; }

    public boolean isDipinjam() { return "0".equals(status); }

    public static Transaksi fromArray(String[] data) {
        if (data == null || data.length < 6) return null;
        return new Transaksi(data[0], data[1], data[2], data[3], data[4], data[5]);
    }

    public String[] toArray() {
        return new String[]{ kodeTrx, nis, kodeBuku, tglPinjam, tglTempo, status };
    }

    @Override
    public String toString() {
        return "Transaksi{kode='" + kodeTrx + "', nis='" + nis + "', status=" + status + "}";
    }
}
