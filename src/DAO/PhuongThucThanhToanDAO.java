package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.PhuongThucThanhToan;

public class PhuongThucThanhToanDAO {
	
	Connection conn;
	
	public PhuongThucThanhToanDAO(Connection conn) {
		this.conn = conn;
	}
	
	public List<PhuongThucThanhToan> getAll() throws SQLException {
        
		List<PhuongThucThanhToan> list = new ArrayList<>();
        String sql = "SELECT * FROM phuong_thuc_thanh_toan";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhuongThucThanhToan p = new PhuongThucThanhToan(
                        rs.getInt("maPTTT"),
                        rs.getString("tenPTTT"),
                        rs.getString("moTa"),
                        rs.getDouble("phiGiaoDich")
                );
                list.add(p);
            }
        }
        return list;
    }
	
public PhuongThucThanhToan layPTTTBangMa(int maPTTT) throws SQLException {
        
        String sql = "SELECT * FROM phuong_thuc_thanh_toan WHERE maPTTT = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);) {
        	ps.setInt(1, maPTTT);
        	try(ResultSet rs = ps.executeQuery()){
	            while (rs.next()) {
	                PhuongThucThanhToan p = new PhuongThucThanhToan(
	                        rs.getInt("maPTTT"),
	                        rs.getString("tenPTTT"),
	                        rs.getString("moTa"),
	                        rs.getDouble("phiGiaoDich"));
	                        return p;
	            }
        	}
        }
		return null;
    }
}
