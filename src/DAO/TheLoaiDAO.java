package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ConnectDB.ConnectDB;
import Entity.TheLoai;

public class TheLoaiDAO {
	
	public TheLoai layTheLoaiBangMaTheLoai(int maTheLoai) {
        String sql = "SELECT * FROM the_loai t WHERE t.maTheLoai = ?";
        
        try (Connection conn = ConnectDB.getConnection();
        		PreparedStatement pst = conn.prepareStatement(sql)){
        	pst.setInt(1, maTheLoai);
            
        	try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new TheLoai(
                        rs.getInt("maTheLoai"),
                        rs.getString("tenTheLoai"),
                        rs.getString("moTa")
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy danh sách thể loại: " + e.getMessage());
        }
        
        return null;
    }
    
}
