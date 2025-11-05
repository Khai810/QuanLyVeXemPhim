package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import Entity.Phim;
import Entity.TheLoai;

public class PhimDAO {
	TheLoaiDAO theLoaiDAO = new TheLoaiDAO();
	
	public ArrayList<Phim> getAllPhim() {
        ArrayList<Phim> listPhim = new ArrayList<>();
        String sql = "SELECT p.*, tl.tenTheLoai, tl.moTa as moTaTheLoai " +
                     "FROM phim p " +
                     "LEFT JOIN the_loai tl ON p.maTheLoai = tl.maTheLoai " +
                     "ORDER BY p.ngayKhoiChieu DESC";
        
        try (Connection conn = ConnectDB.getConnection();
        		PreparedStatement pst = conn.prepareStatement(sql);
        			ResultSet rs = pst.executeQuery();) {
            while (rs.next()) {
                TheLoai theLoai = null;
                int maTheLoai = rs.getInt("maTheLoai");
                if (!rs.wasNull()) {
                    theLoai = new TheLoai(
                        maTheLoai,
                        rs.getString("tenTheLoai"), 
                        rs.getString("moTa")       
                    );
                }
                
                Phim phim = new Phim(
                    rs.getInt("maPhim"),
                    rs.getString("tenPhim"),
                    rs.getString("moTa"),
                    rs.getString("doTuoi"),
                    rs.getString("quocGia"),
                    rs.getInt("thoiLuong"),
                    rs.getString("daoDien"),
                    rs.getDate("ngayKhoiChieu") != null ? 
                        rs.getDate("ngayKhoiChieu").toLocalDate() : null,
                    rs.getString("img"),
                    theLoai
                );
                
                listPhim.add(phim);
	            
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy danh sách phim: " + e.getMessage());
        }
        
        return listPhim;
    }

}
