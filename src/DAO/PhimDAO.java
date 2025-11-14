package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.Phim;
import Entity.TheLoai;

public class PhimDAO {
	Connection conn;
	TheLoaiDAO theLoaiDAO =new TheLoaiDAO();
	
	public PhimDAO(Connection conn) {
		this.conn = conn;
		this.theLoaiDAO = new TheLoaiDAO(conn);
	}
	public ArrayList<Phim> getAllPhim() {
        ArrayList<Phim> listPhim = new ArrayList<>();
        String sql = "SELECT p.*, tl.tenTheLoai, tl.moTa as moTaTheLoai " +
                     "FROM phim p " +
                     "LEFT JOIN the_loai tl ON p.maTheLoai = tl.maTheLoai " +
                     "ORDER BY p.ngayKhoiChieu DESC";
        
        try (PreparedStatement pst = conn.prepareStatement(sql);
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
	public List<Phim> searchPhim(String tuKhoa) {
	    List<Phim> list = new ArrayList<>();
	    String sql = "SELECT * FROM phim WHERE tenPhim LIKE ?";
	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	        pst.setString(1, "%" + tuKhoa + "%");
	        try (ResultSet rs = pst.executeQuery()) {
	            while (rs.next()) {
	                Phim p = new Phim();
	                p.setMaPhim(rs.getInt("maPhim"));
	                p.setTenPhim(rs.getString("tenPhim"));
	                p.setDaoDien(rs.getString("daoDien"));
	                p.setQuocGia(rs.getString("quocGia"));
	                p.setDoTuoi(rs.getString("doTuoi"));
	                p.setThoiLuong(rs.getInt("thoiLuong"));
	                p.setNgayKhoiChieu(rs.getDate("ngayKhoiChieu") != null ? rs.getDate("ngayKhoiChieu").toLocalDate() : null);
	                // Gán các trường khác nếu cần
	                list.add(p);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    // Thêm phim, trả về maPhim vừa tạo (identity)
    public Integer insert(Phim p) throws SQLException {
        String sql = "INSERT INTO phim(tenPhim, moTa, doTuoi, quocGia, thoiLuong, daoDien, ngayKhoiChieu, img, maTheLoai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            bindPhim(ps, p);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    // Cập nhật phim theo maPhim 
    public boolean update(Phim p) throws SQLException {
        String sql = "UPDATE phim SET tenPhim=?, moTa=?, doTuoi=?, quocGia=?, thoiLuong=?, daoDien=?, ngayKhoiChieu=?, img=?, maTheLoai=? " +
                     "WHERE maPhim=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindPhim(ps, p);
            ps.setInt(10, p.getMaPhim());
            return ps.executeUpdate() > 0;
        }
    }

    // Xoá phim theo maPhim 
    public boolean delete(int maPhim) throws SQLException {
        String sql = "DELETE FROM phim WHERE maPhim=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhim);
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm phim theo từ khoá (tên phim/đạo diễn/quốc gia)
    public List<Phim> search(String keyword) {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT p.*, tl.tenTheLoai, tl.moTa as moTaTheLoai " +
                     "FROM phim p LEFT JOIN the_loai tl ON p.maTheLoai = tl.maTheLoai " +
                     "WHERE p.tenPhim LIKE ? OR p.daoDien LIKE ? OR p.quocGia LIKE ? " +
                     "ORDER BY p.maPhim DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TheLoai tl = null;
                    int maTL = rs.getInt("maTheLoai");
                    if (!rs.wasNull()) {
                        tl = new TheLoai(maTL, rs.getString("tenTheLoai"), rs.getString("moTaTheLoai"));
                    }
                    Phim phim = new Phim(
                        rs.getInt("maPhim"),
                        rs.getString("tenPhim"),
                        rs.getString("moTa"),
                        rs.getString("doTuoi"),
                        rs.getString("quocGia"),
                        rs.getInt("thoiLuong"),
                        rs.getString("daoDien"),
                        rs.getDate("ngayKhoiChieu") != null ? rs.getDate("ngayKhoiChieu").toLocalDate() : null,
                        rs.getString("img"),
                        tl
                    );
                    list.add(phim);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Gán tham số Phim cho PreparedStatement (phục vụ insert/update) 
    private void bindPhim(PreparedStatement ps, Phim p) throws SQLException {
        ps.setString(1, p.getTenPhim());
        ps.setString(2, p.getMoTa());
        ps.setString(3, p.getDoTuoi());
        ps.setString(4, p.getQuocGia());
        ps.setInt(5, p.getThoiLuong());
        ps.setString(6, p.getDaoDien());
        if (p.getNgayKhoiChieu() != null) {
            ps.setDate(7, java.sql.Date.valueOf(p.getNgayKhoiChieu()));
        } else {
            ps.setDate(7, null);
        }
        ps.setString(8, p.getImg());
        ps.setObject(9, p.getTheLoai() != null ? p.getTheLoai().getMaTheLoai() : null);
    }

	public Phim layPhimBangMaPhim(int maPhim) {
		String sql = "SELECT * FROM phim WHERE maPhim = ?";
	    Phim phim = null;

	    try (PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setInt(1, maPhim);

	        try (ResultSet rs = pst.executeQuery()) {
	            if (rs.next()) {
	            	TheLoai theLoai = theLoaiDAO.layTheLoaiBangMaTheLoai(rs.getInt("maTheLoai"));
	            	
	            	phim = new Phim(
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
                        theLoai);
	            	return phim;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return phim;
	    }
		return phim;
	}
	
}
