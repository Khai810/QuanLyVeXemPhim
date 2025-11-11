package Entity;

public class NhanVien {
	private Integer maNhanVien;
    private String tenNhanVien;
    private String SDT;
    private String email;
    private String taiKhoan;
    private String matKhau;
    
	public NhanVien(Integer maNhanVien, String tenNhanVien, String SDT, String email, String taiKhoan, String matKhau) {
		super();
		this.maNhanVien = maNhanVien;
		this.tenNhanVien = tenNhanVien;
		this.SDT = SDT;
		this.email = email;
		this.taiKhoan = taiKhoan;
		this.matKhau = matKhau;
	}
	
	public NhanVien() {
		super();
	}

	public Integer getMaNhanVien() {
		return maNhanVien;
	}

	public void setMaNhanVien(Integer maNhanVien) {
		this.maNhanVien = maNhanVien;
	}

	public String getTenNhanVien() {
		return tenNhanVien;
	}

	public void setTenNhanVien(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;
	}

	public String getSDT() {
		return SDT;
	}

	public void setSDT(String SDT) {
		this.SDT = SDT;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(String taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}
    
    
}
