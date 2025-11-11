package DAO;

import java.sql.*;
import java.util.*;
import Entity.LoaiGhe;
import ConnectDB.ConnectDB;

public class LoaiGheDAO {

    public List<LoaiGhe> getAllLoaiGhe() {
        List<LoaiGhe> list = new ArrayList<>();
        String sql = "SELECT * FROM loai_ghe";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiGhe lg = new LoaiGhe();
                lg.setMaLoaiGhe(rs.getInt("maLoaiGhe"));
                lg.setTenLoaiGhe(rs.getString("tenLoaiGhe"));
                lg.setMoTa(rs.getString("moTa"));
                lg.setPhuThu(rs.getDouble("phuThu"));
                list.add(lg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public LoaiGhe getLoaiGheById(int maLoai) {
        String sql = "SELECT * FROM loai_ghe WHERE maLoaiGhe = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maLoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiGhe lg = new LoaiGhe();
                    lg.setMaLoaiGhe(rs.getInt("maLoaiGhe"));
                    lg.setTenLoaiGhe(rs.getString("tenLoaiGhe"));
                    lg.setMoTa(rs.getString("moTa"));
                    lg.setPhuThu(rs.getDouble("phuThu"));
                    return lg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
