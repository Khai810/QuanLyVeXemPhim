package Entity;

import java.time.LocalDateTime;
import java.util.List;

public class HoaDon {
	private Integer maHD;
    private LocalDateTime ngayLapHoaDon;
    private KhachHang khachHang; // Quan hệ
    private NhanVien nhanVien; // Quan hệ
    private Integer soLuongBap;
    private Integer soLuongNuoc;
	private KhuyenMai khuyenMai;
	private PhuongThucThanhToan phuongThucThanhToan;
	
    private final Double giaBap = 70000.0;
    private final Double giaNuoc = 50000.0;
    
    
	public HoaDon(Integer maHD, LocalDateTime ngayLapHoaDon, KhachHang khachHang, NhanVien nhanVien, Integer soLuongBap,
			Integer soLuongNuoc, KhuyenMai khuyenMai, PhuongThucThanhToan phuongThucThanhToan) {
		super();
		this.maHD = maHD;
		this.ngayLapHoaDon = ngayLapHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.soLuongBap = soLuongBap;
		this.soLuongNuoc = soLuongNuoc;
		this.khuyenMai = khuyenMai;
		this.phuongThucThanhToan = phuongThucThanhToan;
	}
	
	public Double tinhTong(List<ChiTietHoaDon> listCTHD) {
		Double tong = 0.0;
		for(ChiTietHoaDon chiTietHoaDon : listCTHD) {
			tong += chiTietHoaDon.getDonGiaBan();
		}
		tong += soLuongBap * giaBap;
		tong += soLuongNuoc * giaNuoc;
		if(!khuyenMai.equals(null)) {
			tong -= khuyenMai.getGiaTriKM();
		}
		return tong;
	}

	public HoaDon() {
		super();
	}

	public Integer getMaHD() {
		return maHD;
	}

	public void setMaHD(Integer maHD) {
		this.maHD = maHD;
	}

	public LocalDateTime getNgayLapHoaDon() {
		return ngayLapHoaDon;
	}

	public void setNgayLapHoaDon(LocalDateTime ngayLapHoaDon) {
		this.ngayLapHoaDon = ngayLapHoaDon;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public Integer getSoLuongBap() {
		return soLuongBap;
	}

	public void setSoLuongBap(Integer soLuongBap) {
		this.soLuongBap = soLuongBap;
	}

	public Integer getSoLuongNuoc() {
		return soLuongNuoc;
	}

	public void setSoLuongNuoc(Integer soLuongNuoc) {
		this.soLuongNuoc = soLuongNuoc;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public PhuongThucThanhToan getPhuongThucThanhToan() {
		return phuongThucThanhToan;
	}

	public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
		this.phuongThucThanhToan = phuongThucThanhToan;
	}
	
	
}
