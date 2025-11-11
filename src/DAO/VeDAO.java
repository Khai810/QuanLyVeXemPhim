package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import Entity.Ghe;
import Entity.SuatChieu;
import Entity.Ve;

public class VeDAO {
	Connection conn;
	SuatChieuDAO suatChieuDAO;
	GheDAO gheDAO;
	
	public VeDAO(Connection conn) {
		this.conn = conn;
		this.suatChieuDAO = new SuatChieuDAO(conn);
		this.gheDAO = new GheDAO(conn);
	}
	public boolean taoListVe(List<Ve> listVe) {
    if (listVe == null || listVe.isEmpty()) return true;

    String sql = "INSERT INTO ve (maSuatChieu, maGhe, giaVe, ngayDat, ngayChieu, gioChieu, tenGhe, tenPhongChieu) " +
                 "OUTPUT INSERTED.maVe VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        for (Ve ve : listVe) {
            pst.setInt(1, ve.getSuatChieu().getMaSuatChieu());
            pst.setInt(2, ve.getGhe().getMaGhe());
            pst.setDouble(3, ve.getGiaVe());
            pst.setDate(4, Date.valueOf(ve.getNgayDat()));
            pst.setDate(5, Date.valueOf(ve.getNgayChieu()));
            pst.setTime(6, Time.valueOf(ve.getGioChieu()));
            pst.setString(7, ve.getTenGhe());
            pst.setString(8, ve.getTenPhongChieu());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    ve.setMaVe(rs.getInt(1)); // lấy maVe mới
                }
            }
        }
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("❌ Lỗi khi tạo loạt vé: " + e.getMessage());
        return false;
    }
}

	
	public Ve layVeBangMa(int maVe) {
	    String sql = "SELECT * FROM ve WHERE maVe = ?";
	    Ve ve = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maVe);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                ve = new Ve();
	                ve.setMaVe(rs.getInt("maVe"));
	                ve.setGiaVe(rs.getDouble("giaVe"));
	                ve.setNgayDat(rs.getDate("ngayDat").toLocalDate());
	                ve.setNgayChieu(rs.getDate("ngayChieu").toLocalDate());
	                ve.setGioChieu(rs.getTime("gioChieu").toLocalTime());
	                ve.setTenGhe(rs.getString("tenGhe"));
	                ve.setTenPhongChieu(rs.getString("tenPhongChieu"));

	                // --- Lấy quan hệ ---
	                int maSuatChieu = rs.getInt("maSuatChieu");
	                int maGhe = rs.getInt("maGhe");

	                SuatChieu suatChieu = suatChieuDAO.laySuatChieuBangMa(maSuatChieu);
	                Ghe ghe = gheDAO.layGhebangMaGhe(maGhe);
	                
	                ve.setSuatChieu(suatChieu);
	                ve.setGhe(ghe);
	                return ve;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return ve;
	}

	
	
//	
//	public boolean taoVe(Ve ve) {
//		String sql = "INSERT INTO ve (maSuatChieu, maGhe, giaVe, ngayDat, ngayChieu, gioChieu, tenGhe, tenPhongChieu) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//        
//        try (Connection conn = ConnectDB.getConnection();
//        		PreparedStatement pst = conn.prepareStatement(sql)){
//        	
//        	pst.setInt(1, ve.getSuatChieu().getMaSuatChieu());
//        	pst.setInt(2, ve.getGhe().getMaGhe());
//        	pst.setDouble(3, ve.getGiaVe());
//        	pst.setDate(4, Date.valueOf(ve.getNgayDat()));
//        	pst.setDate(5, Date.valueOf(ve.getNgayChieu()));
//        	pst.setTime(6, Time.valueOf(ve.getGioChieu()));
//        	pst.setString(7, ve.getTenGhe());
//        	pst.setString(8, ve.getTenPhongChieu());
//        	
//        	int rowsAffected = pst.executeUpdate();
//            
//            return rowsAffected > 0;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.err.println("Lỗi khi tạo vé: " + e.getMessage());
//            return false;
//        }
//    }
}
