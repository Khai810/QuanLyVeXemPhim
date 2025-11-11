package Entity;


public class PhongChieu {
	private Integer maPhongChieu;
    private String tenPhong;
    private Integer soLuongGhe;
    
	public PhongChieu(Integer maPhongChieu, String tenPhong, Integer soLuongGhe) {
		super();
		this.maPhongChieu = maPhongChieu;
		this.tenPhong = tenPhong;
		this.soLuongGhe = soLuongGhe;
		
	}

	public PhongChieu() {
		super();
	}

	public Integer getMaPhongChieu() {
		return maPhongChieu;
	}

	public void setMaPhongChieu(Integer maPhongChieu) {
		this.maPhongChieu = maPhongChieu;
	}

	public String getTenPhong() {
		return tenPhong;
	}

	public void setTenPhong(String tenPhong) {
		this.tenPhong = tenPhong;
	}

	public Integer getSoLuongGhe() {
		return soLuongGhe;
	}

	public void setSoLuongGhe(Integer soLuongGhe) {
		this.soLuongGhe = soLuongGhe;
	}
    
}
