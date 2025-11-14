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
import java.time.LocalDate;
import java.util.List;

import DAO.PhimDAO;
import DAO.TheLoaiDAO;
import Entity.NhanVien;
import Entity.Phim;
import Entity.TheLoai;

@SuppressWarnings("serial")
public class GiaoDienQuanLyPhim extends JPanel implements ActionListener {

    // ======== Form fields ========
    private JTextField txtMaPhim = new JTextField();
    private JTextField txtTenPhim = new JTextField();
    private JTextField txtDaoDien = new JTextField();
    private JTextField txtQuocGia = new JTextField();
    private JTextField txtThoiLuong = new JTextField();
    private JTextField txtDoTuoi = new JTextField();
    private JTextField txtNgayKC = new JTextField();
    private JTextField txtImg = new JTextField();
    private JTextArea txtMoTa = new JTextArea(4,20);
    private JComboBox<TheLoai> cboTheLoai = new JComboBox<>();
    private JTextField txtSearch = new JTextField();
    
    private JLabel lblPoster = new JLabel("<html><center>Poster<br>không có</center></html>", JLabel.CENTER);

    private DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Mã phim", "Tên phim", "Đạo diễn", "Quốc gia", "Thời lượng", "Độ tuổi", "Ngày Khởi Chiếu", "Thể loại"},0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
        private static final long serialVersionUID = 1L;
    };
    private JTable table = new JTable(model);

    // DAO
    Connection conn;
    private PhimDAO phimDAO;
    private TheLoaiDAO theLoaiDAO;

    // Buttons
    private JButton btnBack, btnFind, btnReload;
    private JButton btnNew, btnSave, btnUpdate, btnDelete;

    // Selected image path
    private String selectedImgPath = null;

    private NhanVien nhanVien;
    public GiaoDienQuanLyPhim(NhanVien nhanVien) {
    	this.nhanVien = nhanVien;
    	try {
			this.conn = ConnectDB.getConnection();
			this.phimDAO = new PhimDAO(conn);
			this.theLoaiDAO  = new TheLoaiDAO(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(10,10,10,10));
        setBackground(new Color(245,245,245));

        // ===== Top bar search + buttons =====
        add(buildTopBar(), BorderLayout.NORTH);

        // ===== Main split: form left + table below =====
        add(buildMainPanel(), BorderLayout.CENTER);

        // Load data
        loadTheLoai();
        reloadTable();
    }

    // ===== Top search bar =====
    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBackground(new Color(245,245,245));

        JPanel left = new JPanel(new GridLayout(1,2,6,6));
        left.setBackground(new Color(245,245,245));
        left.add(new JLabel("Tìm:"));
        left.add(txtSearch);
        panel.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,0));
        right.setBackground(new Color(245,245,245));

        btnBack = new JButton("Quay lại");
        btnFind = new JButton("Tìm");
        btnReload = new JButton("Tải lại");

        btnBack.addActionListener(this);
        btnFind.addActionListener(this);
        btnReload.addActionListener(this);
        txtSearch.addActionListener(this);

        right.add(btnBack);
        right.add(btnFind);
        right.add(btnReload);

        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // ===== Main panel: form + poster + table =====
    private JSplitPane buildMainPanel() {

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setResizeWeight(0.6);

        // Top: form + poster
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplit.setResizeWeight(0.7);
        topSplit.setBorder(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY,1,true),
                new EmptyBorder(10,10,10,10)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        gc.gridy = r++; addRow(formPanel, gc, "Mã phim", txtMaPhim, true);
        gc.gridy = r++; addRow(formPanel, gc, "Tên phim", txtTenPhim, false);
        gc.gridy = r++; addRow(formPanel, gc, "Đạo diễn", txtDaoDien, false);
        gc.gridy = r++; addRow(formPanel, gc, "Quốc gia", txtQuocGia, false);
        gc.gridy = r++; addRow(formPanel, gc, "Thời lượng", txtThoiLuong, false);
        gc.gridy = r++; addRow(formPanel, gc, "Độ tuổi", txtDoTuoi, false);
        gc.gridy = r++; addRow(formPanel, gc, "Ngày KC", txtNgayKC, false);
        gc.gridy = r++; addRow(formPanel, gc, "Thể loại", cboTheLoai, false);
        gc.gridy = r++; addRow(formPanel, gc, "Ảnh", withBrowse(txtImg), false);

        // Mô tả
        gc.gridy = r++;
        gc.gridx = 0; gc.weightx=0; formPanel.add(new JLabel("Mô tả"), gc);
        gc.gridx = 1; gc.weightx=1; formPanel.add(new JScrollPane(txtMoTa), gc);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        actionPanel.setBackground(Color.WHITE);
        btnNew = new JButton("Thêm"); btnSave = new JButton("Lưu");
        btnUpdate = new JButton("Sửa"); btnDelete = new JButton("Xoá");
        btnNew.addActionListener(this); btnSave.addActionListener(this);
        btnUpdate.addActionListener(this); btnDelete.addActionListener(this);

        btnNew.setBackground(new Color(46,204,113)); btnNew.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(52,152,219)); btnSave.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(241,196,15)); btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231,76,60)); btnDelete.setForeground(Color.WHITE);
        for(JButton b: new JButton[]{btnNew,btnSave,btnUpdate,btnDelete}) b.setFocusPainted(false);

        actionPanel.add(btnNew); actionPanel.add(btnSave);
        actionPanel.add(btnUpdate); actionPanel.add(btnDelete);

        gc.gridy = r++;
        gc.gridx=0; gc.gridwidth=2;
        formPanel.add(actionPanel, gc);

        // Poster panel
        lblPoster.setPreferredSize(new Dimension(260,350));
        lblPoster.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblPoster.setHorizontalAlignment(SwingConstants.CENTER);
        lblPoster.setVerticalAlignment(SwingConstants.CENTER);

        topSplit.setLeftComponent(formPanel);
        topSplit.setRightComponent(lblPoster);

        // Bottom: table
        JScrollPane tableScroll = new JScrollPane(table);
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
            lblPoster.setIcon(null);
            lblPoster.setText("<html><center>Poster<br>không có</center></html>");
            return;
        }
        ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(
            260,350, Image.SCALE_SMOOTH
        ));
        lblPoster.setIcon(icon);
        lblPoster.setText("");
    }

    private void loadTheLoai(){
        cboTheLoai.removeAllItems();
        for(int i=1;i<=50;i++){
            TheLoai tl = theLoaiDAO.layTheLoaiBangMaTheLoai(i);
            if(tl!=null) cboTheLoai.addItem(tl);
        }
        cboTheLoai.setSelectedIndex(-1);
    }

    private void reloadTable(){
        model.setRowCount(0);
        List<Phim> data = phimDAO.getAllPhim();
        for(Phim p: data){
            model.addRow(new Object[]{
                p.getMaPhim(),p.getTenPhim(),p.getDaoDien(),
                p.getQuocGia(),p.getThoiLuong(),p.getDoTuoi(),
                p.getNgayKhoiChieu(),
                p.getTheLoai()==null?"":p.getTheLoai().getTenTheLoai()
            });
        }
    }

    private void search(){
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        List<Phim> data = phimDAO.search(k);
        for(Phim p: data){
            model.addRow(new Object[]{
                p.getMaPhim(),p.getTenPhim(),p.getDaoDien(),
                p.getQuocGia(),p.getThoiLuong(),p.getDoTuoi(),
                p.getNgayKhoiChieu(),
                p.getTheLoai()==null?"":p.getTheLoai().getTenTheLoai()
            });
        }
    }

    private void onRowSelected(int row){
        if(row<0) return;
        int ma = (Integer)model.getValueAt(row,0);
        Phim p = phimDAO.layPhimBangMaPhim(ma);
        if(p==null) return;

        txtMaPhim.setText(String.valueOf(p.getMaPhim()));
        txtTenPhim.setText(p.getTenPhim());
        txtDaoDien.setText(p.getDaoDien());
        txtQuocGia.setText(p.getQuocGia());
        txtThoiLuong.setText(String.valueOf(p.getThoiLuong()));
        txtDoTuoi.setText(p.getDoTuoi());
        txtNgayKC.setText(p.getNgayKhoiChieu()==null?"":p.getNgayKhoiChieu().toString());
        txtImg.setText(p.getImg());
        txtMoTa.setText(p.getMoTa());
        selectedImgPath = p.getImg();
        updatePoster(selectedImgPath);

        if(p.getTheLoai()!=null){
            for(int i=0;i<cboTheLoai.getItemCount();i++){
                if(cboTheLoai.getItemAt(i).getMaTheLoai()==p.getTheLoai().getMaTheLoai()){
                    cboTheLoai.setSelectedIndex(i);
                }
            }
        }
    }

    private Phim readForm(boolean needId){
        try{
            Integer ma = needId?Integer.parseInt(txtMaPhim.getText().trim()):null;
            String ten = txtTenPhim.getText().trim();
            String daoDien = txtDaoDien.getText().trim();
            String quocGia = txtQuocGia.getText().trim();
            int thoiLuong = Integer.parseInt(txtThoiLuong.getText().trim());
            String doTuoi = txtDoTuoi.getText().trim();
            LocalDate ngayKC = txtNgayKC.getText().isBlank()?null:LocalDate.parse(txtNgayKC.getText().trim());
            String img = txtImg.getText().trim();
            TheLoai tl = (TheLoai)cboTheLoai.getSelectedItem();
            String moTa = txtMoTa.getText();

            if(ten.isEmpty()) throw new IllegalArgumentException("Tên phim không được trống");
            if(thoiLuong<=0) throw new IllegalArgumentException("Thời lượng phải >0");

            return new Phim(ma,ten,moTa,doTuoi,quocGia,thoiLuong,daoDien,ngayKC,img,tl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Lỗi nhập liệu: "+ex.getMessage());
            return null;
        }
    }

    private void doInsert(){
        Phim p = readForm(false); if(p==null) return;
        try{ phimDAO.insert(p); JOptionPane.showMessageDialog(this,"Đã thêm phim"); reloadTable(); }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi thêm: "+ex.getMessage()); }
    }

    private void doUpdate(){
        Phim p = readForm(true); if(p==null) return;
        try{ phimDAO.update(p); JOptionPane.showMessageDialog(this,"Đã cập nhật"); reloadTable(); }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi cập nhật: "+ex.getMessage()); }
    }

    private void doDelete(){
        String txt = txtMaPhim.getText().trim();
        if(txt.isEmpty()){ JOptionPane.showMessageDialog(this,"Chưa chọn phim"); return; }
        int c = JOptionPane.showConfirmDialog(this,"Xoá phim "+txt+"?","Xác nhận",JOptionPane.YES_NO_OPTION);
        if(c!=JOptionPane.YES_OPTION) return;
        try{ phimDAO.delete(Integer.parseInt(txt)); JOptionPane.showMessageDialog(this,"Đã xoá"); reloadTable();}
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Lỗi xoá: "+ex.getMessage());}
    }

    private void clearForm(){
        txtMaPhim.setText(""); txtTenPhim.setText(""); txtDaoDien.setText(""); txtQuocGia.setText("");
        txtThoiLuong.setText(""); txtDoTuoi.setText(""); txtNgayKC.setText(""); txtImg.setText("");
        txtMoTa.setText(""); cboTheLoai.setSelectedIndex(-1); table.clearSelection();
        selectedImgPath=null; updatePoster(null);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        if(o.equals(btnBack)){ 
        	new GiaoDienChonPhim(nhanVien).setVisible(true);
        	SwingUtilities.getWindowAncestor(this).dispose(); 
        	}
        else if(o.equals(btnFind) || o.equals(txtSearch)) search();
        else if(o.equals(btnReload)) reloadTable();
        else if(o.equals(btnNew)) clearForm();
        else if(o.equals(btnSave)) doInsert();
        else if(o.equals(btnUpdate)) doUpdate();
        else if(o.equals(btnDelete)) doDelete();
    }

}
