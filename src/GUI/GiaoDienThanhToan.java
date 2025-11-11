package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ConnectDB.ConnectDB;
import DAO.ChiTietHoaDonDAO;
import DAO.GheDAO;
import DAO.HoaDonDAO;
import DAO.KhachHangDAO;
import DAO.KhuyenMaiDAO;
import DAO.PhuongThucThanhToanDAO;
import DAO.VeDAO;
import Entity.Ghe;
import Entity.HoaDon;
import Entity.KhachHang;
import Entity.KhuyenMai;
import Entity.NhanVien;
import Entity.Phim;
import Entity.PhuongThucThanhToan;
import Entity.SuatChieu;
import Entity.Ve;

public class GiaoDienThanhToan extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	JPanel pNorth, pWest, pCen, pnlTamTinh;
	JSplitPane spnCen;
	JLabel lblThanhToan, lblThongTinVe, lblThongTinKH, lblBapNuoc, lblThongBaoKM;
	JTextField txtTenKH, txtSDT, txtBap, txtNuoc, txtKM;
	JButton btnTao, btnQuayLai, btnTangBap, btnGiamBap, btnTangNuoc, btnGiamNuoc, btnKTKM;
	JComboBox<String> cbxPTTT;
	SuatChieu suatChieuDaChon;
	Set<String> gheDaChon;
	private KhuyenMai khuyenMaiHopLe = null;
	List<PhuongThucThanhToan> listPTTT = null;
	// mock NV
	NhanVien nhanVien = new NhanVien(1, "nhan vien ban ve", "0123456789", "nv@gmail.com", "admin", "admin");
	
	LoadHinhAnh load = new LoadHinhAnh();
	
	
	private final String imgBap = "/img/bap.jpg";
	private final String imgNuoc = "/img/nuoc.jpg";
	
	private final Color COLOR_BG = Color.WHITE;
	private final Font fontTieuDe = new Font("Arial", Font.BOLD, 19);
	private final Font fontChu = new Font("Arial", Font.PLAIN, 14);
	private final Dimension POSTER_SIZE = new Dimension(340, 450);
	private final Dimension BAP_NUOC_SIZE = new Dimension(50, 50);
	
	private final double GIA_BAP = 70000;
	private final double GIA_NUOC = 50000;
	private int soLuongBap = 0;
    private int soLuongNuoc = 0;
	
	public GiaoDienThanhToan(Set<String> ghe, SuatChieu suatChieu) {
		this.suatChieuDaChon = suatChieu;
		this.gheDaChon = ghe;
		
		MenuChinh menuBar = new MenuChinh(this);
		this.setJMenuBar(menuBar);
		MenuToggleUtil.addToggleSupport(this, menuBar);
		
		
		pNorth = new JPanel();
		lblThanhToan = new JLabel("Thanh toán");
		pNorth.add(lblThanhToan);
		
		pWest = createInfoPanel();
		
		pCen = panelThanhToan();
		pCen.setBackground(COLOR_BG);

		spnCen = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pWest, pCen);
		spnCen.setDividerLocation(400); 
		
		add(pNorth, BorderLayout.NORTH);
		add(spnCen, BorderLayout.CENTER);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1400, 800);
		setLocationRelativeTo(null); // Căn giữa màn hình
	}
	
	private JPanel createInfoPanel() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(450, 0));
        p.setBackground(COLOR_BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 20, 10, 12));

        lblThongTinVe = new JLabel("Thông tin vé:");
        lblThongTinVe.setFont(fontTieuDe);
        p.add(lblThongTinVe);
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        // ======= Poster phim =======
        JLabel poster = new JLabel("<html><center>Poster<br>không có</center></html>", JLabel.CENTER);
        poster.setPreferredSize(POSTER_SIZE);
        poster.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        poster.setHorizontalAlignment(SwingConstants.CENTER);

        if (suatChieuDaChon != null && suatChieuDaChon.getPhim() != null && suatChieuDaChon.getPhim().getImg() != null) {
        	poster.setIcon(load.taiHinhAnh(suatChieuDaChon.getPhim().getImg(), POSTER_SIZE.width, POSTER_SIZE.height));
        	poster.setText("");
        }
        
        p.add(poster);
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        // ======= Thông tin phim =======
        Phim phim = (suatChieuDaChon != null) ? suatChieuDaChon.getPhim() : null;
        String tenPhim = (phim != null && phim.getTenPhim() != null) ? phim.getTenPhim() : "Tên phim";
        String daoDien = (phim != null && phim.getDaoDien() != null) ? phim.getDaoDien() : "—";
        String doTuoi = (phim != null && phim.getDoTuoi() != null) ? phim.getDoTuoi() : "—";
        int thoiLuong = (phim != null && phim.getThoiLuong() > 0) ? phim.getThoiLuong() : 0;

        JLabel lblTitle = new JLabel(tenPhim);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblTitle);
        p.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblSuat = new JLabel("<html><strong>Suất: </strong>" + suatChieuDaChon.getGioChieu().toString()+ "   " + suatChieuDaChon.getNgayChieu().toString() + "</html>");
        lblSuat.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSuat.setFont(fontChu);
        p.add(lblSuat);
        
        JLabel lblRap = new JLabel("<html><strong>Rạp: </strong>" + suatChieuDaChon.getPhongChieu().getTenPhong() +"</html>");
        lblRap.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRap.setFont(fontChu);
        p.add(lblRap);

        JLabel lblDaoDien = new JLabel("<html><strong>Đạo diễn: </strong>" + daoDien + "</html>");
        lblDaoDien.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDaoDien.setFont(fontChu);
        p.add(lblDaoDien);

        JLabel lblDoTuoi = new JLabel("<html><strong>Độ tuổi: </strong>" + doTuoi + "</html>");
        lblDoTuoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDoTuoi.setFont(fontChu);
        p.add(lblDoTuoi);

        if (thoiLuong > 0) {
            JLabel lblThoiLuong = new JLabel("<html><strong>Thời lượng: </strong>" + thoiLuong + " phút" + "</html>");
            lblThoiLuong.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblThoiLuong.setFont(fontChu);
            p.add(lblThoiLuong);
        }
        
        String tenGhe = String.join(", ", gheDaChon);
        JLabel lblGhe = new JLabel("<html><strong>Ghế: </strong>" + tenGhe + "</html>");
        lblGhe.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblGhe.setFont(fontChu);
        p.add(lblGhe);

        p.add(Box.createVerticalGlue());

        return p;
    }

	private JPanel panelThanhToan() {
        JPanel pnlThanhToan = new JPanel();
        pnlThanhToan.setLayout(new BoxLayout(pnlThanhToan, BoxLayout.Y_AXIS));
        pnlThanhToan.setBorder(new EmptyBorder(10, 12, 10, 12));
        pnlThanhToan.setBackground(COLOR_BG);

        // --- 1. Thông tin khách hàng ---
        lblThongTinKH = new JLabel("Thông tin khách hàng:");
        lblThongTinKH.setFont(fontTieuDe);
        lblThongTinKH.setAlignmentX(Component.LEFT_ALIGNMENT); // SỬA: Căn trái

        pnlThanhToan.add(lblThongTinKH);
        pnlThanhToan.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Dùng hàm hỗ trợ
        pnlThanhToan.add(createCustomerInfoPanel()); 
        pnlThanhToan.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- 2. Bắp nước ---
        lblBapNuoc = new JLabel("Bắp nước:");
        lblBapNuoc.setFont(fontTieuDe);
        lblBapNuoc.setAlignmentX(Component.LEFT_ALIGNMENT); // SỬA: Căn trái
        
        pnlThanhToan.add(lblBapNuoc);
        pnlThanhToan.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tạo text field (để có thể truy cập từ bên ngoài)
        txtBap = new JTextField(3);
        txtNuoc = new JTextField(3);

        btnTangBap = new JButton("+");
        btnGiamBap = new JButton("-");
        btnTangNuoc = new JButton("+");
        btnGiamNuoc = new JButton("-");
        
        
        pnlThanhToan.add(createFoodItemPanel("Bắp rang", imgBap, txtBap, btnTangBap, btnGiamBap, GIA_BAP));
        pnlThanhToan.add(Box.createRigidArea(new Dimension(0, 5)));

        pnlThanhToan.add(createFoodItemPanel("Nước ngọt", imgNuoc, txtNuoc, btnTangNuoc, btnGiamNuoc, GIA_NUOC));
        pnlThanhToan.add(Box.createRigidArea(new Dimension(0, 90)));

        pnlTamTinh = new JPanel();
        pnlTamTinh.setLayout(new BorderLayout()); 
        pnlTamTinh.setBackground(COLOR_BG);
        pnlTamTinh.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Gọi hàm update lần đầu tiên để hiển thị (với giá trị = 0)
        updateTamTinhPanel(); 
        
        pnlThanhToan.add(pnlTamTinh);

        // --- 3. Các nút ---
        JPanel pnlNut = new JPanel();
        pnlNut.setBackground(COLOR_BG);
        pnlNut.setLayout(new FlowLayout(FlowLayout.RIGHT)); // SỬA: Căn phải (hoặc trái)
        pnlNut.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái
        
        btnTao = new JButton("Tạo đơn hàng");
        btnQuayLai = new JButton("Hủy");
        pnlNut.add(btnQuayLai);
        pnlNut.add(btnTao);
        
        btnTao.addActionListener(this);
        btnQuayLai.addActionListener(this);
        
        pnlThanhToan.add(pnlNut);
        
        // --- 4. Đẩy mọi thứ lên trên ---
        pnlThanhToan.add(Box.createVerticalGlue()); // SỬA: Xóa RigidArea(300)

        return pnlThanhToan;
    }
	
	private JPanel createCustomerInfoPanel() {
        // Panel này chứa 2 cột: (1) Nhãn, (2) Ô nhập liệu
        JPanel pnlForm = new JPanel(new BorderLayout(10, 10));
        pnlForm.setBackground(COLOR_BG);

        // Cột 1: Chứa các nhãn (JLabel)
        JPanel pnlLabels = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlLabels.setBackground(COLOR_BG);
        
        JLabel lblTenKH = new JLabel("Tên khách hàng:");
        JLabel lblSDT = new JLabel("Số điện thoại:");
        JLabel lblPTTT = new JLabel("Phương thức thanh toán:");
        JLabel lblKM = new JLabel("Mã khuyến mãi:");
        
        lblPTTT.setFont(fontChu);
        lblTenKH.setFont(fontChu);
        lblSDT.setFont(fontChu);
        lblKM.setFont(fontChu);
        
        pnlLabels.add(lblTenKH);
        pnlLabels.add(lblSDT);
        pnlLabels.add(lblPTTT);
        pnlLabels.add(lblKM);
        
        // Cột 2: Chứa các ô nhập (JTextField)
        JPanel pnlFields = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlFields.setBackground(COLOR_BG);
        
        this.listPTTT = loadPTTT();
        
        txtTenKH = new JTextField();
        txtSDT = new JTextField();
        cbxPTTT = new JComboBox<>(listPTTT.stream().map(PhuongThucThanhToan::getTenPTTT).toArray(String[]::new));
        
        JPanel pnlKM = new JPanel();
        pnlKM.setLayout(new BoxLayout(pnlKM, BoxLayout.X_AXIS));
        pnlKM.setBackground(COLOR_BG);
        txtKM = new JTextField(10);
        btnKTKM = new JButton("Kiểm tra");
        btnKTKM.addActionListener(this);
        
        lblThongBaoKM = new JLabel("");
        lblThongBaoKM.setFont(new Font("Arial", Font.ITALIC, 12));
        lblThongBaoKM.setForeground(Color.GRAY);
        
        pnlKM.add(txtKM);
        pnlKM.add(btnKTKM);
        pnlKM.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlKM.add(lblThongBaoKM);
        
        pnlFields.add(txtTenKH);
        pnlFields.add(txtSDT);
        pnlFields.add(cbxPTTT);
        pnlFields.add(pnlKM);
        
        // Ghép 2 cột lại
        pnlForm.add(pnlLabels, BorderLayout.WEST);
        pnlForm.add(pnlFields, BorderLayout.CENTER);
        
        // Giới hạn chiều cao tối đa để form không bị co dãn
        pnlForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlForm.getPreferredSize().height));
        pnlForm.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái

        return pnlForm;
    }

	private JPanel createFoodItemPanel(String tenMon, String imgPath, JTextField txtField, JButton btnTang, JButton btnGiam, double gia) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái
        box.setBackground(COLOR_BG);
        
        // Ảnh
        box.add(new JLabel(load.taiHinhAnh(imgPath, BAP_NUOC_SIZE.width, BAP_NUOC_SIZE.height)));
        box.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // Tên
        JLabel lblTen = new JLabel(tenMon);
        lblTen.setFont(fontChu);
        lblTen.setPreferredSize(new Dimension(100, 30)); // Đặt kích thước cố định
        box.add(lblTen);
        
        Dimension btnSize = new Dimension(30, 30);

        btnGiam.addActionListener(this);
        btnGiam.setPreferredSize(btnSize);
        box.add(btnGiam);
        
        // Ô số lượng
        txtField.setText("0");
        txtField.setEditable(false);
        txtField.setHorizontalAlignment(SwingConstants.CENTER);
        // Giới hạn kích thước của text field
        txtField.setMaximumSize(new Dimension(50, 30));
        box.add(txtField);
        
        btnTang.addActionListener(this);
        btnTang.setPreferredSize(btnSize);
        box.add(btnTang);

        box.add(Box.createHorizontalGlue()); // Đẩy các thành phần về bên trái
        
        JLabel lblGiaSP = new JLabel(formatMoney(gia));
        lblGiaSP.setFont(fontChu);
        box.add(lblGiaSP);
        return box;
    }
	
	private JPanel tamTinhPanel() {
	    JPanel pnlTamTinh = new JPanel();
	    pnlTamTinh.setLayout(new BoxLayout(pnlTamTinh, BoxLayout.Y_AXIS));
	    pnlTamTinh.setBackground(COLOR_BG);
	    pnlTamTinh.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
	    pnlTamTinh.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái

	    // --- 1. Tính toán giá trị ---
	    GheDAO gheDAO = new GheDAO();
	    double tongVe = 0;
	    int soVeThuc = 0;

	    Set<String> daTinh = new HashSet<>(); // lưu tất cả ghế đã tính (từng ghế đơn)

	    for (String seatId : gheDaChon) {
	        if (daTinh.contains(seatId)) continue; // nếu ghế đã tính, bỏ qua

	        Ghe g = gheDAO.layGheBangTenGhe(seatId);
	        if (g == null) continue;

	        double phuThu = (g.getLoaiGhe() != null ? g.getLoaiGhe().getPhuThu() : 0);

	        if (g.getTenGhe().contains(",")) {
	            // Ghế đôi: 2 vé + phụ thu 1 lần
	            tongVe += 2 * suatChieuDaChon.getGiaVeCoBan() + phuThu;
	            soVeThuc += 2;

	            // Thêm tất cả ghế thành phần vào set
	            String[] gheThanhPhan = g.getTenGhe().split(",");
	            for (String gheComp : gheThanhPhan) {
	                daTinh.add(gheComp.trim());
	            }
	        } else {
	            // Ghế đơn
	            tongVe += suatChieuDaChon.getGiaVeCoBan() + phuThu;
	            soVeThuc += 1;
	            daTinh.add(seatId);
	        }
	    }


	    // (Lấy soLuongBap, soLuongNuoc từ biến thành viên của lớp)
	    double tongBap = soLuongBap * GIA_BAP;
	    double tongNuoc = soLuongNuoc * GIA_NUOC;

	    double tongTien = tongVe + tongBap + tongNuoc;

	    double giamKM = 0;
	    if (khuyenMaiHopLe != null) {
	        giamKM = khuyenMaiHopLe.getGiaTriKM(); // nếu là số tiền trực tiếp
	        tongTien -= giamKM;
	    }
	    
	    // --- 2. Thêm các hàng vào panel ---
	    
	    // Tiêu đề
	    JLabel lblTitle = new JLabel("TẠM TÍNH");
	    lblTitle.setFont(fontTieuDe);
	    lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
	    pnlTamTinh.add(lblTitle);
	    pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 10)));

	    // Hàng 1: Vé (Luôn hiển thị)
	    String veLabel = String.format("Vé xem phim (x%d)", soVeThuc);
	    pnlTamTinh.add(createTotalRow(veLabel, formatMoney(tongVe), false));
	    pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 5)));

	    // Hàng 2: Bắp (Chỉ hiển thị nếu có)
	    if (soLuongBap > 0) {
	        String lblBap = String.format("Bắp rang (x%d)", soLuongBap);
	        pnlTamTinh.add(createTotalRow(lblBap, formatMoney(tongBap), false));
	        pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 5)));
	    }

	    // Hàng 3: Nước (Chỉ hiển thị nếu có)
	    if (soLuongNuoc > 0) {
	        String lblNuoc = String.format("Nước ngọt (x%d)", soLuongNuoc);
	        pnlTamTinh.add(createTotalRow(lblNuoc, formatMoney(tongNuoc), false));
	    }
	    
	    if (giamKM > 0) {
	        pnlTamTinh.add(createTotalRow("Khuyến mãi", "- " + formatMoney(giamKM), false));
	        pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 5)));
	    }

	    // --- 3. Dòng tổng cộng ---
	    pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 10)));
	    
	    // Thêm đường kẻ ngang
	    JSeparator separator = new JSeparator();
	    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
	    pnlTamTinh.add(separator);
	    
	    pnlTamTinh.add(Box.createRigidArea(new Dimension(0, 10)));

	    // Hàng 4: Tổng
	    pnlTamTinh.add(createTotalRow("TỔNG CỘNG", formatMoney(tongTien), true));

	    // Đẩy tất cả lên trên
	    pnlTamTinh.add(Box.createVerticalGlue());

	    return pnlTamTinh;
	}
	
	private void updateTamTinhPanel() {
	    // 1. Xóa tất cả nội dung cũ bên trong container
	    pnlTamTinh.removeAll();

	    // 2. Gọi hàm factory của bạn để TẠO MỚI panel nội dung
	    //    Hàm tamTinhPanel() của bạn sẽ tự động kiểm tra soLuongBap > 0
	    JPanel newContent = tamTinhPanel(); 

	    // 3. Thêm panel nội dung mới vào container
	    pnlTamTinh.add(newContent, BorderLayout.CENTER);

	    // 4. Vẽ lại giao diện
	    pnlTamTinh.revalidate();
	    pnlTamTinh.repaint();
	}
	
	private JPanel createTotalRow(String labelText, String valueText, boolean isBold) {
	    JPanel row = new JPanel();
	    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
	    row.setBackground(COLOR_BG); // Dùng màu nền chung

	    JLabel lblLabel = new JLabel(labelText);
	    JLabel lblValue = new JLabel(valueText);
	    
	    lblLabel.setFont(fontChu);
	    lblValue.setFont(fontChu);
	    
	    // In đậm nếu là hàng tổng
	    if (isBold) {
	        Font boldFont = lblLabel.getFont().deriveFont(Font.BOLD, 14f);
	        lblLabel.setFont(boldFont);
	        lblValue.setFont(boldFont);
	    }

	    row.add(lblLabel);
	    row.add(Box.createHorizontalGlue()); // Đây là mấu chốt: đẩy giá trị sang phải
	    row.add(lblValue);

	    // Đảm bảo hàng này căn lề trái
	    row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    return row;
	}
	
	private String formatMoney(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.of("vi", "VN"));
        return nf.format(Math.round(v)) + " đ";
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		var event = e.getSource();
		if(event.equals(btnTangBap)){
			soLuongBap++;
			txtBap.setText(String.valueOf(soLuongBap));
			updateTamTinhPanel();
		}
		else if(event.equals(btnGiamBap)){
			if (soLuongBap > 0) {
				soLuongBap--;
				txtBap.setText(String.valueOf(soLuongBap));
				updateTamTinhPanel();
			}
		}
		else if(event.equals(btnTangNuoc)){
			soLuongNuoc++;
			txtNuoc.setText(String.valueOf(soLuongNuoc));
			updateTamTinhPanel();
		}
		else if(event.equals(btnGiamNuoc)){
			if (soLuongNuoc > 0) {
				soLuongNuoc--;
				txtNuoc.setText(String.valueOf(soLuongNuoc));
				updateTamTinhPanel();
			}
		} 
		if(event.equals(btnKTKM)) {
			Connection conn = null;
			try {
				conn = ConnectDB.getConnection();
				KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO(conn);
				String code = txtKM.getText().trim();
	            System.out.print(code);

		        if (code.isEmpty()) {
		            lblThongBaoKM.setText("Vui lòng nhập mã khuyến mãi!");
		            lblThongBaoKM.setForeground(Color.GRAY);
		            khuyenMaiHopLe = null;
		            updateTamTinhPanel();
		            return;
		        }

		        KhuyenMai km = khuyenMaiDAO.findByCode(code);
		        if (km != null) { //them rang buọc  
		            khuyenMaiHopLe = km;
		            lblThongBaoKM.setText("✅ Mã hợp lệ: Giảm " + formatMoney(km.getGiaTriKM()));
		            lblThongBaoKM.setForeground(new Color(0, 128, 0));
		            System.out.print(km.getGiaTriKM());
		        } else {
		            khuyenMaiHopLe = null;
		            lblThongBaoKM.setText("❌ Mã không hợp lệ hoặc đã hết hạn!");
		            lblThongBaoKM.setForeground(Color.RED);
		        }

		        updateTamTinhPanel(); // Cập nhật lại tạm tính
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		        lblThongBaoKM.setText("❌ Lỗi khi kiểm tra mã!");
		        lblThongBaoKM.setForeground(Color.RED);
		    } finally {
		        try { if (conn != null) conn.close(); } catch (SQLException e2) { e2.printStackTrace(); }
		    }
		}
		if(event.equals(btnTao)) {
			Connection conn = null;
			try {
		        conn = ConnectDB.getConnection();
		        conn.setAutoCommit(false);
		        List<Ve> listVe = taoVe(conn);
		        
		        HoaDon hd = taoHoaDon(listVe, conn);
		        conn.commit();
		        
		        GiaoDienDatVeThanhCong frm = new GiaoDienDatVeThanhCong(hd, listVe);
		        frm.setVisible(true);
		        dispose();
			}catch (Exception ex) {
				try { if (conn != null) conn.rollback(); } catch (SQLException e1) { e1.printStackTrace(); }
				ex.printStackTrace();
		    } finally {
		        try { if (conn != null) conn.close(); } catch (SQLException e2) { e2.printStackTrace(); }
		    }
		}
		if(event.equals(btnQuayLai)) {
			GiaoDienChonGhe frm = new GiaoDienChonGhe(suatChieuDaChon.getPhim());
		    frm.setVisible(true);
		    dispose();
		}
	}

	private List<Ve> taoVe(Connection conn) throws SQLException {
	    VeDAO veDAO = new VeDAO(conn);
	    GheDAO gheDAO = new GheDAO(conn);

	    List<Ve> listVe = new ArrayList<>();
	    List<Ghe> listGhe = new ArrayList<>();

	    for (String tenGhe : gheDaChon) {
	        Ghe ghe = gheDAO.layGheBangTenGhe(tenGhe);
	        if (ghe != null) {
	            listGhe.add(ghe);
	        }
	    }

	    for (Ghe ghe : listGhe) {
	        Ve ve = new Ve(
	            9999,
	            suatChieuDaChon,
	            ghe,
	            suatChieuDaChon.getGiaVeCoBan(),
	            LocalDate.now(),
	            suatChieuDaChon.getNgayChieu(),
	            suatChieuDaChon.getGioChieu(),
	            ghe.getTenGhe(),
	            suatChieuDaChon.getPhongChieu().getTenPhong()
	        );
	        listVe.add(ve);
	    }

	    veDAO.taoListVe(listVe);
	    return listVe;
	}
	
	private HoaDon taoHoaDon(List<Ve> listVe, Connection conn) {
		// KH, NV -> HD
		KhachHangDAO khachHangDAO = new KhachHangDAO(conn);
		HoaDonDAO hoaDonDAO = new HoaDonDAO(conn);
		
		String ptttDaChon = (String) cbxPTTT.getSelectedItem();
		PhuongThucThanhToan phuongThucThanhToan = null;
		
		for(PhuongThucThanhToan pttt : listPTTT) {
			if(pttt.getTenPTTT().equals(ptttDaChon)) {
				phuongThucThanhToan = pttt;
				break;
			}
		}
		System.out.print(ptttDaChon);
		
		KhachHang khachHang = new KhachHang(9999, txtTenKH.getText(), txtSDT.getText());
		if(!khachHangDAO.insertKhachHang(khachHang)) {
			System.out.print("loi khi tao KH" + khachHang.getMaKH());
			return null;
		}
		
		HoaDon hd = new HoaDon(9999, LocalDateTime.now(), khachHang, nhanVien, soLuongBap, soLuongNuoc, khuyenMaiHopLe, phuongThucThanhToan);
		if(!hoaDonDAO.taoHoaDon(hd)) {
			System.out.print("loi khi tao HD");
			return null;
		}
		
		ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO(conn);
		chiTietHoaDonDAO.taoChiTietHoaDon(hd, listVe);
		
		return hd;
	}
	
	private List<PhuongThucThanhToan> loadPTTT() {
		Connection conn = null;
		try {
			conn = ConnectDB.getConnection();
			PhuongThucThanhToanDAO phuongThucThanhToanDAO = new PhuongThucThanhToanDAO(conn);
			return phuongThucThanhToanDAO.getAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
