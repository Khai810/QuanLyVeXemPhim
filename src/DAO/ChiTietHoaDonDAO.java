package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.Ve;

public class ChiTietHoaDonDAO {
	Connection conn;
	VeDAO veDAO;
	HoaDonDAO hoaDonDAO;
	
	public ChiTietHoaDonDAO(Connection conn) {
        this.conn = conn;
		this.veDAO = new VeDAO(conn);
        this.hoaDonDAO = new HoaDonDAO(conn);
    }
	
	public boolean taoChiTietHoaDon(HoaDon hoaDon, List<Ve> listVe){
	    String sql = "INSERT INTO chi_tiet_hoa_don (maHD, maVe, donGiaBan, soLuong) " +
	                 "VALUES (?, ?, ?, ?)";
	    
	    // Dùng PreparedStatement với Connection đã có
	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	        
	        for (Ve ve : listVe) {
	            pst.setInt(1, hoaDon.getMaHD()); // Dùng ID mới từ bước 1
	            pst.setInt(2, ve.getMaVe());
	            pst.setDouble(3, ve.getGiaVe()); // Giả sử donGiaBan là giaVe
	            pst.setInt(4, 1); // Mỗi vé là 1 chi tiết, số lượng 1
	            
	            pst.addBatch(); // Thêm vào lô
	        }
	        
	        // Thực thi toàn bộ lô
	        int[] results = pst.executeBatch();
	        
	        // Kiểm tra xem tất cả có thành công không
	        for (int r : results) {
	            if (r <= 0) return false; // Một mục trong lô bị lỗi
	        }
	        
	        return true;
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return false;
	}

	public List<ChiTietHoaDon> layChitiethoadon(int maHoaDon){
		
		String sql = "SELECT * FROM chi_tiet_hoa_don WHERE maHD = ?";

		List<ChiTietHoaDon> listCTHD = new ArrayList<ChiTietHoaDon>();

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
		        pst.setInt(1, maHoaDon);
	        try (ResultSet rs = pst.executeQuery()) {
	            while (rs.next()) {
	                ChiTietHoaDon cthd = new ChiTietHoaDon();
	                HoaDon hoaDon = hoaDonDAO.layHoaDonBangMaHoaDon(rs.getInt("maHD"));
	                cthd.setHoaDon(hoaDon);
	                Ve ve = veDAO.layVeBangMa(rs.getInt("maVe"));
	                cthd.setVe(ve);
	                cthd.setDonGiaBan(rs.getDouble(3));
	                cthd.setSoLuong(rs.getInt(4));
	                listCTHD.add(cthd);
	            }
	        }
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

		return listCTHD;
	}
}