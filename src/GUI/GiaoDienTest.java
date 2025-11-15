package GUI;

import Entity.NhanVien;

public class GiaoDienTest {
	public static void main(String[] args) {
		
		NhanVien nhanVien = new NhanVien(1, "nhan vien ban ve", "0123123123", "nv@Gm", "admin", "admin", "imgPhim/thay_cung.jpg");
		var dd = new GiaoDienChonPhim(nhanVien);
		dd.setVisible(true);

//		var dd = new GiaoDienDangNhap();
//		dd.setVisible(true);

	}
}
