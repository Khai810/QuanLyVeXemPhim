package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Entity.NhanVien;

public class NhanVienDAO {
	Connection conn;
	
	public NhanVienDAO(Connection conn) {
		this.conn = conn;
	}
	
	
	public boolean taoNhanVien(NhanVien nhanVien) {
		String sql = "INSERT INTO nhan_vien (tenNhanVien, SDT, email, taiKhoan, matKhau) VALUES (?, ?, ?, ?, ?);";				
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, nhanVien.getTenNhanVien());
            pst.setString(2, nhanVien.getSDT());
            pst.setString(3, nhanVien.getEmail());
            pst.setString(4, nhanVien.getTaiKhoan());
            pst.setString(5, nhanVien.getMatKhau());
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi insert NV: " + e.getMessage());
        }
        
        return false; // Thất bại
    }

	public NhanVien layNhanVienBangMa(int maNhanVien) {
        String sql = "SELECT * FROM nhan_vien WHERE maNhanVien = ?";
        NhanVien nv = null;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, maNhanVien);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    nv = new NhanVien();
                    nv.setMaNhanVien(rs.getInt("maNhanVien"));
                    nv.setTenNhanVien(rs.getString("tenNhanVien"));
                    nv.setSDT(rs.getString("SDT"));
                    nv.setEmail(rs.getString("email"));
                    nv.setTaiKhoan(rs.getString("taiKhoan"));
                    nv.setMatKhau(rs.getString("matKhau"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy nhân viên theo mã: " + e.getMessage());
        }

        return nv;
    }
}
