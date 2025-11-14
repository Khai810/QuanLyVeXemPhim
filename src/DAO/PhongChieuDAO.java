package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.PhongChieu;

public class PhongChieuDAO {
	Connection conn;
	
	public PhongChieuDAO(Connection conn) {
		this.conn = conn;
	}
	public List<PhongChieu> getAllPhongChieu() {
	    List<PhongChieu> list = new ArrayList<>();
	    String sql = "SELECT * FROM phong_chieu";
	    try (PreparedStatement pst = conn.prepareStatement(sql);
	         ResultSet rs = pst.executeQuery()) {
	        while (rs.next()) {
	            PhongChieu pc = new PhongChieu();
	            pc.setMaPhongChieu(rs.getInt("maPhongChieu"));
	            pc.setTenPhong(rs.getString("tenPhong"));
	            pc.setSoLuongGhe(rs.getInt("soLuongGhe"));
	            list.add(pc);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
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
