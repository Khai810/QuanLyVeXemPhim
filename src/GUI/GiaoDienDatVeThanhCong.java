package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;

import ConnectDB.ConnectDB;
import DAO.ChiTietHoaDonDAO;

import java.util.List;

import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.NhanVien;
import Entity.Ve;

public class GiaoDienDatVeThanhCong extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JLabel lblLogo;
    private JLabel lblMaGD, lblPhuongThuc, lblSoTien, lblSuatChieu, lblNgayChieu
    , lblPhongChieu, lblTenKH, lblTenNV, lblSDTKH;
    private JLabel valMaGD, valPhuongThuc, valSoTien, valSuatChieu, valNgayChieu
    , valPhongChieu, valTenKH, valTenNV, valSDTKH;
    private JButton btnInVe, btnHoanTat, btnInHoaDon;
    private JPanel pnlInfo;

	HoaDon hoaDon;
	NhanVien nhanVien;
	List<ChiTietHoaDon> listCTHD;
	private static final String FILE_PATH = "inVe/";
	
	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    
    private static final Font fontChu = new Font("Segoe UI", Font.BOLD, 14);
    
	public GiaoDienDatVeThanhCong(HoaDon hoaDon, NhanVien nhanVien) {
		super();
		this.hoaDon = hoaDon;
		this.nhanVien = nhanVien;
		
		MenuChinh menuBar = new MenuChinh(this, nhanVien);
		this.setJMenuBar(menuBar);
		MenuToggleUtil.addToggleSupport(this, menuBar);
		
		loadCTHD();
		
        LoadHinhAnh loadHinhAnh = new LoadHinhAnh();
        lblLogo = new JLabel(loadHinhAnh.taiHinhAnh("/img/logo.png", 100, 100), SwingConstants.CENTER);
        lblLogo.setBackground(PRI_COLOR);
        lblLogo.setOpaque(false);
        
        // ======= THÔNG TIN HÓA ĐƠN =======
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        lblMaGD = makeLabel("Mã hóa đơn");
        lblPhuongThuc = makeLabel("Phương thức");
        lblSoTien = makeLabel("Số tiền");
        lblSuatChieu = makeLabel("Suất chiếu");
        lblNgayChieu = makeLabel("Ngày chiếu");
        lblPhongChieu = makeLabel("Phòng");
        lblTenKH = makeLabel("Tên khách hàng");
        lblSDTKH = makeLabel("Số điện thoại khách hàng");
        lblTenNV = makeLabel("Tên nhân viên");
        
        valMaGD = makeValue(hoaDon.getMaHD() + "");
        valPhuongThuc = makeValue(hoaDon.getPhuongThucThanhToan().getTenPTTT());
        valSoTien = makeValue(String.format("%,.0f đ", hoaDon.tinhTong(listCTHD)));
        valSoTien.setForeground(new Color(255, 130, 0));
        
        valSuatChieu = makeValue(listCTHD.get(0).getVe().getGioChieu() + "");
        valNgayChieu = makeValue(listCTHD.get(0).getVe().getNgayChieu() + "");
        valPhongChieu = makeValue(listCTHD.get(0).getVe().getTenPhongChieu() + "");
        valTenKH = makeValue(hoaDon.getKhachHang().getTenKH() + "");
        valSDTKH = makeValue(hoaDon.getKhachHang().getSDT() + "");
        valTenNV = makeValue(hoaDon.getNhanVien().getTenNhanVien());
        
        pnlInfo = taopnlhoaDon(); 
        
        
        // ======= BUTTONS =======
        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(PRI_COLOR);
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnInVe = taoBtn("🖨 In Vé");

        btnHoanTat = taoBtn("Hoàn Tất");
        
        btnInHoaDon = taoBtn("🖨 In hóa đơn");
        
        pnlBottom.add(btnInHoaDon);
        pnlBottom.add(Box.createRigidArea(new Dimension(15, 0)));
        pnlBottom.add(btnInVe);
        pnlBottom.add(Box.createRigidArea(new Dimension(15, 0)));
        pnlBottom.add(btnHoanTat);

        btnInHoaDon.addActionListener(this);
        btnHoanTat.addActionListener(this);
        btnInVe.addActionListener(this);

        // ======= ADD TO FRAME =======
        add(pnlInfo, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1400, 800);
		setLocationRelativeTo(null);
}

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontChu);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    private JLabel makeValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontChu);
        lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		var event = e.getSource();
		if(event.equals(btnHoanTat)) {
			GiaoDienChonPhim frmChonPhim = new GiaoDienChonPhim(nhanVien);
			frmChonPhim.setVisible(true);
			dispose();
		}
		else if(event.equals(btnInVe)) {
			for(Ve ve : listCTHD.stream().map(ChiTietHoaDon::getVe).toList()) {
				JFrame frame = new JFrame("Vé xem phim");
				VeXemPhim pnlVe = new VeXemPhim(ve);
				frame.add(pnlVe);
		        frame.pack();
		        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        frame.setLocationRelativeTo(null);
		        frame.setVisible(true);
		        try {
		        	pnlVe.saveAsImage(FILE_PATH + "ve" + ve.getMaVe() + ".png");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if (event.equals(btnInHoaDon)) {
			try {
				JPanel pnlBill = taopnlhoaDon();
				
				inHoaDon(pnlBill, FILE_PATH + "hoaDon" + this.hoaDon.getMaHD() + ".png");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void loadCTHD() {
		Connection conn = null;
		try {
			conn = ConnectDB.getConnection();
			ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO(conn);
			this.listCTHD = cthdDAO.layChitiethoadon(hoaDon.getMaHD());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JButton taoBtn(String tenBtn) {
		JButton btn = new JButton(tenBtn);
        
		btn.setBackground(RED_COLOR);
		btn.setForeground(BTN_COLOR);
		btn.setPreferredSize(new Dimension(200, 50));
		btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
		btn.setOpaque(true);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(true);
		btn.setFocusPainted(false);

		return btn;
	}
	
	private int addDongKe(JPanel pnl, GridBagConstraints gbc, int viTri) {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        
        // Lưu lại các cài đặt GBC cũ
        int originalGridWidth = gbc.gridwidth;
        int originalFill = gbc.fill;
        Insets originalInsets = gbc.insets;

        // Cấu hình GBC cho JSeparator
        gbc.gridx = 0;
        gbc.gridy = viTri;
        gbc.gridwidth = 2; // Cho phép JSeparator kéo dài qua 2 cột
        gbc.fill = GridBagConstraints.HORIZONTAL; // Cho phép JSeparator lấp đầy theo chiều ngang
        gbc.insets = new Insets(5, 0, 5, 0); // Thêm một chút đệm trên và dưới

        pnl.add(separator, gbc);

        // Khôi phục lại các cài đặt GBC cho các component tiếp theo
        gbc.gridwidth = originalGridWidth;
        gbc.fill = originalFill;
        gbc.insets = originalInsets;
        
        // Trả về vị trí (gridy) tiếp theo
        return viTri + 1;
    }
	
	public void inHoaDon(JPanel panel, String filePath) throws Exception {
		panel.setSize(panel.getPreferredSize());
	    panel.doLayout();
		int w = panel.getWidth();
	    int h = panel.getHeight();

	    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = img.createGraphics();
	    panel.printAll(g2);
	    g2.dispose();

	    ImageIO.write(img, "png", new File(filePath));
	}
	
	private JPanel taopnlhoaDon() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBackground(SEC_COLOR);
		pnl.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;        
        
        int viTri = 0;
        
        gbc.gridx = 0; gbc.gridy = viTri++; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        pnl.add(lblLogo, gbc);
        
        JLabel lblTitle = new JLabel("HÓA ĐƠN THANH TOÁN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(RED_COLOR);
        
        gbc.gridy = viTri++; gbc.insets = new Insets(2, 8, 15, 8); // Padding lớn hơn
        pnl.add(lblTitle, gbc);
        
        // Reset về cấu hình mặc định
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 8, 7, 8);
        
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblMaGD, gbc);
        gbc.gridx = 1; pnl.add(valMaGD, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblTenKH, gbc);
        gbc.gridx = 1; pnl.add(valTenKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblSDTKH, gbc);
        gbc.gridx = 1; pnl.add(valSDTKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblPhuongThuc, gbc);
        gbc.gridx = 1; pnl.add(valPhuongThuc, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblSuatChieu, gbc);
        gbc.gridx = 1; pnl.add(valSuatChieu, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblNgayChieu, gbc);
        gbc.gridx = 1; pnl.add(valNgayChieu, gbc);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblPhongChieu, gbc);
        gbc.gridx = 1; pnl.add(valPhongChieu, gbc);

        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblTenNV, gbc);
        gbc.gridx = 1; pnl.add(valTenNV, gbc);
        
        viTri = addDongKe(pnl, gbc, viTri);

        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(makeLabel("Vé "), gbc);
        gbc.gridx = 1; pnl.add(makeValue(" "), gbc);

       
        for(ChiTietHoaDon cthd : listCTHD) {
            String tenGhe = cthd.getVe().getTenGhe();
            double donGia = cthd.getDonGiaBan();
            
        	gbc.gridx = 0; gbc.gridy = viTri++;
        	pnl.add(makeLabel("    • " + tenGhe), gbc);
            gbc.gridx = 1;
            pnl.add(makeValue(String.format("%,.0f đ", donGia)), gbc);
        }
        
        if(hoaDon.getSoLuongBap() > 0) {
        	gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(makeLabel("Bắp x" + hoaDon.getSoLuongBap() ), gbc);
            gbc.gridx = 1; pnl.add(makeValue(String.format("%,.0f đ", hoaDon.tinhGiaBap())), gbc);
        }
        
        if(hoaDon.getSoLuongNuoc() > 0) {
        	gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(makeLabel("Nước x" + hoaDon.getSoLuongNuoc() ), gbc);
            gbc.gridx = 1; pnl.add(makeValue(String.format("%,.0f đ", hoaDon.tinhGiaNuoc())), gbc);
        }
        
        if(hoaDon.getKhuyenMai() != null) {
        	gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(makeLabel("Khuyến mãi " + hoaDon.getKhuyenMai().getTenKM() ), gbc);
            gbc.gridx = 1; pnl.add(makeValue("- " + String.format("%,.0f đ", hoaDon.getKhuyenMai().getGiaTriKM())), gbc);
        }
        
        viTri = addDongKe(pnl, gbc, viTri);
        
        gbc.gridx = 0; gbc.gridy = viTri++; pnl.add(lblSoTien, gbc);
        gbc.gridx = 1; pnl.add(valSoTien, gbc);
      
        return pnl;
	}
}
