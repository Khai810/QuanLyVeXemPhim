package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import DAO.SuatChieuDAO;
import Entity.SuatChieu;
import Entity.NhanVien;
import Entity.Phim;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class GiaoDienChonSuatChieu extends JFrame {

    private JPanel panelCenterMain, showtimesPanel;
    private JButton btnQuayLai;

    private NhanVien nhanVien;  
    private Phim phim;
	private JPanel datePanel;         
	private List<SuatChieu> scList;
    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    

    private static final Color ORANGE_DARK = new Color(199, 91, 18);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    private static final Font fontChu = new Font("Segoe UI", Font.PLAIN, 14);

    public GiaoDienChonSuatChieu(Phim phim, NhanVien nv) {
        this.phim = phim;
        this.nhanVien = nv;

        setTitle("Chọn suất chiếu");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(252, 247, 223));

        addCenter();
        addSouth();
        loadPhimCards();
    }

    private void addCenter() {
        panelCenterMain = new JPanel();
        panelCenterMain.setBackground(new Color(252,247,223));
        panelCenterMain.setLayout(new BoxLayout(panelCenterMain, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelCenterMain);
        scroll.getVerticalScrollBar().setUnitIncrement(8);

        add(scroll, BorderLayout.CENTER);
    }

    private void addSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        south.setBackground(new Color(252,247,223));
        btnQuayLai = taoBtn("Quay lại");
        btnQuayLai.setBackground(RED_COLOR);
        btnQuayLai.setForeground(BTN_COLOR);
        btnQuayLai.setPreferredSize(new Dimension(200, 50));
        btnQuayLai.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnQuayLai.addActionListener(e -> {
            new GiaoDienChonPhim(nhanVien).setVisible(true); // Mở lại danh sách phim
            dispose(); // Đóng cửa sổ hiện tại
        });

        south.add(btnQuayLai);
        add(south, BorderLayout.SOUTH);
    }

    private void loadPhimCards() {
        panelCenterMain.removeAll();

        // Load tất cả suất chiếu của phim
        SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
        scList = suatChieuDAO.getAllSuatChieu(phim.getMaPhim());

        // Tạo showtimesPanel trước, để dùng cho cả ngày đầu tiên
        showtimesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        showtimesPanel.setBackground(PRI_COLOR);
        showtimesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tạo panel chọn ngày
        createDatePanel(); // trong này sẽ gọi firstDateButton.doClick()

        // Hiển thị thông tin phim + poster
        panelCenterMain.add(createPhimCard(phim));

        panelCenterMain.revalidate();
        panelCenterMain.repaint();
    }
 // === Tạo panel chọn ngày ===
    private void createDatePanel() {
        if (datePanel != null) panelCenterMain.remove(datePanel);

        datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        datePanel.setBackground(SEC_COLOR);

        // Lấy danh sách ngày duy nhất có suất chiếu
        Set<String> uniqueDates = new TreeSet<>();
        for (SuatChieu sc : scList) {
            uniqueDates.add(sc.getNgayChieu().toString()); // yyyy-MM-dd
        }

        JButton firstDateButton = null; // lưu nút ngày đầu tiên

        for (String dateStr : uniqueDates) {
            JButton btnDate = taoBtn(dateStr);
            btnDate.setPreferredSize(new Dimension(150, 40));
            
            btnDate.addActionListener(e -> {
                // Reset màu các nút khác
                for (Component c : datePanel.getComponents()) {
                    if (c instanceof JButton) {
                    	((JButton) c).setBackground(RED_COLOR);
                    }
                }
                // Tô màu nút đang chọn
                btnDate.setBackground(ORANGE_DARK);
//                btnDate.setForeground(Color.WHITE);

                // Hiển thị giờ chiếu cho ngày đã chọn
                showTimesForDate(dateStr);
            });

            datePanel.add(btnDate);

            if (firstDateButton == null) firstDateButton = btnDate; // lưu nút đầu tiên
        }

        panelCenterMain.add(datePanel, 0); // thêm lên trên cùng

        // ===== Tự động chọn ngày đầu tiên =====
        if (firstDateButton != null) {
            firstDateButton.doClick(); // giả lập click
        }
    }

 // === Hiển thị các nút giờ chiếu trong ngày đã chọn ===
    private void showTimesForDate(String dateStr) {
        showtimesPanel.removeAll();  // xóa các nút cũ

        for (SuatChieu sc : scList) {
            if (sc.getNgayChieu().toString().equals(dateStr)) {
                JButton btnSC = taoBtn(sc.getGioChieu().toString());
                btnSC.addActionListener(ev -> {
                    new GiaoDienChonGhe(phim, sc, nhanVien).setVisible(true);
                    dispose();
                });
                showtimesPanel.add(btnSC);
            }
        }

        showtimesPanel.revalidate();
        showtimesPanel.repaint();
    }


    private JPanel createPhimCard(Phim phim) {
    	SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(10,10,10,10));
        card.setBackground(new Color(253,252,241));
     /// ===== Poster với strut =====
        JPanel posterPanel = new JPanel();
        posterPanel.setLayout(new BoxLayout(posterPanel, BoxLayout.Y_AXIS));
        posterPanel.setBackground(SEC_COLOR);
        // Tạo JLabel poster
        JLabel lblPoster = new JLabel("No Image", JLabel.CENTER);
        lblPoster.setPreferredSize(new Dimension(260, 350));
        lblPoster.setMaximumSize(new Dimension(260, 350));
        lblPoster.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Load ảnh
        if (phim.getImg() != null && !phim.getImg().isEmpty()) {
            try {
                // Thử load từ resources
                java.net.URL imgURL = getClass().getResource(phim.getImg());
                ImageIcon icon = null;
                if (imgURL != null) {
                    icon = new ImageIcon(
                            new ImageIcon(imgURL).getImage().getScaledInstance(260, 350, Image.SCALE_SMOOTH)
                    );
                } else {
                    // Thử load từ file hệ thống
                    java.io.File f = new java.io.File(phim.getImg());
                    if(f.exists()) {
                        icon = new ImageIcon(
                                new ImageIcon(f.getAbsolutePath()).getImage().getScaledInstance(260, 350, Image.SCALE_SMOOTH)
                        );
                    }
                }
                if (icon != null) {
                    lblPoster.setIcon(icon);
                    lblPoster.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        posterPanel.add(lblPoster);
        posterPanel.add(Box.createVerticalStrut(10));
        card.add(posterPanel, BorderLayout.WEST);
        // Tên phim
        Font titleFont = new Font("Segoe UI", Font.BOLD, 20); 
        Font infoFont = fontChu;
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(SEC_COLOR);

        JLabel lblTen = new JLabel("Tên phim: " + phim.getTenPhim());
        lblTen.setFont(titleFont);
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblTen);

        JLabel lblDoTuoi = new JLabel("Độ tuổi: " + phim.getDoTuoi());
        lblDoTuoi.setFont(infoFont);
        lblDoTuoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblDoTuoi);

        JLabel lblTheLoai = new JLabel("Thể loại: " + (phim.getTheLoai() != null ? phim.getTheLoai().getTenTheLoai() : "—"));
        lblTheLoai.setFont(infoFont);
        lblTheLoai.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblTheLoai);
        
     // ===== Giá cơ bản =====
        List<SuatChieu> scList = suatChieuDAO.getAllSuatChieu(phim.getMaPhim());

        String giaVeText = "—";
        if (scList != null && !scList.isEmpty()) {
            Double giaVe = scList.get(0).getGiaVeCoBan();
            if (giaVe != null) {
                giaVeText = formatMoney(giaVe);
            }
        }

        JLabel lblGiaVe = new JLabel("Giá cơ bản: " + giaVeText);
        lblGiaVe.setFont(infoFont);
        lblGiaVe.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lblGiaVe);
        // ===== 2D Phụ đề =====
        JLabel lbl2D = new JLabel("2D Phụ đề");
        lbl2D.setFont(infoFont);
        lbl2D.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lbl2D);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        infoPanel.add(showtimesPanel);
        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
    private String formatMoney(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.of("vi", "VN"));
        return nf.format(Math.round(v)) + " đ";
    }
    
    private JButton taoBtn(String tenBtn) {
		JButton btn = new JButton(tenBtn);
        
		btn.setBackground(RED_COLOR);
		btn.setForeground(BTN_COLOR);
		btn.setPreferredSize(new Dimension(150, 40));
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
		btn.setFocusPainted(false);
		btn.setOpaque(true);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(true);
		
		return btn;
	}
}
