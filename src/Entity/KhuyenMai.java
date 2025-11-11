package Entity;

import java.sql.Date;

public class KhuyenMai {
	private int maKM;
	private String tenKM;
	private String maCode;
	private double giaTriKM;
	private Date ngayBatDau;
	private Date ngayKetThuc;
	private String moTa;
	private boolean trangThai;
	 
	public KhuyenMai(int maKM, String tenKM, String maCode, double giaTriKM, Date ngayBatDau, Date ngayKetThuc,
			String moTa, boolean trangThai) {
		super();
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.maCode = maCode;
		this.giaTriKM = giaTriKM;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.moTa = moTa;
		this.trangThai = trangThai;
	}
	
	public KhuyenMai() {super();}

	public int getMaKM() {
		return maKM;
	}

	public void setMaKM(int maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public String getMaCode() {
		return maCode;
	}

	public void setMaCode(String maCode) {
		this.maCode = maCode;
	}

	public double getGiaTriKM() {
		return giaTriKM;
	}

	public void setGiaTriKM(double giaTriKM) {
		this.giaTriKM = giaTriKM;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
	 
	 
	
	 
}
