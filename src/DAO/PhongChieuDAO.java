package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Entity.PhongChieu;

public class PhongChieuDAO {
	Connection conn;
	
	public PhongChieuDAO(Connection conn) {
		this.conn = conn;
	}
	
	public PhongChieu layPhongChieuBangMaPhongChieu(int maPhongChieu) {
		String sql = "SELECT * FROM phong_chieu WHERE maPhongChieu = ?";
	    PhongChieu phongChieu = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maPhongChieu);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                phongChieu = new PhongChieu();
	                phongChieu.setMaPhongChieu(rs.getInt("maPhongChieu"));
	                phongChieu.setTenPhong(rs.getString("tenPhong"));
	                phongChieu.setSoLuongGhe(rs.getInt("soLuongGhe"));
	                return phongChieu;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return phongChieu;
	}
}
