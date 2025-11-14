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
import Entity.Phim;
import Entity.PhongChieu;
import Entity.SuatChieu;
import Entity.TheLoai;

public class SuatChieuDAO {
	
	Connection conn;
	PhimDAO phimDAO;
    PhongChieuDAO phongChieuDAO;
    
    public SuatChieuDAO() {
        try {
            this.conn = ConnectDB.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.phimDAO = new PhimDAO(conn);
        this.phongChieuDAO = new PhongChieuDAO(conn);
    }

	public SuatChieuDAO(Connection conn) {
		this.conn = conn;
		this.phimDAO = new PhimDAO(conn);
		this.phongChieuDAO = new PhongChieuDAO(conn);
	}
	
	public List<SuatChieu> getAllSuatChieu(Integer maPhim){
		List<SuatChieu> list = new ArrayList<SuatChieu>();
		String sql = "SELECT " +
                "    sc.maSuatChieu, sc.maPhim AS sc_maPhim, sc.maPhongChieu AS sc_maPhongChieu, " +
                "    sc.ngayChieu, sc.gioChieu, sc.giaVeCoBan, " +
                "    p.maPhim, p.tenPhim, p.moTa AS phim_moTa, p.doTuoi, p.quocGia, " +
                "    p.thoiLuong, p.daoDien, p.ngayKhoiChieu, p.img, " +
                "    tl.maTheLoai, tl.tenTheLoai, tl.moTa AS theLoai_moTa, " +
                "    pc.maPhongChieu, pc.tenPhong, pc.soLuongGhe " +
                "FROM suat_chieu sc " +
                "LEFT JOIN phim p ON sc.maPhim = p.maPhim " +
                "LEFT JOIN the_loai tl ON tl.maTheLoai = p.maTheLoai " +
                "LEFT JOIN phong_chieu pc ON pc.maPhongChieu = sc.maPhongChieu " +
                "WHERE sc.maPhim = ? " +
                "ORDER BY sc.gioChieu ASC";
		
		try(PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setInt(1, maPhim);
			try(ResultSet rs = pst.executeQuery()){
				while (rs.next()) {
                    // Tạo TheLoai
                    TheLoai theLoai = null;
                    int maTheLoai = rs.getInt("maTheLoai");
                    if (!rs.wasNull()) {
                        theLoai = new TheLoai(
                            maTheLoai,
                            rs.getString("tenTheLoai"),
                            rs.getString("theLoai_moTa")
                        );
                    }

                    // Tạo Phim
                    Phim phim = new Phim(
                        rs.getInt("sc_maPhim"),
                        rs.getString("tenPhim"),
                        rs.getString("phim_moTa"),
                        rs.getString("doTuoi"),
                        rs.getString("quocGia"),
                        rs.getInt("thoiLuong"),
                        rs.getString("daoDien"),
                        rs.getDate("ngayKhoiChieu") != null ? 
                                rs.getDate("ngayKhoiChieu").toLocalDate() : null,
                        rs.getString("img"),
                        theLoai
                    );

                    // Tạo PhongChieu
                    PhongChieu phongChieu = new PhongChieu(
                        rs.getInt("sc_maPhongChieu"),
                        rs.getString("tenPhong"),
                        rs.getInt("soLuongGhe")
                    );

                    // Tạo SuatChieu
                    SuatChieu suatChieu = new SuatChieu(
                        rs.getInt("maSuatChieu"),
                        phim,
                        phongChieu,
                        rs.getDate("ngayChieu") != null ?
                            rs.getDate("ngayChieu").toLocalDate() : null,
                        rs.getTime("gioChieu") != null ?
                            rs.getTime("gioChieu").toLocalTime() : null,
                        rs.getDouble("giaVeCoBan")
                    );

                    list.add(suatChieu);
                }
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
    
	public SuatChieu laySuatChieuBangMa(int maSuatChieu) {
		String sql = "SELECT * FROM suat_chieu WHERE maSuatChieu = ?";
	    SuatChieu sc = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maSuatChieu);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	                sc = new SuatChieu();
	                sc.setMaSuatChieu(rs.getInt(1));
	                sc.setNgayChieu(rs.getDate(4).toLocalDate());
	                sc.setGioChieu(rs.getTime(5).toLocalTime());
	                sc.setGiaVeCoBan(rs.getDouble(6));

	                int maPhim = rs.getInt("maPhim");
	                int maPhongChieu = rs.getInt("maPhongChieu");

	                Phim phim = phimDAO.layPhimBangMaPhim(maPhim);
	                PhongChieu phongChieu = phongChieuDAO.layPhongChieuBangMaPhongChieu(maPhongChieu);
	                
	                sc.setPhim(phim);
	                sc.setPhongChieu(phongChieu);
	                return sc;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return sc;
	}
	
	 // Thêm suất chiếu
    public boolean insert(SuatChieu sc) {
        String sql = "INSERT INTO suat_chieu(maPhim, maPhongChieu, ngayChieu, gioChieu, giaVeCoBan) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, sc.getPhim().getMaPhim());
            pst.setInt(2, sc.getPhongChieu().getMaPhongChieu());
            pst.setDate(3, Date.valueOf(sc.getNgayChieu()));
            pst.setTime(4, Time.valueOf(sc.getGioChieu()));
            pst.setDouble(5, sc.getGiaVeCoBan());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật suất chiếu
    public boolean update(SuatChieu sc) {
        String sql = "UPDATE suat_chieu SET maPhim=?, maPhongChieu=?, ngayChieu=?, gioChieu=?, giaVeCoBan=? WHERE maSuatChieu=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, sc.getPhim().getMaPhim());
            pst.setInt(2, sc.getPhongChieu().getMaPhongChieu());
            pst.setDate(3, Date.valueOf(sc.getNgayChieu()));
            pst.setTime(4, Time.valueOf(sc.getGioChieu()));
            pst.setDouble(5, sc.getGiaVeCoBan());
            pst.setInt(6, sc.getMaSuatChieu());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa suất chiếu
    public boolean delete(int maSuatChieu) {
        String sql = "DELETE FROM suat_chieu WHERE maSuatChieu=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, maSuatChieu);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
