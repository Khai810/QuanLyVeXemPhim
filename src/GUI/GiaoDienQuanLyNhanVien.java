package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import ConnectDB.ConnectDB;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.NhanVienDAO;
import Entity.NhanVien;

@SuppressWarnings("serial")
public class GiaoDienQuanLyNhanVien extends JFrame implements ActionListener {

    // ======== Form fields ========
    private JTextField txtMaNhanVien = new JTextField();
    private JTextField txtTenNhanVien = new JTextField();
    private JTextField txtSDT = new JTextField();
    private JTextField txtEmail = new JTextField();
    private JTextField txtTaiKhoan = new JTextField();
    private JPasswordField txtMatKhau = new JPasswordField();
    private JTextField txtImg = new JTextField();
    
    private JTextField txtSearch = new JTextField();
    
    private JLabel lblImg = new JLabel("<html><center>Ảnh thẻ nhân viên<br>không có</center></html>", JLabel.CENTER);

    private DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Mã nhân viên", "Tên nhân viên", "Số Điện thoại", "Email", "Tài Khoản", "Ảnh thẻ"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
        private static final long serialVersionUID = 1L;
    };
    private JTable table = new JTable(model);
    private String selectedImgPath = null;

    // DAO
    Connection conn;
    private NhanVienDAO nhanVienDAO;

    // Buttons
    private JButton btnTim, btnTaiLai;
    private JButton btnThem, btnLuu, btnSua, btnXoa;

    private static final Dimension POSTER_SIZE = new Dimension(320, 450);
    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;

    public GiaoDienQuanLyNhanVien(NhanVien nhanVien) {
    	try {
			this.conn = ConnectDB.getConnection();
			this.nhanVienDAO = new NhanVienDAO(conn);
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
        reloadTable();
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

        gc.gridy = r++; addRow(formPanel, gc, "Mã nhân viên", txtMaNhanVien, true);
        gc.gridy = r++; addRow(formPanel, gc, "Tên nhân viên", txtTenNhanVien, false);
        gc.gridy = r++; addRow(formPanel, gc, "SĐT", txtSDT, false);
        gc.gridy = r++; addRow(formPanel, gc, "Email", txtEmail, false);
        gc.gridy = r++; addRow(formPanel, gc, "Tài khoản", txtTaiKhoan, false);
        gc.gridy = r++; addRow(formPanel, gc, "Mật khẩu", txtMatKhau, false);
        gc.gridy = r++; addRow(formPanel, gc, "Ảnh thẻ", withBrowse(txtImg), false);
        
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
        lblImg.setPreferredSize(POSTER_SIZE);
        lblImg.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        lblImg.setVerticalAlignment(SwingConstants.CENTER);
        
        topSplit.setDividerLocation(1000);
        topSplit.setLeftComponent(formPanel);
        topSplit.setRightComponent(lblImg);

        // Bottom: table
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        tableScroll.setBackground(PRI_COLOR);;

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setGridColor(Color.LIGHT_GRAY);
        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) onRowSelected(table.getSelectedRow());
        });

        verticalSplit.setTopComponent(topSplit);
        verticalSplit.setBottomComponent(tableScroll);
        verticalSplit.setDividerLocation(450);
        return verticalSplit;
    }

    private void addRow(JPanel form, GridBagConstraints gc, String label, JComponent field, boolean readOnly){
        gc.gridx=0; gc.weightx=0; form.add(new JLabel(label), gc);
        gc.gridx=1; gc.weightx=1; form.add(field, gc);
        if(field instanceof JTextField) ((JTextField) field).setEditable(!readOnly);
    }

    private JComponent withBrowse(JTextField txt){
        JPanel p = new JPanel(new BorderLayout(4,0));
        p.setBackground(Color.WHITE);
        p.add(txt, BorderLayout.CENTER);
        JButton b = new JButton("Chọn...");
        b.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Hình ảnh","png","jpg","jpeg","gif"));
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                selectedImgPath = fc.getSelectedFile().getAbsolutePath();
                txt.setText(selectedImgPath);
                updatePoster(selectedImgPath);
            }
        });
        p.add(b, BorderLayout.EAST);
        return p;
    }

    private void updatePoster(String path){
        if(path==null || path.isBlank()){
            lblImg.setIcon(null);
            lblImg.setText("<html><center>Poster<br>không có</center></html>");
            return;
        }
        LoadHinhAnh loadHinhAnh = new LoadHinhAnh();
    	ImageIcon imageIcon = loadHinhAnh.taiHinhAnh(path, POSTER_SIZE.width, POSTER_SIZE.height);
        lblImg.setIcon(imageIcon);
        lblImg.setText("");
    }

    private void reloadTable(){
        model.setRowCount(0);
        List<NhanVien> data = nhanVienDAO.layTatCaNhanVien();
        for(NhanVien nv: data){
            model.addRow(new Object[]{
                nv.getMaNhanVien(),nv.getTenNhanVien(), nv.getSDT(),
                nv.getEmail(), nv.getTaiKhoan(),
                nv.getImg()
            });
        }
    }

    private void search(){
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        List<NhanVien> data = nhanVienDAO.searchNhanVien(k);
        for(NhanVien nv: data){
        	model.addRow(new Object[]{
                nv.getMaNhanVien(),nv.getTenNhanVien(), nv.getSDT(),
                nv.getEmail(), nv.getTaiKhoan(),
                nv.getImg()
            });
        }
    }

    private void onRowSelected(int row){
        if(row<0) return;
        int ma = (Integer)model.getValueAt(row,0);
        NhanVien nv = nhanVienDAO.layNhanVienBangMa(ma);
        if(nv==null) return;

        txtMaNhanVien.setText(nv.getMaNhanVien().toString());
        txtTenNhanVien.setText(nv.getTenNhanVien());
        txtSDT.setText(nv.getSDT());
        txtEmail.setText(nv.getEmail());
        txtTaiKhoan.setText(nv.getTaiKhoan());
        txtMatKhau.setText(nv.getMatKhau());
        txtImg.setText(nv.getImg());

        selectedImgPath = nv.getImg();
        updatePoster(selectedImgPath);

    }

    private NhanVien readForm(boolean needId){
        try{
            Integer maNV = needId?Integer.parseInt(txtMaNhanVien.getText().trim()):null;
            String ten = txtTenNhanVien.getText().trim();
            String SDT = txtSDT.getText().trim();
            String Email = txtEmail.getText().trim();
            String taiKhoan = txtTaiKhoan.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());
            String img = txtImg.getText().trim();

            return new NhanVien(maNV,ten,SDT,Email,taiKhoan,matKhau,img);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Lỗi nhập liệu: "+ex.getMessage());
            return null;
        }
    }

    private void doInsert(){
        NhanVien nv = readForm(false); if(nv==null) return;
        try{ nhanVienDAO.taoNhanVien(nv); JOptionPane.showMessageDialog(this,"Đã thêm nhân viên"); reloadTable(); }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi thêm: "+ex.getMessage()); }
    }

    private void doUpdate(){
    	NhanVien nv = readForm(true); if(nv==null) return;
        try{
        	nhanVienDAO.capNhatNhanVien(nv);
        	JOptionPane.showMessageDialog(this,"Đã cập nhật"); 
        	reloadTable(); 
        }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi cập nhật: "+ex.getMessage()); }
    }

    private void doDelete(){
        String maNV = txtMaNhanVien.getText().trim();
        if(maNV.isEmpty()){ JOptionPane.showMessageDialog(this,"Chưa chọn nhân viên"); return; }
        int c = JOptionPane.showConfirmDialog(this,"Xoá nhân viên "+ txtTenNhanVien.getText() +"?","Xác nhận",JOptionPane.YES_NO_OPTION);
        if(c!=JOptionPane.YES_OPTION) return;
        try{ nhanVienDAO.xoaNhanVien(Integer.parseInt(maNV)); JOptionPane.showMessageDialog(this,"Đã xoá"); reloadTable();}
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi xoá: "+ex.getMessage());}
    }

    private void clearForm(){
        txtMaNhanVien.setText(""); txtTenNhanVien.setText(""); txtSDT.setText(""); txtEmail.setText("");
        txtTaiKhoan.setText(""); txtMatKhau.setText(""); txtImg.setText("");table.clearSelection();
        selectedImgPath=null; updatePoster(null);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        if(o.equals(btnTim) || o.equals(txtSearch)) search();
        else if(o.equals(btnTaiLai)) reloadTable();
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
