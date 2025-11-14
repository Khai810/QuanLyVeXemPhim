package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Entity.KhachHang;

public class KhachHangDAO {
	Connection conn;
	
	public KhachHangDAO(Connection conn) {
		this.conn = conn;
	}
	
	public boolean insertKhachHang(KhachHang khachHang) {
        String sql = "INSERT INTO khach_hang (tenKH, SDT) OUTPUT INSERTED.maKH VALUES (?, ?)";
        
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, khachHang.getTenKH());
            pst.setString(2, khachHang.getSDT());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    khachHang.setMaKH(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" Lỗi khi insert khách hàng: " + e.getMessage());
        }
        
        return false; // Thất bại
    }
	
	public KhachHang layKhachHangBangMa(int maKH) {
        String sql = "SELECT * FROM khach_hang WHERE maKH = ?";
        KhachHang kh = null;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, maKH);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    kh = new KhachHang();
                    kh.setMaKH(rs.getInt("maKH"));
                    kh.setTenKH(rs.getString("tenKH"));
                    kh.setSDT(rs.getString("SDT"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy khách hàng theo mã: " + e.getMessage());
        }

        return kh;
    }

	public KhachHang layKhachHangBangSDT(String sdt) {
        String sql = "SELECT * FROM khach_hang WHERE SDT = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, sdt);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getInt("maKH"));
                    kh.setTenKH(rs.getString("tenKH"));
                    kh.setSDT(rs.getString("SDT"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

	public List<KhachHang> layTatCaKhachHang() {
        String sql = "SELECT * FROM khach_hang ORDER BY maKH";
        List<KhachHang> list = new ArrayList<>();

        try (PreparedStatement pst = conn.prepareStatement(sql);
        		ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getInt("maKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSDT(rs.getString("SDT"));
                list.add(kh);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy tất cả khách hàng" + e.getMessage());
        }
        return list;
    }
}
