package Entity;


public class LoaiGhe {
	private Integer maLoaiGhe;
    private String tenLoaiGhe;
    private String moTa;
    private Double phuThu;
    private String img;
    
    
	public LoaiGhe(Integer maLoaiGhe, String tenLoaiGhe, String moTa, Double phuThu) {
		super();
		this.maLoaiGhe = maLoaiGhe;
		this.tenLoaiGhe = tenLoaiGhe;
		this.moTa = moTa;
		this.phuThu = phuThu;
	}

	public LoaiGhe(Integer maLoaiGhe, String tenLoaiGhe, String moTa, Double phuThu, String img) {
		super();
		this.maLoaiGhe = maLoaiGhe;
		this.tenLoaiGhe = tenLoaiGhe;
		this.moTa = moTa;
		this.phuThu = phuThu;
		this.img = img;
	}

	public LoaiGhe() {
		super();
	}

	public Integer getMaLoaiGhe() {
		return maLoaiGhe;
	}

	public void setMaLoaiGhe(Integer maLoaiGhe) {
		this.maLoaiGhe = maLoaiGhe;
	}

	public String getTenLoaiGhe() {
		return tenLoaiGhe;
	}

	public void setTenLoaiGhe(String tenLoaiGhe) {
		this.tenLoaiGhe = tenLoaiGhe;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public Double getPhuThu() {
		return phuThu;
	}

	public void setPhuThu(Double phuThu) {
		this.phuThu = phuThu;
	}
	public String getImg() {
	    return img;
	}

	public void setImg(String img) {
	    this.img = img;
	}

    
}
