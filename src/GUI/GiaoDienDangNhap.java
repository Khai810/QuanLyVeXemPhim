package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import ConnectDB.ConnectDB;
import DAO.NhanVienDAO;
import Entity.NhanVien;

@SuppressWarnings("serial")
public class GiaoDienDangNhap extends JFrame implements ActionListener{
	
	JTextField txtTaiKhoan;
	JLabel lblTaiKhoan, lblMatKhau, lblLogo, lblTieuDe;
	JPasswordField txtMatKhau;
	JButton btnDangNhap;
	JPanel pnlCen, pnlNorth, pnlSouth;
	
	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    
    private static final Font fontChu = new Font("Segoe UI", Font.BOLD, 14);

    
	public GiaoDienDangNhap() {
		super();
		
		pnlNorth = new JPanel();
		pnlNorth.setBackground(PRI_COLOR);
		LoadHinhAnh loadHinhAnh = new LoadHinhAnh();
        lblLogo = new JLabel(loadHinhAnh.taiHinhAnh("/img/logo.png", 100, 100), SwingConstants.CENTER);
        lblLogo.setBackground(PRI_COLOR);
        lblLogo.setOpaque(false);
		
        lblTieuDe = new JLabel("ĐĂNG NHẬP");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTieuDe.setForeground(RED_COLOR);
        
        pnlNorth.add(lblLogo);
        pnlNorth.add(lblTieuDe);
        
		pnlCen = new JPanel(new GridLayout(2, 2, 30, 30));
		pnlCen.setBackground(SEC_COLOR);
		pnlCen.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		pnlCen.setBorder(new EmptyBorder(20, 20, 10, 20));
		
        txtTaiKhoan = new JTextField(30);
		txtMatKhau = new JPasswordField(30);
		
		lblTaiKhoan = makeLabel("Tài khoản:");
		lblMatKhau = makeLabel("Mật khẩu:");
		
		pnlCen.add(lblTaiKhoan);
        pnlCen.add(txtTaiKhoan);
        
        pnlCen.add(lblMatKhau);
        pnlCen.add(txtMatKhau);
		
        pnlSouth = new JPanel();
        pnlSouth.setBackground(PRI_COLOR);
        pnlNorth.setBorder(new EmptyBorder(20, 0, 10, 0));
        btnDangNhap = new JButton("Đăng Nhập");
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnDangNhap.setBackground(RED_COLOR);
        btnDangNhap.setForeground(BTN_COLOR);
        btnDangNhap.setPreferredSize(new Dimension(180, 40));

        btnDangNhap.setOpaque(true);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setContentAreaFilled(true);
		btnDangNhap.addActionListener(this);
        
        pnlSouth.add(btnDangNhap);
        
        add(pnlNorth, BorderLayout.NORTH);
        add(pnlCen, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);
        
		setSize(500, 350);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); 
	}
	
	private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontChu);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		var event = e.getSource();
		
		if(event.equals(btnDangNhap)) {
			try {
				Connection conn = ConnectDB.getConnection();
				NhanVienDAO nhanVienDAO = new NhanVienDAO(conn);
				NhanVien nhanVien = nhanVienDAO.dangNhap(txtTaiKhoan.getText().trim(), txtMatKhau.getText().trim());

				if(nhanVien == null) {
					JOptionPane.showMessageDialog(this, "Sai tài khoản / mật khẩu !!!");
					txtMatKhau.setText("");
					txtMatKhau.requestFocus();
					return;
				}
				else {
					System.out.print(nhanVien.getMaNhanVien());
					GiaoDienChonPhim frmChonPhim = new GiaoDienChonPhim(nhanVien);
					frmChonPhim.setVisible(true);
					dispose();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	
}
