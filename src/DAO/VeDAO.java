package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;
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
	
//	public boolean taoSetVe(List<Ve> listVe) {
//	    
//	    if (listVe == null || listVe.isEmpty()) {
//	        return true;
//	    }
//	    
//	    String sql = "INSERT INTO ve (maSuatChieu, maGhe, giaVe, ngayDat, ngayChieu, gioChieu, tenGhe, tenPhongChieu) " +
//	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//	    
//	    try (Connection conn = ConnectDB.getConnection();
//	         PreparedStatement pst = conn.prepareStatement(sql)) {
//
//	        try {
//	            conn.setAutoCommit(false);
//
//	            for (Ve ve : listVe) {
//	                pst.setInt(1, ve.getSuatChieu().getMaSuatChieu());
//	                pst.setInt(2, ve.getGhe().getMaGhe());
//	                pst.setDouble(3, ve.getGiaVe());
//	                pst.setDate(4, Date.valueOf(ve.getNgayDat()));
//	                pst.setDate(5, Date.valueOf(ve.getNgayChieu()));
//	                pst.setTime(6, Time.valueOf(ve.getGioChieu()));
//	                pst.setString(7, ve.getTenGhe());
//	                pst.setString(8, ve.getTenPhongChieu());
//
//	                pst.addBatch();
//	            }
//
//	            int[] results = pst.executeBatch();
//	            
//	            for (int r : results) {
//	                if (r <= 0 && r != Statement.SUCCESS_NO_INFO) {
//	                    throw new SQLException("Một vé trong lô không thể tạo, trả về số hàng: " + r);
//	                }
//	            }
//
//	            conn.commit();
//	            return true;
//
//	        } catch (SQLException e) {
//	            e.printStackTrace();
//	            System.err.println("Lỗi khi tạo loạt vé, đang rollback...");
//	            if (conn != null) {
//	                conn.rollback();
//	            }
//	            return false;
//	        }
//
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	        System.err.println("Lỗi khi chuẩn bị tạo vé: " + e.getMessage());
//	        return false;
//	    }
//	}

	public Ve layVeBangMaVe(int maVe) {
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

	public boolean taoVe(Ve ve) {
		String sql = "INSERT INTO ve (maSuatChieu, maGhe, giaVe, ngayDat, ngayChieu, gioChieu, tenGhe, tenPhongChieu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
        		PreparedStatement pst = conn.prepareStatement(sql)){
        	
        	pst.setInt(1, ve.getSuatChieu().getMaSuatChieu());
        	pst.setInt(2, ve.getGhe().getMaGhe());
        	pst.setDouble(3, ve.getGiaVe());
        	pst.setDate(4, Date.valueOf(ve.getNgayDat()));
        	pst.setDate(5, Date.valueOf(ve.getNgayChieu()));
        	pst.setTime(6, Time.valueOf(ve.getGioChieu()));
        	pst.setString(7, ve.getTenGhe());
        	pst.setString(8, ve.getTenPhongChieu());
        	
        	int rowsAffected = pst.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tạo vé: " + e.getMessage());
            return false;
        }
    }

	public List<Ve> layTatCaVe(){
		String sql = "SELECT * FROM ve ORDER BY maVe DESC";
        List<Ve> list = new ArrayList<Ve>();

        try (PreparedStatement pst = conn.prepareStatement(sql);
        		ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
            	Ve ve = new Ve();
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
                
                list.add(ve);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy tất cả vé" + e.getMessage());
        }
        return list;
	}

	public boolean capNhatVe(Ve ve) {
		String sql = "UPDATE ve SET maSuatChieu = ?, maGhe = ?, giaVe = ?, ngayDat = ?, ngayChieu = ?"
				+ ", gioChieu = ?, tenGhe = ?, tenPhongChieu = ? WHERE maVe = ?";
        
        try (Connection conn = ConnectDB.getConnection();
        		PreparedStatement pst = conn.prepareStatement(sql)){
        	
        	pst.setInt(1, ve.getSuatChieu().getMaSuatChieu());
        	pst.setInt(2, ve.getGhe().getMaGhe());
        	pst.setDouble(3, ve.getGiaVe());
        	pst.setDate(4, Date.valueOf(ve.getNgayDat()));
        	pst.setDate(5, Date.valueOf(ve.getNgayChieu()));
        	pst.setTime(6, Time.valueOf(ve.getGioChieu()));
        	pst.setString(7, ve.getTenGhe());
        	pst.setString(8, ve.getTenPhongChieu());
        	pst.setInt(9, ve.getMaVe());
        	
        	int rowsAffected = pst.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi cập nhật vé: " + e.getMessage());
            return false;
        }
    }
	public List<Ve> search(String tuKhoa) {
	    String sql = 
	            "SELECT v.* FROM ve v LEFT JOIN suat_chieu sc ON sc.maSuatChieu = v.maSuatChieu "
	          + "LEFT JOIN ghe g ON g.maGhe = v.maGhe "
	          + "LEFT JOIN phim p ON p.maPhim = sc.maPhim "
	          + "WHERE CAST(v.maVe AS VARCHAR) LIKE ?  "
	          + "OR p.tenPhim LIKE ? "
	          + "OR v.gioChieu LIKE ? "
	          + "OR g.tenGhe LIKE ? "
	          + "ORDER BY v.maVe DESC";

	    List<Ve> list = new ArrayList<>();

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	    	pst.setString(1, "%" + tuKhoa + "%");
	        pst.setString(2, "%" + tuKhoa + "%");
	        pst.setString(3, "%" + tuKhoa + "%");
	        pst.setString(4, "%" + tuKhoa + "%");

	        try (ResultSet rs = pst.executeQuery()) {
	            while (rs.next()) {
	                Ve ve = new Ve();
	                ve.setMaVe(rs.getInt("maVe"));
	                ve.setGiaVe(rs.getDouble("giaVe"));
	                ve.setNgayDat(rs.getDate("ngayDat").toLocalDate());
	                ve.setNgayChieu(rs.getDate("ngayChieu").toLocalDate());
	                ve.setGioChieu(rs.getTime("gioChieu").toLocalTime());
	                ve.setTenGhe(rs.getString("tenGhe"));
	                ve.setTenPhongChieu(rs.getString("tenPhongChieu"));

	                // --- Quan hệ ---
	                int maSuatChieu = rs.getInt("maSuatChieu");
	                int maGhe = rs.getInt("maGhe");

	                ve.setSuatChieu(suatChieuDAO.laySuatChieuBangMa(maSuatChieu));
	                ve.setGhe(gheDAO.layGhebangMaGhe(maGhe));

	                list.add(ve);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public boolean xoaVe(Integer maVe) {
		String sql = "DELETE FROM ve WHERE maVe=?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, maVe);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
