package Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class SuatChieu {
	private Integer maSuatChieu;
    private Phim phim; // Quan hệ
    private PhongChieu phongChieu; // Quan hệ
    private LocalDate ngayChieu;
    private LocalTime gioChieu;
    private Double giaVeCoBan;
    
	public SuatChieu(Integer maSuatChieu, Phim phim, PhongChieu phongChieu, LocalDate ngayChieu, LocalTime gioChieu,
			Double giaVeCoBan) {
		super();
		this.maSuatChieu = maSuatChieu;
		this.phim = phim;
		this.phongChieu = phongChieu;
		this.ngayChieu = ngayChieu;
		this.gioChieu = gioChieu;
		this.giaVeCoBan = giaVeCoBan;
	}
	
	public SuatChieu() {
		super();
	}

	public Integer getMaSuatChieu() {
		return maSuatChieu;
	}
	public void setMaSuatChieu(Integer maSuatChieu) {
		this.maSuatChieu = maSuatChieu;
	}
	public Phim getPhim() {
		return phim;
	}
	public void setPhim(Phim phim) {
		this.phim = phim;
	}
	public PhongChieu getPhongChieu() {
		return phongChieu;
	}
	public void setPhongChieu(PhongChieu phongChieu) {
		this.phongChieu = phongChieu;
	}
	public LocalDate getNgayChieu() {
		return ngayChieu;
	}
	public void setNgayChieu(LocalDate ngayChieu) {
		this.ngayChieu = ngayChieu;
	}
	public LocalTime getGioChieu() {
		return gioChieu;
	}
	public void setGioChieu(LocalTime gioChieu) {
		this.gioChieu = gioChieu;
	}
	public Double getGiaVeCoBan() {
		return giaVeCoBan;
	}
	public void setGiaVeCoBan(Double giaVeCoBan) {
		this.giaVeCoBan = giaVeCoBan;
	}
    
	@Override
	public String toString() {
		return this.getPhim().getTenPhim() + " - " + this.ngayChieu.toString() + " - " + this.gioChieu.toString();
	}
    
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    SuatChieu that = (SuatChieu) o;
	    return maSuatChieu == that.maSuatChieu;
	}

	@Override
	public int hashCode() {
	    return Objects.hash(maSuatChieu);
	}

}
