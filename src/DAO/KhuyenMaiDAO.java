package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Entity.KhuyenMai;

public class KhuyenMaiDAO {
	
	Connection conn;
	
	public KhuyenMaiDAO(Connection conn) {
		this.conn = conn;
	}
	
	public KhuyenMai findByCode(String code) throws SQLException {
        
        String sql = "SELECT * FROM khuyen_mai WHERE maCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new KhuyenMai(
                        rs.getInt("maKM"),
                        rs.getString("tenKM"),
                        rs.getString("maCode"),
                        rs.getDouble("giaTriKM"),
                        rs.getDate("ngayBatDau"),
                        rs.getDate("ngayKetThuc"),
                        rs.getString("moTa"),
                        rs.getBoolean("trangThai")
                );
            }
        }
        return null;
    }

public KhuyenMai layKhuyenMaiBangMa(int maKM) throws SQLException {
        
        String sql = "SELECT * FROM khuyen_mai WHERE maKM = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maKM);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new KhuyenMai(
                        rs.getInt("maKM"),
                        rs.getString("tenKM"),
                        rs.getString("maCode"),
                        rs.getDouble("giaTriKM"),
                        rs.getDate("ngayBatDau"),
                        rs.getDate("ngayKetThuc"),
                        rs.getString("moTa"),
                        rs.getBoolean("trangThai")
                );
            }
        }
        return null;
    }
	
}
