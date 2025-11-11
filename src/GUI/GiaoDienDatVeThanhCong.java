package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.List;

import Entity.HoaDon;
import Entity.Ve;

public class GiaoDienDatVeThanhCong extends JFrame implements ActionListener{
	private JLabel lblIcon, lblTitle, lblSubtitle;
    private JLabel lblMaGD, lblPhuongThuc, lblSoTien, lblGhe;
    private JLabel valMaGD, valPhuongThuc, valSoTien, valGhe;
    private JButton btnInVe, btnHoanTat;
	    
	HoaDon hoaDon;
	List<Ve> listVe;
	
	private static final Color SEC_COLOR = new Color(28, 32, 40);
	private static final Color PRI_COLOR = new Color(18, 22, 28);
	
	public GiaoDienDatVeThanhCong(HoaDon hoaDon, List<Ve> listVe) {
		super();
		this.hoaDon = hoaDon;
		this.listVe = listVe;
		
		// ======= ICON + TITLE =======
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(PRI_COLOR);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        lblIcon = new JLabel("✔", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 70));
        lblIcon.setForeground(new Color(0, 200, 100));

        lblTitle = new JLabel("Thanh Toán Thành Công!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        lblSubtitle = new JLabel("Vé của bạn đã được xác nhận", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(150, 150, 150));

        pnlTop.add(lblIcon, BorderLayout.NORTH);
        pnlTop.add(lblTitle, BorderLayout.CENTER);
        pnlTop.add(lblSubtitle, BorderLayout.SOUTH);

        // ======= THÔNG TIN HÓA ĐƠN =======
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBackground(SEC_COLOR);
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        lblMaGD = makeLabel("Mã giao dịch");
        lblPhuongThuc = makeLabel("Phương thức");
        lblSoTien = makeLabel("Số tiền");
        lblGhe = makeLabel("Ghế");

        valMaGD = makeValue("HD-" + hoaDon.getMaHD());
//        valPhuongThuc = makeValue(hoaDon.getPhuongThucThanhToan().getTenPTTT());
        valPhuongThuc = makeValue("VL");
        valSoTien = makeValue(String.format("%,.0f đ", tinhTongTien(hoaDon, listVe)));
        valSoTien.setForeground(new Color(255, 130, 0));

        valGhe = makeValue(layDanhSachGhe(listVe));

        gbc.gridx = 0; gbc.gridy = 0; pnlInfo.add(lblMaGD, gbc);
        gbc.gridx = 1; pnlInfo.add(valMaGD, gbc);
        gbc.gridx = 0; gbc.gridy = 1; pnlInfo.add(lblPhuongThuc, gbc);
        gbc.gridx = 1; pnlInfo.add(valPhuongThuc, gbc);
        gbc.gridx = 0; gbc.gridy = 2; pnlInfo.add(lblSoTien, gbc);
        gbc.gridx = 1; pnlInfo.add(valSoTien, gbc);
        gbc.gridx = 0; gbc.gridy = 3; pnlInfo.add(lblGhe, gbc);
        gbc.gridx = 1; pnlInfo.add(valGhe, gbc);

        // ======= BUTTONS =======
        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(new Color(18, 22, 28));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnInVe = new JButton("🖨 In Vé");
        btnInVe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnInVe.setBackground(new Color(40, 45, 55));
        btnInVe.setForeground(Color.WHITE);
        btnInVe.setFocusPainted(false);
        btnInVe.setPreferredSize(new Dimension(150, 45));

        btnHoanTat = new JButton("Hoàn Tất");
        btnHoanTat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHoanTat.setBackground(new Color(255, 100, 0));
        btnHoanTat.setForeground(Color.WHITE);
        btnHoanTat.setFocusPainted(false);
        btnHoanTat.setPreferredSize(new Dimension(150, 45));

        pnlBottom.add(btnInVe);
        pnlBottom.add(Box.createRigidArea(new Dimension(15, 0)));
        pnlBottom.add(btnHoanTat);

        // ======= ADD TO FRAME =======
        add(pnlTop, BorderLayout.NORTH);
        add(pnlInfo, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
        
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1400, 800);
		setLocationRelativeTo(null);
}

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(180, 180, 180));
        return lbl;
    }

    private JLabel makeValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private double tinhTongTien(HoaDon hd, List<Ve> listVe) {
        double tongVe = listVe.stream().mapToDouble(Ve::getGiaVe).sum();
        double tongBap = hd.getSoLuongBap() * 50000;
        double tongNuoc = hd.getSoLuongNuoc() * 30000;
        return tongVe + tongBap + tongNuoc;
    }

    private String layDanhSachGhe(List<Ve> listVe) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listVe.size(); i++) {
            sb.append(listVe.get(i).getTenGhe());
            if (i < listVe.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		var event = e.getSource();
		if(event.equals(btnHoanTat)) {
			GiaoDienChonPhim frmChonPhim = new GiaoDienChonPhim();
			frmChonPhim.setVisible(true);
			dispose();
		}
	}
	
    
}
