package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ConnectDB.ConnectDB;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.ChiTietHoaDonDAO;
import DAO.HoaDonDAO;
import DAO.KhachHangDAO;
import DAO.NhanVienDAO;
import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.KhachHang;
import Entity.NhanVien;

@SuppressWarnings("serial")
public class GiaoDienQuanLyKhachHang extends JFrame implements ActionListener {

    // ======== Form fields ========
    private JTextField txtMaKH = new JTextField();
    private JTextField txtTenKH = new JTextField();
    private JTextField txtSDT = new JTextField();
    
    
    private JTextField txtSearch = new JTextField();
    
    private DefaultTableModel modelKH = new DefaultTableModel(
        new Object[]{"Mã khách hàng", "Tên khách hàng", "Số Điện thoại"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
        private static final long serialVersionUID = 1L;
    };
    
    private DefaultTableModel modelHD = new DefaultTableModel(
            new Object[]{"Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
    
    private JTable tableKH = new JTable(modelKH);
    private JTable tableHD = new JTable(modelHD);

    // DAO
    Connection conn;
    private KhachHangDAO khachHangDAO;
    private HoaDonDAO hoaDonDAO;

    // Buttons
    private JButton btnTim, btnTaiLai;
    private JButton btnThem, btnLuu, btnSua, btnXoa;

    private static final Dimension POSTER_SIZE = new Dimension(320, 450);
    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;

    public GiaoDienQuanLyKhachHang(NhanVien nhanVien) {
    	try {
			this.conn = ConnectDB.getConnection();
			this.khachHangDAO = new KhachHangDAO(conn);
			this.hoaDonDAO = new HoaDonDAO(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	MenuChinh menuBar = new MenuChinh(this, nhanVien);
		this.setJMenuBar(menuBar);
		MenuToggleUtil.addToggleSupport(this, menuBar);
    	
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(PRI_COLOR);
		setLocationRelativeTo(null); // Căn giữa màn hình

        // ===== Top bar search + buttons =====
        add(buildTopBar(), BorderLayout.NORTH);

        // ===== Main split: form left + table below =====
        add(buildMainPanel(), BorderLayout.CENTER);

        // Load data
        reloadTableKH();
    }

    // ===== Top search bar =====
    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(PRI_COLOR);

        JPanel left = new JPanel(new GridLayout(1,2,6,6));
        left.setBackground(PRI_COLOR);
        left.add(new JLabel("Tìm:"));
        left.add(txtSearch);
        panel.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,0));
        right.setBackground(PRI_COLOR);

        btnTim = taoBtn("Tìm");
        btnTaiLai = taoBtn("Tải lại");

        btnTim.addActionListener(this);
        btnTaiLai.addActionListener(this);
        txtSearch.addActionListener(this);

        right.add(btnTim);
        right.add(btnTaiLai);

        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // ===== Main panel: form + poster + table =====
    private JSplitPane buildMainPanel() {

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setResizeWeight(0.6);
        verticalSplit.setBackground(SEC_COLOR);
        verticalSplit.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Top: form + poster
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplit.setResizeWeight(0.7);
        topSplit.setBorder(null);
        topSplit.setBackground(SEC_COLOR);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SEC_COLOR);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY,1,true),
                new EmptyBorder(10,10,10,10)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        gc.gridy = r++; addRow(formPanel, gc, "Mã khách hàng", txtMaKH, true);
        gc.gridy = r++; addRow(formPanel, gc, "Tên khách hàng", txtTenKH, false);
        gc.gridy = r++; addRow(formPanel, gc, "SĐT", txtSDT, false);
        
        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        actionPanel.setBackground(SEC_COLOR);
        btnThem = taoBtn("Thêm"); btnLuu = taoBtn("Lưu");
        btnSua = taoBtn("Sửa"); btnXoa = taoBtn("Xoá");
        btnThem.addActionListener(this); btnLuu.addActionListener(this);
        btnSua.addActionListener(this); btnXoa.addActionListener(this);

        for(JButton b: new JButton[]{btnThem,btnLuu,btnSua,btnXoa}) b.setFocusPainted(false);

        actionPanel.add(btnThem); actionPanel.add(btnLuu);
        actionPanel.add(btnSua); actionPanel.add(btnXoa);

        gc.gridy = r++;
        gc.gridx=0; gc.gridwidth=2;
        formPanel.add(actionPanel, gc);

        // Poster panel
        JScrollPane scpKH = new JScrollPane(tableKH);
        tableKH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKH.setRowHeight(25);
        tableKH.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));
        tableKH.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tableKH.setGridColor(Color.LIGHT_GRAY);
        tableKH.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) onRowSelected(tableKH.getSelectedRow());
        });
        
        topSplit.setDividerLocation(500);
        topSplit.setLeftComponent(formPanel);
        topSplit.setRightComponent(scpKH);

        // Bottom: table
        JScrollPane tableScroll = new JScrollPane(tableHD);
        tableScroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        tableScroll.setBackground(PRI_COLOR);;

        tableHD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHD.setRowHeight(25);
        tableHD.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));
        tableHD.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tableHD.setGridColor(Color.LIGHT_GRAY);
//        tableHD.getSelectionModel().addListSelectionListener(e -> {
//            if(!e.getValueIsAdjusting()) onRowSelected(table.getSelectedRow());
//        });

        verticalSplit.setTopComponent(topSplit);
        verticalSplit.setBottomComponent(tableScroll);
        verticalSplit.setDividerLocation(370);
        return verticalSplit;
    }

    private void addRow(JPanel form, GridBagConstraints gc, String label, JComponent field, boolean readOnly){
        gc.gridx=0; gc.weightx=0; form.add(new JLabel(label), gc);
        gc.gridx=1; gc.weightx=1; form.add(field, gc);
        if(field instanceof JTextField) ((JTextField) field).setEditable(!readOnly);
    }

    private void reloadTableKH(){
        modelKH.setRowCount(0);
        List<KhachHang> data = khachHangDAO.layTatCaKhachHang();
        for(KhachHang kh: data){
        	modelKH.addRow(new Object[]{
        		kh.getMaKH(),kh.getTenKH(), kh.getSDT()
            });
        }
        reloadTableHD(null);
        
    }
    
    private void reloadTableHD(Integer maKH){
    	if(maKH == null) { 
    		modelHD.setRowCount(0); return;
    	}
 
        modelHD.setRowCount(0);
        List<HoaDon> data = hoaDonDAO.layHoaDonBangMaKH(maKH);
        for(HoaDon hd: data){
        	ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO(conn);
			List<ChiTietHoaDon> listCTHD = chiTietHoaDonDAO.layChitiethoadon(hd.getMaHD());
			modelHD.addRow(new Object[]{
					hd.getMaHD(),hd.getNgayLapHoaDon(), hd.getKhachHang(), hd.getNhanVien(), hd.tinhTong(listCTHD)
			});
        }
    }

    private void search(){
        String k = txtSearch.getText().trim();
        modelKH.setRowCount(0);
        List<KhachHang> data = khachHangDAO.searchKH(k);
        for(KhachHang kh: data){
        	modelKH.addRow(new Object[]{
        		kh.getMaKH(),kh.getTenKH(), kh.getSDT()
            });
        }
    }

    private void onRowSelected(int row){
        if(row<0) return;
        int ma = (Integer)modelKH.getValueAt(row,0);
        KhachHang kh = khachHangDAO.layKhachHangBangMa(ma);
        if(kh==null) return;

        txtMaKH.setText(kh.getMaKH().toString());
        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSDT());
        
        reloadTableHD(kh.getMaKH());
    }

    private KhachHang readForm(boolean needId){
        try{
            Integer maKH = needId?Integer.parseInt(txtMaKH.getText().trim()):null;
            String ten = txtTenKH.getText().trim();
            String SDT = txtSDT.getText().trim();
            
            return new KhachHang(maKH,ten,SDT);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Lỗi nhập liệu: "+ex.getMessage());
            return null;
        }
    }

    private void doInsert(){
    	KhachHang kh = readForm(false); if(kh==null) return;
        try{ khachHangDAO.insertKhachHang(kh); JOptionPane.showMessageDialog(this,"Đã thêm khách hàng"); reloadTableKH(); }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi thêm: "+ex.getMessage()); }
    }

    private void doUpdate(){
    	KhachHang kh = readForm(true); if(kh==null) return;
        try{
        	khachHangDAO.capNhatKhachHang(kh);
        	JOptionPane.showMessageDialog(this,"Đã cập nhật"); 
        	reloadTableKH(); 
        }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi cập nhật: "+ex.getMessage()); }
    }

    private void doDelete(){
        String maKH = txtMaKH.getText().trim();
        if(maKH.isEmpty()){ JOptionPane.showMessageDialog(this,"Chưa chọn khách hàng"); return; }
        int c = JOptionPane.showConfirmDialog(this,"Xoá khách hàng "+ txtTenKH.getText() +"?","Xác nhận",JOptionPane.YES_NO_OPTION);
        if(c!=JOptionPane.YES_OPTION) return;
        try{ 
        	khachHangDAO.xoaKH(Integer.parseInt(maKH)); 
        	JOptionPane.showMessageDialog(this,"Đã xoá"); 
        	reloadTableKH();
        	}
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi xoá: "+ex.getMessage());}
    }

    private void clearForm(){
        txtMaKH.setText(""); txtTenKH.setText(""); txtSDT.setText(""); 
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        if(o.equals(btnTim) || o.equals(txtSearch)) search();
        else if(o.equals(btnTaiLai)) reloadTableKH();
        else if(o.equals(btnThem)) clearForm();
        else if(o.equals(btnLuu)) doInsert();
        else if(o.equals(btnSua)) doUpdate();
        else if(o.equals(btnXoa)) doDelete();
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
    	return btn;
    }
    
}
