package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import ConnectDB.ConnectDB;
import DAO.*;
import Entity.*;

import java.awt.*;
import java.awt.event.*;
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

    private JComboBox<KhachHang> cboKhachHang = new JComboBox<>();
    private JComboBox<NhanVien> cboNhanVien = new JComboBox<>();
    private JComboBox<KhuyenMai> cboKhuyenMai = new JComboBox<>();
    private JComboBox<PhuongThucThanhToan> cboPTTT = new JComboBox<>();

    private JTextField txtSearch = new JTextField();

    private DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên",
                "Bắp", "Nước", "Khuyến mãi", "PTTT"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);

    // DAO
    private Connection conn;
    private HoaDonDAO hoaDonDAO;
    private KhachHangDAO khachHangDAO;
    private NhanVienDAO nhanVienDAO;
    private KhuyenMaiDAO khuyenMaiDAO;
    private PhuongThucThanhToanDAO ptttDAO;
    private NhanVien nhanVien;
    // Buttons
    private JButton btnFind, btnReload;
    private JButton btnXoaRong, btnSave, btnUpdate, btnDelete;

	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
	
    
    //=========== Constructor ===========

    public GiaoDienQuanLyHoaDon(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
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

        btnFind = taoBtn("Tìm");
        btnFind.addActionListener(this);
        
        btnReload = taoBtn("Tải lại");
        btnReload.addActionListener(this);
        
        
        btnReload.addActionListener(this);
        txtSearch.addActionListener(this);

        right.add(btnFind);
        right.add(btnReload);

        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    //=========== Main Panel ===========

    private JSplitPane buildMainPanel() {
        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        vertical.setResizeWeight(0.55);
        vertical.setBackground(SEC_COLOR);

        JSplitPane top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        top.setResizeWeight(1);

        JPanel formPanel = buildFormPanel();
        top.setLeftComponent(formPanel);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new EmptyBorder(10, 20, 10, 20));
//        tableScroll.setBackground(PRI_COLOR);
        table.setRowHeight(25);
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

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(SEC_COLOR);

        btnXoaRong = taoBtn("Thêm");
        btnSave = taoBtn("Lưu");
        btnUpdate = taoBtn("Sửa");
        btnDelete = taoBtn("Xoá");

        for (JButton b : new JButton[]{btnXoaRong, btnSave, btnUpdate, btnDelete}) {
            b.addActionListener(this);
        }

        actions.add(btnXoaRong);
        actions.add(btnSave);
        actions.add(btnUpdate);
        actions.add(btnDelete);

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
    }

    private void reloadTable() {
        model.setRowCount(0);
        List<HoaDon> list = hoaDonDAO.layTatCaHoaDon();

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
            JOptionPane.showMessageDialog(this, "Đã cập nhật");
            reloadTable();
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

        if (o.equals(btnFind) || o.equals(txtSearch)) search();
        else if (o.equals(btnReload)) reloadTable();
        else if (o.equals(btnXoaRong)) clearForm();
        else if (o.equals(btnSave)) doInsert();
        else if (o.equals(btnUpdate)) doUpdate();
        else if (o.equals(btnDelete)) doDelete();
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
    	btn.addActionListener(this);
    	btn.setOpaque(true);
    	btn.setBorderPainted(false);
    	btn.setContentAreaFilled(true);
    	return btn;
    }
}
