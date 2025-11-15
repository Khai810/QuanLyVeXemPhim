package Entity;

import java.util.Objects;

public class Ghe {
	private Integer maGhe;
    private String tenGhe;
    private LoaiGhe loaiGhe; // Quan hệ
    private PhongChieu phongChieu; // Quan hệ
	
    public Ghe(Integer maGhe, String tenGhe, LoaiGhe loaiGhe, PhongChieu phongChieu) {
		super();
		this.maGhe = maGhe;
		this.tenGhe = tenGhe;
		this.loaiGhe = loaiGhe;
		this.phongChieu = phongChieu;
	}

	public Ghe() {
		super();
	}

	public Integer getMaGhe() {
		return maGhe;
	}

	public void setMaGhe(Integer maGhe) {
		this.maGhe = maGhe;
	}

	public String getTenGhe() {
		return tenGhe;
	}

	public void setTenGhe(String tenGhe) {
		this.tenGhe = tenGhe;
	}

	public LoaiGhe getLoaiGhe() {
		return loaiGhe;
	}

	public void setLoaiGhe(LoaiGhe loaiGhe) {
		this.loaiGhe = loaiGhe;
	}
	
	public PhongChieu getPhongChieu() {
		return phongChieu;
	}

	public void setPhongChieu(PhongChieu phongChieu) {
		this.phongChieu = phongChieu;
	}
    
	@Override
	public String toString() {
		return this.tenGhe;
	}
    
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    Ghe that = (Ghe) o;
	    return maGhe == that.maGhe;
	}

	@Override
	public int hashCode() {
	    return Objects.hash(maGhe);
	}
}
