package Entity;

public class TheLoai {
	private Integer maTheLoai;
    private String tenTheLoai;
    private String moTa;
    
	public TheLoai() {
		super();
	}

	public TheLoai(Integer maTheLoai, String tenTheLoai, String moTa) {
		super();
		this.maTheLoai = maTheLoai;
		this.tenTheLoai = tenTheLoai;
		this.moTa = moTa;
	}

	public Integer getMaTheLoai() {
		return maTheLoai;
	}

	public void setMaTheLoai(Integer maTheLoai) {
		this.maTheLoai = maTheLoai;
	}

	public String getTenTheLoai() {
		return tenTheLoai;
	}

	public void setTenTheLoai(String tenTheLoai) {
		this.tenTheLoai = tenTheLoai;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}
	@Override
	public String toString() {
	    return tenTheLoai; 
	}
}
