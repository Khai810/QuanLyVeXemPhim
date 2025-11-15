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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("serial")
public class GiaoDienQuanLyVe extends JFrame implements ActionListener {

    private JTextField txtMaVe = new JTextField();
    private JTextField txtNgayDat = new JTextField();
    private JTextField txtGiaVe = new JTextField();
    private JTextField txtNgayChieu = new JTextField();
    private JTextField txtGioChieu = new JTextField();
    private JTextField txtPhongChieu = new JTextField();

    private JTextField txtSearch = new JTextField();
    private JPanel topRight;

    private JComboBox<SuatChieu> cboSuatChieu = new JComboBox<>();
    private JComboBox<Ghe> cboGhe = new JComboBox<>();
    private JComboBox<Phim> cboPhim = new JComboBox<>();

    private DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Mã Vé", "Phim", "Suất chiếu", "Ghế", "Giá vé",
                "Ngày đặt", "Ngày chiếu", "Giờ chiếu"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable table = new JTable(model);
    private VeXemPhim ticket;
    // DAO
    private Connection conn;
    private GheDAO gheDAO;
    private SuatChieuDAO suatChieuDAO;
    private VeDAO veDAO;
    private PhimDAO phimDAO;

    // Buttons
    private JButton btnFind, btnReload;
    private JButton btnXoaRong, btnSave, btnUpdate, btnDelete, btnInVe;
    private static final Font fontChuBtn = new Font("Segoe UI", Font.BOLD, 14);

    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);

    private static final Color BTN_COLOR = Color.WHITE;
    private static final String FILE_PATH = "inVe/";

    // date/time format
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public GiaoDienQuanLyVe(NhanVien nhanVien) {
        try {
            this.conn = ConnectDB.getConnection();
            this.gheDAO = new GheDAO(conn);
            this.suatChieuDAO = new SuatChieuDAO(conn);
            this.veDAO = new VeDAO(conn);
            this.phimDAO = new PhimDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối DB: " + e.getMessage());
        }

        MenuChinh menuBar = new MenuChinh(this, nhanVien);
        this.setJMenuBar(menuBar);
        MenuToggleUtil.addToggleSupport(this, menuBar);

        setBackground(PRI_COLOR);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        // listeners phụ trợ
        cboPhim.addActionListener(ev -> onPhimChanged());
        cboSuatChieu.addActionListener(ev -> onSuatChieuChanged());

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
        top.setBackground(SEC_COLOR);
        
        JPanel formPanel = buildFormPanel();
        JPanel topRightBackGround = new JPanel();
        topRightBackGround.setBackground(PRI_COLOR);
        
        topRight = new JPanel(new BorderLayout());
        topRight.setBackground(SEC_COLOR);
        topRight.setPreferredSize(new Dimension(400, 350));

        topRightBackGround.add(topRight);
        top.setLeftComponent(formPanel);
        top.setRightComponent(topRightBackGround);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        tableScroll.setPreferredSize(new Dimension(0, 400)); 
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(Color.LIGHT_GRAY);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected(table.getSelectedRow());
        });

        vertical.setTopComponent(top);
        vertical.setBottomComponent(tableScroll);
        vertical.setDividerLocation(400); 

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

        gc.gridy = r++; addRow(p, gc, "Mã vé", txtMaVe, true);
        gc.gridy = r++; addRow(p, gc, "Ngày đặt (yyyy-MM-dd)", txtNgayDat, false);
        gc.gridy = r++; addRow(p, gc, "Giá vé", txtGiaVe, false);

        gc.gridy = r++; addRow(p, gc, "Phim", cboPhim, false);

        gc.gridy = r++; addRow(p, gc, "Suất chiếu", cboSuatChieu, false);
        gc.gridy = r++; addRow(p, gc, "Ngày chiếu (yyyy-MM-dd)", txtNgayChieu, true);
        gc.gridy = r++; addRow(p, gc, "Giờ Chiếu (HH:mm)", txtGioChieu, true);
        gc.gridy = r++; addRow(p, gc, "Phòng", txtPhongChieu, true);
        
        gc.gridy = r++; addRow(p, gc, "Ghế", cboGhe, false);
        

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(SEC_COLOR);

        btnXoaRong = taoBtn("Thêm");
        btnSave = taoBtn("Lưu");
        btnUpdate = taoBtn("Sửa");
        btnDelete = taoBtn("Xoá");
        btnInVe = taoBtn("In vé");
        
        for (JButton b : new JButton[]{btnXoaRong, btnSave, btnUpdate, btnDelete, btnInVe}) {
            b.addActionListener(this);
        }

        actions.add(btnXoaRong);
        actions.add(btnSave);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnInVe);

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
        // load phim
        cboPhim.removeAllItems();
        try {
            List<Phim> dsPhim = phimDAO.getAllPhim(); // giả định
            for (Phim p : dsPhim) cboPhim.addItem(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // khi chưa chọn phim/phòng, vẫn clear suất/ghế
        cboSuatChieu.removeAllItems();
        cboGhe.removeAllItems();
    }

    private void onPhimChanged() {
        Phim phim = (Phim) cboPhim.getSelectedItem();
        cboSuatChieu.removeAllItems();
        if (phim == null) return;
        try {
            List<SuatChieu> dsSC = suatChieuDAO.getAllSuatChieu(phim.getMaPhim());
            for (SuatChieu sc : dsSC) cboSuatChieu.addItem(sc);
            cboSuatChieu.setSelectedIndex(-1);
            txtNgayChieu.setText("");
            txtGioChieu.setText("");
            txtPhongChieu.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSuatChieuChanged() {
        SuatChieu sc = (SuatChieu) cboSuatChieu.getSelectedItem();
        if (sc == null) return;
        // tự động điền ngày & giờ theo suất chiếu nếu có
        try {
            if (sc.getNgayChieu() != null) txtNgayChieu.setText(sc.getNgayChieu().toString());
            if (sc.getGioChieu() != null) txtGioChieu.setText(sc.getGioChieu().toString());
            if(sc.getPhongChieu() != null) txtPhongChieu.setText(sc.getPhongChieu().getTenPhong());
            cboGhe.removeAllItems();
            int maPhong = sc.getPhongChieu().getMaPhongChieu();

            for(Ghe ghe : gheDAO.getGheTheoPhong(maPhong)) {
            	cboGhe.addItem(ghe);
            }
            cboGhe.setSelectedIndex(-1);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void reloadTable() {
        model.setRowCount(0);
        try {
            List<Ve> list = veDAO.layTatCaVe();
            for (Ve v : list) {
                model.addRow(new Object[]{
                    v.getMaVe(),
                    v.getSuatChieu() == null ? "" : v.getSuatChieu().getPhim().getTenPhim(),
                    v.getSuatChieu() == null ? "" : v.getSuatChieu().getGioChieu(),
                    v.getTenGhe(),
                    v.getGiaVe(),
                    v.getNgayDat() == null ? "" : v.getNgayDat().toString(),
                    v.getNgayChieu() == null ? "" : v.getNgayChieu().toString(),
                    v.getGioChieu() == null ? "" : v.getGioChieu().toString()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load vé: " + ex.getMessage());
        }
    }

    //=========== Table selection fill form ===========
    private void onRowSelected(int row) {
        if (row < 0) return;

        int ma = (Integer) model.getValueAt(row, 0);
        Ve v = veDAO.layVeBangMaVe(ma);
        if (v == null) return;

        txtMaVe.setText(String.valueOf(v.getMaVe()));
        txtNgayDat.setText(v.getNgayDat() == null ? "" : v.getNgayDat().toString());
        txtGiaVe.setText(String.valueOf(v.getGiaVe()));
        txtNgayChieu.setText(v.getNgayChieu() == null ? "" : v.getNgayChieu().toString());
        txtGioChieu.setText(v.getGioChieu() == null ? "" : v.getGioChieu().toString());

        if (v.getSuatChieu() != null) {
            cboPhim.setSelectedItem(v.getSuatChieu().getPhim());
            // đảm bảo suất được load
            onPhimChanged();
            cboSuatChieu.setSelectedItem(v.getSuatChieu());
            txtPhongChieu.setText(v.getSuatChieu().getPhongChieu().getTenPhong());
        } else {
            cboSuatChieu.setSelectedIndex(-1);
        }
        // chọn ghế
        for (int i = 0; i < cboGhe.getItemCount(); i++) {
            Ghe g = cboGhe.getItemAt(i);
            if (g.getTenGhe().equals(v.getTenGhe())) {
                cboGhe.setSelectedIndex(i);
                break;
            }
        }


        // hiển thị vé lên topRight dùng TicketPanel nếu có
        try {
        	ticket = new VeXemPhim(v);
            topRight.removeAll();
            JScrollPane sp =  new JScrollPane(ticket);
            sp.setBackground(PRI_COLOR);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);

            topRight.add(sp, BorderLayout.CENTER);
            topRight.setBackground(SEC_COLOR);
            topRight.revalidate();
            topRight.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //=========== Form helpers ===========
    private Ve readForm(boolean needId) {
        try {
            Integer ma = needId && !txtMaVe.getText().isBlank() ? Integer.parseInt(txtMaVe.getText().trim()) : null;
            LocalDate ngayDat = txtNgayDat.getText().isBlank() ? null : LocalDate.parse(txtNgayDat.getText().trim(), DATE_FMT);
            double gia = txtGiaVe.getText().isBlank() ? 0.0 : Double.parseDouble(txtGiaVe.getText().trim());
            SuatChieu sc = (SuatChieu) cboSuatChieu.getSelectedItem();
            LocalDate ngayChieu = txtNgayChieu.getText().isBlank() ? null : LocalDate.parse(txtNgayChieu.getText().trim(), DATE_FMT);
            LocalTime gio = txtGioChieu.getText().isBlank() ? null : LocalTime.parse(txtGioChieu.getText().trim(), TIME_FMT);
            Ghe ghe = (Ghe) cboGhe.getSelectedItem();

            Ve v = new Ve();
            if (ma != null) v.setMaVe(ma);
            v.setNgayDat(ngayDat);
            v.setGiaVe(gia);
            v.setSuatChieu(sc);
            v.setNgayChieu(ngayChieu);
            v.setGioChieu(gio);
            v.setGhe(ghe);
            if (ghe != null) v.setTenGhe(ghe.getTenGhe());
            if (sc != null) v.setTenPhongChieu(sc.getPhongChieu().getTenPhong());
            
            return v;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage());
            return null;
        }
    }

    private void clearForm() {
        txtMaVe.setText("");
        txtNgayDat.setText("");
        txtGiaVe.setText("");
        txtNgayChieu.setText("");
        txtGioChieu.setText("");
        txtPhongChieu.setText("");
        cboPhim.setSelectedIndex(-1);
        cboSuatChieu.removeAllItems();
        cboGhe.removeAllItems();
        table.clearSelection();
        topRight.removeAll();
        topRight.revalidate();
        topRight.repaint();
    }

    //=========== CRUD ===========
    private void doInsert() {
        Ve v = readForm(false);
        if (v == null) return;

        try {
            boolean ok = veDAO.taoVe(v);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã thêm vé");
                reloadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm vé thất bại");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + ex.getMessage());
        }
    }

    private void doUpdate() {
        Ve v = readForm(true);
        if (v == null || txtMaVe.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn vé để sửa");
            return;
        }
        try {
            boolean ok = veDAO.capNhatVe(v);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật vé");
                reloadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

//    private void doDelete() {
//        if (txtMaVe.getText().isBlank()) {
//            JOptionPane.showMessageDialog(this, "Chưa chọn vé để xóa");
//            return;
//        }
//        int c = JOptionPane.showConfirmDialog(this, "Xoá vé?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//        if (c != JOptionPane.YES_OPTION) return;
//
//        try {
//            int ma = Integer.parseInt(txtMaVe.getText().trim());
//            boolean ok = veDAO.xoaVe(ma);
//            if (ok) {
//                JOptionPane.showMessageDialog(this, "Đã xóa vé");
//                reloadTable();
//                clearForm();
//            } else {
//                JOptionPane.showMessageDialog(this, "Xóa thất bại");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
//        }
//    }
//
    //=========== Search & Action ===========
    private void search() {
        String k = txtSearch.getText().trim();
        model.setRowCount(0);
        try {
            List<Ve> list = veDAO.search(k); // giả định có hàm search(String)
            for (Ve v : list) {
                model.addRow(new Object[]{
                    v.getMaVe(),
                    v.getSuatChieu() == null ? "" : v.getSuatChieu().getPhim().getTenPhim(),
                    v.getSuatChieu() == null ? "" : v.getSuatChieu().getGioChieu(),
                    v.getTenGhe(),
                    v.getGiaVe(),
                    v.getNgayDat() == null ? "" : v.getNgayDat().toString(),
                    v.getNgayChieu() == null ? "" : v.getNgayChieu().toString(),
                    v.getGioChieu() == null ? "" : v.getGioChieu().toString()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tìm: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnFind) || o.equals(txtSearch)) search();
        else if (o.equals(btnReload)) reloadTable();
        else if (o.equals(btnXoaRong)) clearForm();
        else if (o.equals(btnSave)) doInsert();
        else if (o.equals(btnUpdate)) doUpdate();
//        else if (o.equals(btnDelete)) doDelete();
        else if (o.equals(btnInVe)) {
			try {
				JFrame frame = new JFrame("Vé xem phim");
				frame.add(ticket);
		        frame.pack();
		        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        frame.setLocationRelativeTo(null);
		        frame.setVisible(true);
		        
				ticket.saveAsImage(FILE_PATH + "ve" + txtMaVe.getText() + ".png");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }

    private JButton taoBtn(String ten) {
        JButton btn = new JButton(ten);
        btn.setBackground(RED_COLOR);
        btn.setForeground(BTN_COLOR);
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setFont(fontChuBtn);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        return btn;
    }
}
