package DAO;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Entity.HoaDon;
import Entity.KhachHang;
import Entity.KhuyenMai;
import Entity.NhanVien;
import Entity.PhuongThucThanhToan;

public class HoaDonDAO {
	Connection conn;
	KhachHangDAO khachHangDAO;
	NhanVienDAO nhanVienDAO;
	KhuyenMaiDAO khuyenMaiDAO;
	PhuongThucThanhToanDAO phuongThucThanhToanDAO;
	
	public HoaDonDAO(Connection conn) {
		this.conn = conn;
		this.khachHangDAO = new KhachHangDAO(conn);
		this.nhanVienDAO = new NhanVienDAO(conn);
		this.khuyenMaiDAO = new KhuyenMaiDAO(conn);
		this.phuongThucThanhToanDAO = new PhuongThucThanhToanDAO(conn);
	}
	
	public boolean taoHoaDon(HoaDon hoaDon) {
		String sql = "INSERT INTO hoa_don (ngayLapHoaDon, maKH, maNhanVien, soLuongBap, soLuongNuoc, maPTTT, maKM) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
		
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
        	
        	pst.setTimestamp(1, Timestamp.valueOf(hoaDon.getNgayLapHoaDon()));
        	pst.setInt(2, hoaDon.getKhachHang().getMaKH());
        	pst.setInt(3, hoaDon.getNhanVien().getMaNhanVien());
        	pst.setInt(4, hoaDon.getSoLuongBap());
        	pst.setInt(5, hoaDon.getSoLuongNuoc());
            pst.setInt(6, hoaDon.getPhuongThucThanhToan().getMaPTTT());
            
            if (hoaDon.getKhuyenMai() != null) {
                pst.setInt(7, hoaDon.getKhuyenMai().getMaKM());
            } else {
                pst.setNull(7, java.sql.Types.INTEGER);
            }
        	int rowsAffected = pst.executeUpdate();
            
            if(rowsAffected > 0){
            	try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                    	hoaDon.setMaHD(rs.getInt(1));
                        return true;
                    }
                }
            };
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tạo vé: " + e.getMessage());
            return false;
        }
		return false;
    }

	public HoaDon layHoaDonBangMaHoaDon(int maHoaDon) {
		String sql = "SELECT * FROM hoa_don WHERE maHD = ?";
	    HoaDon hoaDon = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maHoaDon);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	            	hoaDon = new HoaDon();
	            	hoaDon.setMaHD(rs.getInt("maHD"));
	            	hoaDon.setNgayLapHoaDon(rs.getTimestamp("ngayLapHoaDon").toLocalDateTime());
	            	hoaDon.setSoLuongBap(rs.getInt("soLuongBap"));
	                hoaDon.setSoLuongNuoc(rs.getInt("soLuongNuoc"));

	                int maKH = rs.getInt("maKH");
	                int maNhanVien = rs.getInt("maNhanVien");
	                int maPTTT = rs.getInt("maPTTT");
	                int maKM = rs.getInt("maKM");
	                
	                KhachHang khachHang = khachHangDAO.layKhachHangBangMa(maKH);
	                NhanVien nhanVien = nhanVienDAO.layNhanVienBangMa(maNhanVien);
	                PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanDAO.layPTTTBangMa(maPTTT);

	                hoaDon.setKhachHang(khachHang);
	                hoaDon.setNhanVien(nhanVien);
	                hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);

	                if (!rs.wasNull()) {
	                    KhuyenMai khuyenMai = khuyenMaiDAO.layKhuyenMaiBangMa(maKM);
	                    hoaDon.setKhuyenMai(khuyenMai);
	                }

	                return hoaDon;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return hoaDon;
	}
}
