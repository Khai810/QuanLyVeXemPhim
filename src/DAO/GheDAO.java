package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public Ghe layGheBangTenGhe(String tenGhe) {
		String sql = "SELECT g.*, lg.*, pc.* " +
                "FROM ghe g " +
                "LEFT JOIN loai_ghe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
                "LEFT JOIN phong_chieu pc ON g.maPhongChieu = pc.maPhongChieu " +
                "WHERE g.tenGhe = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(sql)){
        	pst.setString(1, tenGhe);
            
        	try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                	
                	// SỬA 2: Tạo LoaiGhe từ dữ liệu đã JOIN
                    LoaiGhe loaiGhe = null;
                    int maLoaiGhe = rs.getInt("maLoaiGhe");
                    // Kiểm tra xem maLoaiGhe có bị NULL không
                    if (!rs.wasNull()) {
                        loaiGhe = new LoaiGhe(); // (Giả sử bạn có constructor rỗng và các setter)
                        loaiGhe.setMaLoaiGhe(maLoaiGhe);
                        loaiGhe.setTenLoaiGhe(rs.getString("tenLoaiGhe"));
                        loaiGhe.setPhuThu(rs.getDouble("phuThu"));
                        // ... set các thuộc tính khác của LoaiGhe từ rs
                    }

                    // SỬA 3: Tạo PhongChieu từ dữ liệu đã JOIN
                    PhongChieu phongChieu = null;
                    int maPhongChieu = rs.getInt("maPhongChieu");
                    // Kiểm tra xem maPhongChieu có bị NULL không
                    if (!rs.wasNull()) {
                        phongChieu = new PhongChieu(); // (Giả sử bạn có constructor rỗng)
                        phongChieu.setMaPhongChieu(maPhongChieu);
                        phongChieu.setTenPhong(rs.getString("tenPhong"));
                        phongChieu.setSoLuongGhe(rs.getInt("soLuongGhe"));
                        // ... set các thuộc tính khác của PhongChieu từ rs
                    }
                    
                    // SỬA 4: Tạo Ghe với các đối tượng đã có đầy đủ thông tin
                    // (Giả sử constructor 4 tham số như bạn viết)
                    Ghe ghe = new Ghe(
                        rs.getInt("maGhe"),
                        rs.getString("tenGhe"),
                        loaiGhe, // Bây giờ đã có dữ liệu
                        phongChieu // Bây giờ đã có dữ liệu
                    );
                    
                    // (Nếu lớp Ghe của bạn có thêm thuộc tính, hãy set chúng ở đây)
                    // ví dụ: ghe.setTrangThaiDat(rs.getBoolean("trangThaiDat"));
                    
                    return ghe;
                    
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
}
