package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.Ghe;
import Entity.LoaiGhe;
import Entity.PhongChieu;

public class GheDAO {
	Connection conn;
	PhongChieuDAO phongChieuDAO;
	
	public GheDAO(Connection conn) {
		this.conn = conn;
		this.phongChieuDAO = new PhongChieuDAO(conn);
	}
	
	public Ghe layGheBangTenGhe(String seatId) {
		System.out.print(seatId);
	    String sql = "SELECT g.*, lg.*, pc.* " +
	                 "FROM ghe g " +
	                 "LEFT JOIN loai_ghe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
	                 "LEFT JOIN phong_chieu pc ON g.maPhongChieu = pc.maPhongChieu " +
	                 "WHERE g.tenGhe = ? OR g.tenGhe LIKE ?";

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	        pst.setString(1, seatId);             // cho ghế đơn (ví dụ G2)
	        pst.setString(2, "%" + seatId + "%"); // cho ghế đôi chứa seatId
	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                LoaiGhe loaiGhe = new LoaiGhe(
	                    rs.getInt("maLoaiGhe"),
	                    rs.getString("tenLoaiGhe")
	                    , rs.getString("moTa"),
	                    rs.getDouble("phuThu"));
	                PhongChieu phongChieu = new PhongChieu(
	                    rs.getInt("maPhongChieu"),
	                    rs.getString("tenPhong"),
	                    rs.getInt("soLuongGhe")
	                );
	                return new Ghe(
	                    rs.getInt("maGhe"),
	                    rs.getString("tenGhe"), // giữ nguyên "J3,J4"
	                    loaiGhe,
	                    phongChieu
	                );
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public Ghe layGhebangMaGhe(int maGhe) {
		String sql = "SELECT * FROM ghe g LEFT JOIN loai_ghe lg ON g.maLoaiGhe = lg.maLoaiGhe  WHERE maGhe = ? ";
		Ghe ghe = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maGhe);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	            	ghe = new Ghe();
	            	ghe.setMaGhe(rs.getInt("maGhe"));
	            	ghe.setTenGhe(rs.getString("tenGhe"));
	            	ghe.setLoaiGhe(new LoaiGhe(rs.getInt("maLoaiGhe")
	            			, rs.getString("tenLoaiGhe"), rs.getString("moTa")
	            			, rs.getDouble("phuThu")));
	            	PhongChieu phongChieu = phongChieuDAO.layPhongChieuBangMaPhongChieu(rs.getInt("maPhongChieu"));
	            	
	            	ghe.setPhongChieu(phongChieu);
	                return ghe;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return ghe;
	}
	
	public List<Ghe> getGheTheoPhong(int maPhong) {
        List<Ghe> list = new ArrayList<>();
        String sql = "SELECT g.*, lg.tenLoaiGhe, lg.moTa, lg.phuThu " +
                     "FROM ghe g " +
                     "JOIN loai_ghe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
                     "WHERE g.maPhongChieu = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getInt("maGhe"));
                    g.setTenGhe(rs.getString("tenGhe"));

                    LoaiGhe lg = new LoaiGhe();
                    lg.setMaLoaiGhe(rs.getInt("maLoaiGhe"));
                    lg.setTenLoaiGhe(rs.getString("tenLoaiGhe"));
                    lg.setMoTa(rs.getString("moTa"));
                    lg.setPhuThu(rs.getDouble("phuThu"));
                    g.setLoaiGhe(lg);

                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy danh sách ghế theo phòng: " + e.getMessage());
        }
        return list;
    }

}

