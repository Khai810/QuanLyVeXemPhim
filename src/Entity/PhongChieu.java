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
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof PhongChieu)) return false;
	    PhongChieu other = (PhongChieu) obj;
	    return this.maPhongChieu != null && this.maPhongChieu.equals(other.maPhongChieu);
	}

	@Override
	public int hashCode() {
	    return maPhongChieu != null ? maPhongChieu.hashCode() : 0;
	}

	@Override
	public String toString() {
	    return tenPhong; 
	}
}
