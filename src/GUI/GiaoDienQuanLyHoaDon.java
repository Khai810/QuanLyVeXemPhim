package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import ConnectDB.ConnectDB;
import DAO.*;
import Entity.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("serial")
public class GiaoDienQuanLyHoaDon extends JFrame implements ActionListener {

    //=========== Fields ===========

    private JTextField txtMaHD = new JTextField();
    private JTextField txtNgayLap = new JTextField();
    private JTextField txtSoLuongBap = new JTextField();
    private JTextField txtSoLuongNuoc = new JTextField();
    private JTextField txtTongTien = new JTextField();
    
    private JComboBox<KhachHang> cboKhachHang = new JComboBox<>();
    private JComboBox<NhanVien> cboNhanVien = new JComboBox<>();
    private JComboBox<KhuyenMai> cboKhuyenMai = new JComboBox<>();
    private JComboBox<PhuongThucThanhToan> cboPTTT = new JComboBox<>();

    private JTextField txtSearch = new JTextField();
    private JPanel topRight;

    private DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên",
                "Bắp", "Nước", "Khuyến mãi", "PTTT", "Tổng tiền"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);
    private JPanel pnlHoaDon;
    // DAO
    private Connection conn;
    private HoaDonDAO hoaDonDAO;
    private KhachHangDAO khachHangDAO;
    private NhanVienDAO nhanVienDAO;
    private KhuyenMaiDAO khuyenMaiDAO;
    private PhuongThucThanhToanDAO ptttDAO;

    // Buttons
    private JButton btnTim, btnTaiLai;
    private JButton btnThem, btnLuu, btnSua, btnXoa, btnInHoaDon;
    private static final Font fontChu = new Font("Segoe UI", Font.BOLD, 14);
    private static final String FILE_PATH = "inVe/";

	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
	
    
    //=========== Constructor ===========

    public GiaoDienQuanLyHoaDon(NhanVien nhanVien) {
    	try {
            this.conn = ConnectDB.getConnection();
            this.hoaDonDAO = new HoaDonDAO(conn);
            this.khachHangDAO = new KhachHangDAO(conn);
            this.nhanVienDAO = new NhanVienDAO(conn);
            this.khuyenMaiDAO = new KhuyenMaiDAO(conn);
            this.ptttDAO = new PhuongThucThanhToanDAO(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        MenuChinh menuBar = new MenuChinh(this, nhanVien);
		this.setJMenuBar(menuBar);
		MenuToggleUtil.addToggleSupport(this, menuBar);
		
        setBackground(new Color(245, 245, 245));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        loadComboData();
        reloadTable();
        
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Căn giữa màn hình

    }

    //=========== Top Search ===========

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(PRI_COLOR);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel left = new JPanel(new GridLayout(1, 2, 6, 6));
        left.setBackground(PRI_COLOR);
        left.add(new JLabel("Tìm:"));
        left.add(txtSearch);
        panel.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(PRI_COLOR);

        btnTim = taoBtn("Tìm");
        btnTim.addActionListener(this);
        
        btnTaiLai = taoBtn("Tải lại");
        btnTaiLai.addActionListener(this);
        
        
        btnTaiLai.addActionListener(this);
        txtSearch.addActionListener(this);

        right.add(btnTim);
        right.add(btnTaiLai);

        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    //=========== Main Panel ===========

    private JSplitPane buildMainPanel() {
        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        vertical.setResizeWeight(0.55);
        vertical.setBackground(SEC_COLOR);
        vertical.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JSplitPane top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        top.setResizeWeight(1);
        
        JPanel formPanel = buildFormPanel();
        topRight = new JPanel(new BorderLayout());
        topRight.setBackground(SEC_COLOR);
        topRight.setPreferredSize(new Dimension(550,710));

        top.setLeftComponent(formPanel);
        top.setRightComponent(topRight);
        
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        tableScroll.setBackground(PRI_COLOR);;
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setGridColor(Color.LIGHT_GRAY);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected(table.getSelectedRow());
        });

        vertical.setTopComponent(top);
        vertical.setBottomComponent(tableScroll);

        return vertical;
    }

    //=========== Form ===========

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(SEC_COLOR);
        p.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        gc.gridy = r++; addRow(p, gc, "Mã HĐ", txtMaHD, true);
        gc.gridy = r++; addRow(p, gc, "Ngày lập", txtNgayLap, false);
        gc.gridy = r++; addRow(p, gc, "Khách hàng", cboKhachHang, false);
        gc.gridy = r++; addRow(p, gc, "Nhân viên", cboNhanVien, false);
        gc.gridy = r++; addRow(p, gc, "Số bắp", txtSoLuongBap, false);
        gc.gridy = r++; addRow(p, gc, "Số nước", txtSoLuongNuoc, false);
        gc.gridy = r++; addRow(p, gc, "Khuyến mãi", cboKhuyenMai, false);
        gc.gridy = r++; addRow(p, gc, "PT thanh toán", cboPTTT, false);
        gc.gridy = r++; addRow(p, gc, "Tổng tiền", txtTongTien, true);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(SEC_COLOR);

        btnThem = taoBtn("Thêm");
        btnLuu = taoBtn("Lưu");
        btnSua = taoBtn("Sửa");
        btnXoa = taoBtn("Xoá");
        btnInHoaDon = taoBtn("In HĐ");
        for (JButton b : new JButton[]{btnThem, btnLuu, btnSua, btnXoa, btnInHoaDon}) {
            b.addActionListener(this);
            actions.add(b);
        }

        gc.gridy = r++;
        gc.gridx = 0;
        gc.gridwidth = 2;
        p.add(actions, gc);

        return p;
    }

    // add a row in Form
    private void addRow(JPanel p, GridBagConstraints gc, String lbl, JComponent field, boolean readOnly) {
        gc.gridx = 0; gc.weightx = 0;
        p.add(new JLabel(lbl), gc);

        gc.gridx = 1; gc.weightx = 1;
        p.add(field, gc);

        if (field instanceof JTextField)
            ((JTextField) field).setEditable(!readOnly);
    }

    //=========== DAO LOAD ===========

    private void loadComboData() {
        cboKhachHang.removeAllItems();
        cboNhanVien.removeAllItems();
        cboKhuyenMai.removeAllItems();
        cboPTTT.removeAllItems();

        for (KhachHang k : khachHangDAO.layTatCaKhachHang()) cboKhachHang.addItem(k);
        for (NhanVien n : nhanVienDAO.layTatCaNhanVien()) cboNhanVien.addItem(n);
        for (KhuyenMai km : khuyenMaiDAO.layTatCaKhuyenMai()) cboKhuyenMai.addItem(km);
        for (PhuongThucThanhToan p : ptttDAO.layTatCaPTTT()) cboPTTT.addItem(p);
        
        clearForm();
    }

    private void reloadTable() {
        model.setRowCount(0);
        List<HoaDon> list = hoaDonDAO.layTatCaHoaDon();

        for (HoaDon h : list) {
        	ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO(conn);
			List<ChiTietHoaDon> listCTHD = chiTietHoaDonDAO.layChitiethoadon(h.getMaHD());
			
            model.addRow(new Object[]{
                    h.getMaHD(),
                    h.getNgayLapHoaDon().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    h.getKhachHang() == null ? "" : h.getKhachHang().getTenKH(),
                    h.getNhanVien() == null ? "" : h.getNhanVien().getTenNhanVien(),
                    h.getSoLuongBap(),
                    h.getSoLuongNuoc(),
                    h.getKhuyenMai() == null ? "" : h.getKhuyenMai().getTenKM(),
                    h.getPhuongThucThanhToan() == null ? "" : h.getPhuongThucThanhToan().getTenPTTT(),
                    String.format("%,.0f đ", h.tinhTong(listCTHD))
            });
        }
    }

    //=========== CRUD ===========

    private HoaDon readForm(boolean needId) {
        try {
            Integer ma = needId ? Integer.parseInt(txtMaHD.getText().trim()) : null;

            LocalDateTime ngayLap = txtNgayLap.getText().isBlank() ?
                    LocalDateTime.now() :
                    LocalDateTime.parse(txtNgayLap.getText().trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            int bap = Integer.parseInt(txtSoLuongBap.getText().trim());
            int nuoc = Integer.parseInt(txtSoLuongNuoc.getText().trim());

            HoaDon hd = new HoaDon(
                ma,
                ngayLap,
                (KhachHang) cboKhachHang.getSelectedItem(),
                (NhanVien) cboNhanVien.getSelectedItem(),
                bap,
                nuoc,
                (KhuyenMai) cboKhuyenMai.getSelectedItem(),
                (PhuongThucThanhToan) cboPTTT.getSelectedItem()
            );
            return hd;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage());
            return null;
        }
    }

    private void doInsert() {
        HoaDon h = readForm(false);
        if (h == null) return;

        try {
            hoaDonDAO.taoHoaDon(h);
            JOptionPane.showMessageDialog(this, "Đã thêm hoá đơn");
            reloadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + e.getMessage());
        }
    }

    private void doUpdate() {
        HoaDon h = readForm(true);
        if (h == null) return;

        try {
            hoaDonDAO.capNhatHoaDon(h);
            reloadTable();
            
            List<ChiTietHoaDon> listCTHD = new ChiTietHoaDonDAO(conn).layChitiethoadon(h.getMaHD());
            pnlHoaDon = taopnlhoaDon(h, listCTHD);
            // Xóa nội dung cũ
            topRight.removeAll();
            // Thêm hóa đơn mới
            topRight.add(new JScrollPane(pnlHoaDon), BorderLayout.CENTER);

            // Cập nhật UI
            topRight.revalidate();
            topRight.repaint();
            JOptionPane.showMessageDialog(this, "Đã cập nhật");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
        }
    }

    private void doDelete() {
        if (txtMaHD.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn hoá đơn");
            return;
        }

        int c = JOptionPane.showConfirmDialog(this, "Xoá hoá đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;

        try {
            hoaDonDAO.xoaHoaDon(Integer.parseInt(txtMaHD.getText().trim()));
            JOptionPane.showMessageDialog(this, "Đã xoá");
            reloadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xoá: " + e.getMessage());
        }
    }

    //=========== Table selection fill form ===========

    private void onRowSelected(int row) {
        if (row < 0) return;

        int ma = (Integer) model.getValueAt(row, 0);
        HoaDon h = hoaDonDAO.layHoaDonBangMaHoaDon(ma);
        if (h == null) return;

        txtMaHD.setText(String.valueOf(h.getMaHD()));
        txtNgayLap.setText(h.getNgayLapHoaDon().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        txtSoLuongBap.setText(String.valueOf(h.getSoLuongBap()));
        txtSoLuongNuoc.setText(String.valueOf(h.getSoLuongNuoc()));

        cboKhachHang.setSelectedItem(h.getKhachHang());
        cboNhanVien.setSelectedItem(h.getNhanVien());
        cboKhuyenMai.setSelectedItem(h.getKhuyenMai());
        cboPTTT.setSelectedItem(h.getPhuongThucThanhToan());
        
        List<ChiTietHoaDon> listCTHD = new ChiTietHoaDonDAO(conn).layChitiethoadon(h.getMaHD());
        
        txtTongTien.setText(String.format("%,.0f đ", h.tinhTong(listCTHD)));
        pnlHoaDon = taopnlhoaDon(h, listCTHD);

        // Xóa nội dung cũ
        topRight.removeAll();
        // Thêm hóa đơn mới
        topRight.add(new JScrollPane(pnlHoaDon), BorderLayout.CENTER);

        // Cập nhật UI
        topRight.revalidate();
        topRight.repaint();


    }

    private void clearForm() {
        txtMaHD.setText("");
        txtNgayLap.setText("");
        txtSoLuongBap.setText("");
        txtSoLuongNuoc.setText("");

        cboKhachHang.setSelectedIndex(-1);
        cboNhanVien.setSelectedIndex(-1);
        cboKhuyenMai.setSelectedIndex(-1);
        cboPTTT.setSelectedIndex(-1);

        table.clearSelection();
    }

    //=========== Action Handling ===========

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnTim) || o.equals(txtSearch)) search();
        else if (o.equals(btnTaiLai)) reloadTable();
        else if (o.equals(btnThem)) clearForm();
        else if (o.equals(btnLuu)) doInsert();
        else if (o.equals(btnSua)) doUpdate();
        else if (o.equals(btnXoa)) doDelete();
        else if (o.equals(btnInHoaDon)) {
			try {
				JFrame frame = new JFrame("Hóa đơn" + txtMaHD.getText());
				frame.add(pnlHoaDon);
		        frame.pack();
		        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        frame.setLocationRelativeTo(null);
		        frame.setVisible(true);
		        
				inHoaDon(pnlHoaDon, FILE_PATH + "hoaDon" + txtMaHD.getText() + ".png");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }

    private void search() {
        String k = txtSearch.getText().trim();
        model.setRowCount(0);

        List<HoaDon> list = hoaDonDAO.search(k);

        for (HoaDon h : list) {
            model.addRow(new Object[]{
                    h.getMaHD(),
                    h.getNgayLapHoaDon().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    h.getKhachHang() == null ? "" : h.getKhachHang().getTenKH(),
                    h.getNhanVien() == null ? "" : h.getNhanVien().getTenNhanVien(),
                    h.getSoLuongBap(),
                    h.getSoLuongNuoc(),
                    h.getKhuyenMai() == null ? "" : h.getKhuyenMai().getTenKM(),
                    h.getPhuongThucThanhToan() == null ? "" : h.getPhuongThucThanhToan().getTenPTTT()
            });
        }
    }
    
    private JButton taoBtn(String ten) {
    	JButton btn = new JButton(ten);
    	btn.setBackground(RED_COLOR);
    	btn.setForeground(BTN_COLOR);
    	btn.setPreferredSize(new Dimension(90, 40));
    	btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    private JPanel taopnlhoaDon(HoaDon hoaDon, List<ChiTietHoaDon> listCTHD) {
    	JLabel lblMaGD, lblPhuongThuc, lblSoTien, lblSuatChieu, lblNgayChieu
        , lblPhongChieu, lblTenKH, lblTenNV, lblSDTKH;
        JLabel valMaGD, valPhuongThuc, valSoTien, valSuatChieu, valNgayChieu
        , valPhongChieu, valTenKH, valTenNV, valSDTKH;
        
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
        
        LoadHinhAnh loadHinhAnh = new LoadHinhAnh();
        JLabel lblLogo = new JLabel(loadHinhAnh.taiHinhAnh("/img/logo.png", 100, 100), SwingConstants.CENTER);
        lblLogo.setBackground(PRI_COLOR);
        lblLogo.setOpaque(false);
        
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
