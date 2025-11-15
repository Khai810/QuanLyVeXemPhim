package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import ConnectDB.ConnectDB;
import DAO.PhimDAO;
import DAO.SuatChieuDAO;
import DAO.PhongChieuDAO;
import Entity.Phim;
import Entity.SuatChieu;
import Entity.PhongChieu;

public class GiaoDienQuanLySuatChieu extends JPanel{
	private static final long serialVersionUID = 1L;
	
    private JPanel panelCenterMain;
    private JPanel panelEast;
    private PhimDAO phimDAO;
    private SuatChieuDAO suatChieuDAO;
    private PhongChieuDAO phongChieuDAO;
 

    // East form components
    private JTextField txtMaSuatChieu, txtNgayChieu, txtGiaVe;
    private JComboBox<Phim> cbPhim;
    private JComboBox<PhongChieu> cbPhongChieu;
	private JComboBox<String> cbGio, cbPhut;
	private JPanel datePanel; 
	private String selectedDate; 
	private JButton btnDateSelected;
	private JButton btnSuatChieuSelected; 
	private java.util.Map<Phim, JPanel> phimToShowtimes = new java.util.HashMap<>();
	
	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    
    public GiaoDienQuanLySuatChieu() {
    	setLayout(new BorderLayout(10, 10));
        // DAO với kết nối
        try {
        	Connection conn = ConnectDB.getConnection();
            phimDAO = new PhimDAO(conn);
            suatChieuDAO = new SuatChieuDAO(conn);
            phongChieuDAO = new PhongChieuDAO(conn);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối DB: " + e.getMessage());
            return;
        }

        // ===== Center panel: scrollable phim cards =====
        panelCenterMain = new JPanel();
        panelCenterMain.setBackground(PRI_COLOR);
        panelCenterMain.setLayout(new BoxLayout(panelCenterMain, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelCenterMain);
        scroll.getVerticalScrollBar().setUnitIncrement(8); // Tăng tốc độ cuộn

        loadPhimCards();

        // ===== East panel: CRUD form =====
        panelEast = new JPanel();
        panelEast.setBackground(PRI_COLOR);
        panelEast.setLayout(new BoxLayout(panelEast, BoxLayout.Y_AXIS));
        panelEast.setBorder(new EmptyBorder(10,10,10,10));
        panelEast.setPreferredSize(new Dimension(350,0));
        addEastComponents();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, panelEast);
        splitPane.setResizeWeight(0.7);
        add(splitPane);
    }

    private void addEastComponents() {
    	//Label
    	JLabel lblQuanLySuatChieu = new JLabel("Quản Lý Suất Chiếu");
    	lblQuanLySuatChieu.setFont(new Font("Arial", Font.BOLD, 24)); // chữ to, in đậm
    	lblQuanLySuatChieu.setAlignmentX(Component.CENTER_ALIGNMENT);    // căn giữa theo BoxLayout
    	panelEast.add(lblQuanLySuatChieu);
    	panelEast.add(Box.createVerticalStrut(15)); // thêm khoảng trống dưới tiêu đề
    	panelEast.setBackground(PRI_COLOR);
    	
    	// Mã Suất Chiếu
        panelEast.add(createFieldPanel("Mã Suất Chiếu:", txtMaSuatChieu = new JTextField(15)));
        txtMaSuatChieu.setEnabled(false);

        // Phim
        panelEast.add(createFieldPanel("Phim:", cbPhim = new JComboBox<Phim>()));
        cbPhim.removeAllItems();
        for (Phim p : phimDAO.getAllPhim()) cbPhim.addItem(p);

        // Phòng Chiếu
        panelEast.add(createFieldPanel("Phòng Chiếu:", cbPhongChieu = new JComboBox<PhongChieu>()));
        cbPhongChieu.removeAllItems();
        for (PhongChieu pc : phongChieuDAO.getAllPhongChieu()) cbPhongChieu.addItem(pc);

        // Ngày Chiếu
        panelEast.add(createFieldPanel("Ngày Chiếu (yyyy-MM-dd):", txtNgayChieu = new JTextField(15)));

        // Giờ Chiếu: 2 comboBox (giờ + phút)
        JPanel gioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        gioPanel.add(new JLabel("Giờ Chiếu:"));
        gioPanel.setBackground(PRI_COLOR);
        cbGio = new JComboBox<>();
        for (int i = 0; i < 25; i++) cbGio.addItem(String.format("%02d", i));
        gioPanel.add(cbGio);

        cbPhut = new JComboBox<>(new String[]{"00", "15", "30", "45"});
        gioPanel.add(cbPhut);
        panelEast.add(gioPanel);

        // Giá cơ bản
        panelEast.add(createFieldPanel("Giá Cơ Bản:", txtGiaVe = new JTextField(15)));

        // Nút CRUD cùng dòng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PRI_COLOR);
        JButton btnThem = taoBtn("Thêm");
        JButton btnXoa = taoBtn("Xóa");
        JButton btnLuu = taoBtn("Lưu");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLuu);
        panelEast.add(Box.createVerticalStrut(10));
        panelEast.add(buttonPanel);

        // ActionListener
        btnThem.addActionListener(e -> clearForm());
        btnXoa.addActionListener(e -> deleteSelectedSuatChieu());
        btnLuu.addActionListener(e -> saveSuatChieu());
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        panel.setBackground(PRI_COLOR);
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120,25));
        panel.add(lbl);
        panel.add(field);
        return panel;
    }

    private void loadPhimCards() {
        panelCenterMain.removeAll();
        phimToShowtimes.clear();

        // Lấy tất cả phim
        List<Phim> allPhim = phimDAO.getAllPhim();

        // Lấy tất cả suất chiếu từ tất cả phim để tạo datePanel
        List<SuatChieu> allSuatChieu = new ArrayList<>();
        for (Phim p : allPhim) {
            allSuatChieu.addAll(suatChieuDAO.getAllSuatChieu(p.getMaPhim()));
        }

        // Tạo datePanel chung
        createDatePanel(allSuatChieu);

        // Tạo card cho từng phim
        for (Phim p : allPhim) {
            List<SuatChieu> suatChieuCuaPhim = suatChieuDAO.getAllSuatChieu(p.getMaPhim());
            JPanel showtimes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            showtimes.setBackground(PRI_COLOR);
            phimToShowtimes.put(p, showtimes);

            panelCenterMain.add(createPhimCard(p, showtimes, suatChieuCuaPhim));
            panelCenterMain.add(new JSeparator(SwingConstants.HORIZONTAL));
        }

        panelCenterMain.revalidate();
        panelCenterMain.repaint();
    }



    // === Tạo panel chọn ngày ===
    private void createDatePanel(List<SuatChieu> allSuatChieu) {
        if (datePanel != null) panelCenterMain.remove(datePanel);
        datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        datePanel.setBackground(SEC_COLOR);

        Set<String> uniqueDates = new TreeSet<>();
        for (SuatChieu sc : allSuatChieu) uniqueDates.add(sc.getNgayChieu().toString());

        JButton firstButton = null;
        for (String d : uniqueDates) {
            JButton btn = new JButton(d);
            btn.setFocusPainted(false);
            btn.setBackground(SEC_COLOR);
            btn.addActionListener(e -> {
                selectedDate = d;
                // Reset màu cũ
                if (btnDateSelected != null) btnDateSelected.setBackground(SEC_COLOR);
                // Set màu mới
                btn.setBackground(Color.YELLOW);
                btnDateSelected = btn;

                updateAllShowtimes();
            });
            datePanel.add(btn);
            if (firstButton == null) firstButton = btn;
        }

        panelCenterMain.add(datePanel, 0);

        if (firstButton != null) firstButton.doClick();
    }
    
    private void updateAllShowtimes() {
        for (Phim p : phimToShowtimes.keySet()) {
            JPanel showtimes = phimToShowtimes.get(p);
            showtimes.removeAll();
            List<SuatChieu> scList = suatChieuDAO.getAllSuatChieu(p.getMaPhim());
            for (SuatChieu sc : scList) {
                if (sc.getNgayChieu().toString().equals(selectedDate)) {
                    JButton btnSC = new JButton(sc.getGioChieu().toString());
                    btnSC.setBackground(SEC_COLOR);
                    btnSC.addActionListener(ev -> {
                        loadSelectedSuatChieuToForm(sc);
                        // Reset màu nút cũ
                        if (btnSuatChieuSelected != null) btnSuatChieuSelected.setBackground(SEC_COLOR);
                        // Highlight nút hiện tại
                        btnSC.setBackground(Color.YELLOW);
                        btnSuatChieuSelected = btnSC;
                    });
                    showtimes.add(btnSC);
                }
            }
            showtimes.revalidate();
            showtimes.repaint();
        }
    }

    private JPanel createPhimCard(Phim phim, JPanel showtimesPanel, List<SuatChieu> scList) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10,10));
        card.setBorder(new EmptyBorder(10,10,10,10));
        card.setBackground(SEC_COLOR);

        // ===== Poster =====
        JLabel lblPoster = new JLabel("No Image", JLabel.CENTER);
        lblPoster.setPreferredSize(new Dimension(120, 180));
        lblPoster.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if (phim.getImg() != null && !phim.getImg().isEmpty()) {
            try {
                // Thử load từ resources
                java.net.URL imgURL = getClass().getResource(phim.getImg());
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(
                            new ImageIcon(imgURL).getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH)
                    );
                    lblPoster.setIcon(icon);
                    lblPoster.setText("");
                } else {
                    // Thử load từ file hệ thống
                    java.io.File f = new java.io.File(phim.getImg());
                    if(f.exists()) {
                        ImageIcon icon = new ImageIcon(
                            new ImageIcon(f.getAbsolutePath()).getImage().getScaledInstance(120,180,Image.SCALE_SMOOTH)
                        );
                        lblPoster.setIcon(icon);
                        lblPoster.setText("");
                    }
                }
            } catch (Exception e) {
                // bỏ qua lỗi ảnh
            }
        }
        card.add(lblPoster, BorderLayout.WEST);

     // ===== Info Panel =====
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(SEC_COLOR);

        JLabel lblTen = new JLabel("Tên phim: " + phim.getTenPhim());
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblTen);

        JLabel lblDoTuoi = new JLabel("Độ tuổi: " + phim.getDoTuoi());
        lblDoTuoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblDoTuoi);

        JLabel lblTheLoai = new JLabel("Thể loại: " + (phim.getTheLoai() != null ? phim.getTheLoai().getTenTheLoai() : "—"));
        lblTheLoai.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblTheLoai);

        // ===== Giá cơ bản =====
        String giaVeText = "—";
        if (scList != null && !scList.isEmpty()) {
            Double giaVe = scList.get(0).getGiaVeCoBan();
            if (giaVe != null) {
                giaVeText = formatMoney(giaVe);
            }
        }

        JLabel lblGiaVe = new JLabel("Giá cơ bản: " + giaVeText);
        lblGiaVe.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblGiaVe);
        // ===== 2D Phụ đề =====
        JLabel lbl2D = new JLabel("2D Phụ đề");
        lbl2D.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lbl2D);

        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // ===== Suất chiếu =====
        showtimesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(showtimesPanel);
        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
    
    private String formatMoney(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.of("vi", "VN"));
        return nf.format(Math.round(v)) + " đ";
    }

    private void clearForm() {
    	 txtMaSuatChieu.setText("");
    	 txtNgayChieu.setText("");
    	 txtGiaVe.setText("");
    	 if (cbPhim.getItemCount() > 0) cbPhim.setSelectedIndex(0);
    	 if (cbPhongChieu.getItemCount() > 0) cbPhongChieu.setSelectedIndex(0);
    	 // Reset giờ/phút
    	 if (cbGio.getItemCount() > 0) cbGio.setSelectedIndex(0);
    	 if (cbPhut.getItemCount() > 0) cbPhut.setSelectedIndex(0);
    }

    private void loadSelectedSuatChieuToForm(SuatChieu sc) {
        if (sc == null) return;

        // Mã suất chiếu
        txtMaSuatChieu.setText(String.valueOf(sc.getMaSuatChieu()));

        // Chọn phim trong comboBox
        Phim phim = sc.getPhim();
        if (phim != null) {
            for (int i = 0; i < cbPhim.getItemCount(); i++) {
                if (cbPhim.getItemAt(i).getMaPhim() == phim.getMaPhim()) {
                    cbPhim.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Chọn phòng chiếu
        PhongChieu phong = sc.getPhongChieu();
        if (phong != null) {
            for (int i = 0; i < cbPhongChieu.getItemCount(); i++) {
                if (cbPhongChieu.getItemAt(i).getMaPhongChieu() == phong.getMaPhongChieu()) {
                    cbPhongChieu.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Ngày chiếu
        if (sc.getNgayChieu() != null)
            txtNgayChieu.setText(sc.getNgayChieu().toString());

        // Giá vé
        txtGiaVe.setText(String.valueOf(sc.getGiaVeCoBan()));

     // Giờ chiếu: tách LocalTime ra giờ + phút
        if (sc.getGioChieu() != null) {
            LocalTime gioChieu = sc.getGioChieu();
            cbGio.setSelectedItem(String.format("%02d", gioChieu.getHour()));

            int phut = gioChieu.getMinute();
            // Làm tròn xuống gần nhất với 00,15,30,45
            if (phut < 15) phut = 0;
            else if (phut < 30) phut = 15;
            else if (phut < 45) phut = 30;
            else phut = 45;
            cbPhut.setSelectedItem(String.format("%02d", phut));
        }
    }


    private void deleteSelectedSuatChieu() {
        if (txtMaSuatChieu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một suất chiếu để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa suất chiếu này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int maSC = Integer.parseInt(txtMaSuatChieu.getText());

            boolean deleted = suatChieuDAO.delete(maSC);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Xóa suất chiếu thành công!");
                
                // Reload lại các card phim để các button giờ được cập nhật
                loadPhimCards();

                // Reset form
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }


    private void saveSuatChieu() {
        try {
            Integer ma = txtMaSuatChieu.getText().isEmpty() ? null : Integer.parseInt(txtMaSuatChieu.getText());
            Phim phim = (Phim) cbPhim.getSelectedItem();
            PhongChieu phong = (PhongChieu) cbPhongChieu.getSelectedItem();
            LocalDate ngay = LocalDate.parse(txtNgayChieu.getText());
            int gio = Integer.parseInt((String) cbGio.getSelectedItem());
            int phut = Integer.parseInt((String) cbPhut.getSelectedItem());
            LocalTime gioChieu = LocalTime.of(gio, phut);
            double gia = Double.parseDouble(txtGiaVe.getText());

            SuatChieu sc = new SuatChieu();
            sc.setMaSuatChieu(ma);
            sc.setPhim(phim);
            sc.setPhongChieu(phong);
            sc.setNgayChieu(ngay);
            sc.setGioChieu(gioChieu);
            sc.setGiaVeCoBan(gia);

            boolean success;
            try {
                if (ma == null) {
                    success = suatChieuDAO.insert(sc);  // Thêm mới, DB tự sinh mã
                } else {
                    success = suatChieuDAO.update(sc);  // Cập nhật
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu suất chiếu: " + ex.getMessage());
                return;
            }

            if (success) {
                JOptionPane.showMessageDialog(this, (ma == null ? "Thêm" : "Cập nhật") + " suất chiếu thành công!");
                loadPhimCards();   // Đồng bộ lại các card
                clearForm();       // Reset form
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + e.getMessage());
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
}
