package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;
import Entity.Ghe;
import Entity.LoaiGhe;
import Entity.PhongChieu;

public class GheDAO {
	Connection conn;
	PhongChieuDAO phongChieuDAO;
	
	
	public GheDAO() {
		super();
	}

	public GheDAO(Connection conn) {
		this.conn = conn;
		this.phongChieuDAO = new PhongChieuDAO(conn);
	}
	
	public Ghe layGheBangTenGhe(String seatId) {
	    String sql = "SELECT g.*, lg.*, pc.* " +
	                 "FROM ghe g " +
	                 "LEFT JOIN loai_ghe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
	                 "LEFT JOIN phong_chieu pc ON g.maPhongChieu = pc.maPhongChieu";
	    
	    try (PreparedStatement pst = conn.prepareStatement(sql);
	         ResultSet rs = pst.executeQuery()) {

	        while (rs.next()) {
	            String tenGheDb = rs.getString("tenGhe"); // ví dụ "J1,J2" cho ghế đôi
	            String[] ids = tenGheDb.split(","); // tách ghế đôi
	            for (String id : ids) {
	                if (id.trim().equals(seatId)) { // nếu trùng với ghế cần tìm
	                    // ===== Tạo LoaiGhe từ dữ liệu đã JOIN =====
	                    LoaiGhe loaiGhe = null;
	                    int maLoaiGhe = rs.getInt("maLoaiGhe");
	                    if (!rs.wasNull()) {
	                        loaiGhe = new LoaiGhe();
	                        loaiGhe.setMaLoaiGhe(maLoaiGhe);
	                        loaiGhe.setTenLoaiGhe(rs.getString("tenLoaiGhe"));
	                        loaiGhe.setPhuThu(rs.getDouble("phuThu"));
	                    }

	                    // ===== Tạo PhongChieu từ dữ liệu đã JOIN =====
	                    PhongChieu phongChieu = null;
	                    int maPhongChieu = rs.getInt("maPhongChieu");
	                    if (!rs.wasNull()) {
	                        phongChieu = new PhongChieu();
	                        phongChieu.setMaPhongChieu(maPhongChieu);
	                        phongChieu.setTenPhong(rs.getString("tenPhong"));
	                        phongChieu.setSoLuongGhe(rs.getInt("soLuongGhe"));
	                    }

	                    // ===== Tạo Ghe với các đối tượng đã có đầy đủ thông tin =====
	                    return new Ghe(
	                        rs.getInt("maGhe"),
	                        tenGheDb, // vẫn giữ tên gốc như trong DB
	                        loaiGhe,
	                        phongChieu
	                    );
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("Lỗi khi lấy ghế: " + e.getMessage());
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
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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

