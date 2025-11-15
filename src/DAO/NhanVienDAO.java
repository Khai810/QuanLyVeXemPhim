package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.NhanVien;

public class NhanVienDAO {
	Connection conn;
	
	public NhanVienDAO(Connection conn) {
		this.conn = conn;
	}
	
	public List<NhanVien> layTatCaNhanVien() {
        String sql = "SELECT * FROM nhan_vien";
        List<NhanVien> list = new ArrayList<>();

        try (PreparedStatement pst = conn.prepareStatement(sql);
        		ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getInt("maNhanVien"));
                nv.setTenNhanVien(rs.getString("tenNhanVien"));
                nv.setSDT(rs.getString("SDT"));
                nv.setEmail(rs.getString("email"));
                nv.setTaiKhoan(rs.getString("taiKhoan"));
                nv.setMatKhau(rs.getString("matKhau"));
                nv.setImg(rs.getString("img"));
                list.add(nv);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy tất cả nhân viên" + e.getMessage());
        }
        return list;
    }
	
	public boolean taoNhanVien(NhanVien nhanVien) {
		String sql = "INSERT INTO nhan_vien (tenNhanVien, SDT, email, taiKhoan, matKhau, img) VALUES (?, ?, ?, ?, ?, ?);";				
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, nhanVien.getTenNhanVien());
            pst.setString(2, nhanVien.getSDT());
            pst.setString(3, nhanVien.getEmail());
            pst.setString(4, nhanVien.getTaiKhoan());
            pst.setString(5, nhanVien.getMatKhau());
            pst.setString(6, nhanVien.getImg());

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi insert NV: " + e.getMessage());
        }
        
        return false; // Thất bại
    }

	public NhanVien layNhanVienBangMa(int maNhanVien) {
        String sql = "SELECT * FROM nhan_vien WHERE maNhanVien = ?";
        NhanVien nv = null;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, maNhanVien);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    nv = new NhanVien();
                    nv.setMaNhanVien(rs.getInt("maNhanVien"));
                    nv.setTenNhanVien(rs.getString("tenNhanVien"));
                    nv.setSDT(rs.getString("SDT"));
                    nv.setEmail(rs.getString("email"));
                    nv.setTaiKhoan(rs.getString("taiKhoan"));
                    nv.setMatKhau(rs.getString("matKhau"));
                    nv.setImg(rs.getString("img"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy nhân viên theo mã: " + e.getMessage());
        }

        return nv;
    }

	public NhanVien dangNhap(String taiKhoan, String matKhau) {
		String sql = "SELECT * FROM nhan_vien WHERE taiKhoan = ? AND matKhau = ?;";
        NhanVien nv = null;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
        	pst.setString(1, taiKhoan);
        	pst.setString(2, matKhau);
        	try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    nv = new NhanVien();
                    nv.setMaNhanVien(rs.getInt("maNhanVien"));
                    nv.setTenNhanVien(rs.getString("tenNhanVien"));
                    nv.setSDT(rs.getString("SDT"));
                    nv.setEmail(rs.getString("email"));
                    nv.setTaiKhoan(rs.getString("taiKhoan"));
                    nv.setMatKhau(rs.getString("matKhau"));
                    nv.setImg(rs.getString("img"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy tất cả nhân viên" + e.getMessage());
        }
        return nv;
	}

	public boolean capNhatNhanVien(NhanVien nv) {
		String sql = "UPDATE nhan_vien SET tenNhanVien = ?, SDT = ?, email = ?, taiKhoan = ?, matKhau = ?, img = ? "
				+ "WHERE maNhanVien = ?";				
		try(PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setString(1, nv.getTenNhanVien());
			pst.setString(2, nv.getSDT());
			pst.setString(3, nv.getEmail());
			pst.setString(4, nv.getTaiKhoan());
			pst.setString(5, nv.getMatKhau());
			pst.setString(6, nv.getImg());
			pst.setInt(7, nv.getMaNhanVien());
			int row = pst.executeUpdate();
			return row > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean xoaNhanVien(Integer maNV) {
		String sql = "DELETE FROM nhan_vien WHERE maNhanVien = ?";				
		try(PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setInt(1, maNV);
			int row = pst.executeUpdate();
			return row > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public List<NhanVien> searchNhanVien(String tuKhoa) {
        String sql = "SELECT * FROM nhan_vien "
                + "WHERE CAST(maNhanVien AS VARCHAR) LIKE ? "
                + "OR tenNhanVien LIKE ? "
                + "OR SDT LIKE ? "
                + "OR Email LIKE ? "
                + "OR TaiKhoan LIKE ? "
                + "ORDER BY maNhanVien DESC";
        List<NhanVien> list = new ArrayList<>();

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
        	String k = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";
	        pst.setString(1, k);
	        pst.setString(2, k);
	        pst.setString(3, k);
	        pst.setString(4, k);
	        pst.setString(5, k);
	        
        	try (ResultSet rs = pst.executeQuery()){
	            while (rs.next()) {
	                NhanVien nv = new NhanVien();
	                nv.setMaNhanVien(rs.getInt("maNhanVien"));
	                nv.setTenNhanVien(rs.getString("tenNhanVien"));
	                nv.setSDT(rs.getString("SDT"));
	                nv.setEmail(rs.getString("email"));
	                nv.setTaiKhoan(rs.getString("taiKhoan"));
	                nv.setMatKhau(rs.getString("matKhau"));
	                nv.setImg(rs.getString("img"));
	                list.add(nv);
	            }
            return list;
        	}
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy tất cả nhân viên" + e.getMessage());
        }
        return list;
    }
}