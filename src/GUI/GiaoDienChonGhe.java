package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ConnectDB.ConnectDB;
import DAO.GheDAO;
import DAO.SuatChieuDAO;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import Entity.Phim;
import Entity.SuatChieu;
import Entity.Ghe;
import Entity.NhanVien;

@SuppressWarnings("serial")
public class GiaoDienChonGhe extends JFrame {
    private JPanel seatPanel;
    private JPanel infoPanel;
    private JLabel lblTotal;
    private JLabel lblSuat;
    private JButton btnContinue;
    private SuatChieu suatChieu;
    private double basePrice = 60000;
    private Map<String, JButton> seatButtons = new LinkedHashMap<>();
    private Map<String, Ghe> seatIdMap = new HashMap<>();
    private Set<String> selectedSeats = new LinkedHashSet<>();
    private List<SuatChieu> listSuatChieu;
    Set<Ghe> countedGhe = new HashSet<>();
    private NhanVien nhanVien;
    // colors
    private final Color COLOR_AVAILABLE = new Color(240, 240, 240);
    private final Color COLOR_BOOKED = Color.RED; // đỏ nhạt
    private final Color COLOR_SELECTED = Color.YELLOW; // vàng
    private final Color COLOR_BG = new Color(253, 252, 241);

    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    
    public GiaoDienChonGhe(Phim phim, SuatChieu suatChieu, NhanVien nhanVien) {
        this.nhanVien = nhanVien;

        SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
        this.listSuatChieu = suatChieuDAO.getAllSuatChieu(phim.getMaPhim());
        if (this.listSuatChieu == null) this.listSuatChieu = new ArrayList<>();

        if (suatChieu != null) {
            this.suatChieu = suatChieu;
        } else if (!listSuatChieu.isEmpty()) {
            this.suatChieu = listSuatChieu.get(0);
        }

        if (this.suatChieu != null && this.suatChieu.getGiaVeCoBan() != null) {
            this.basePrice = this.suatChieu.getGiaVeCoBan();
        }

        initUI();
        setTitle("Chọn ghế - Quản lý vé xem phim");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        getContentPane().setBackground(COLOR_BG);
        getContentPane().setLayout(new BorderLayout());

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(COLOR_BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(COLOR_BG);
        left.add(createSeatArea(), BorderLayout.CENTER);

        infoPanel = createInfoPanel();
        main.add(left, BorderLayout.CENTER);
        main.add(infoPanel, BorderLayout.EAST);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    private JPanel createSeatArea() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(COLOR_BG);

        // ===== MÀN CHIẾU =====
        JLabel lblManChieu = new JLabel("MÀN CHIẾU", JLabel.CENTER);
        lblManChieu.setOpaque(true);
        lblManChieu.setBackground(new Color(230, 230, 230));
        lblManChieu.setFont(new Font("Arial", Font.BOLD, 20));
        lblManChieu.setPreferredSize(new Dimension(100, 40));
        lblManChieu.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        wrapper.add(lblManChieu, BorderLayout.NORTH);

        // ===== LƯỚI GHẾ =====
        seatPanel = new JPanel(new GridBagLayout());
        seatPanel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.NONE;

        char[] rows = "ABCDEFGHIJ".toCharArray();
        int maxCols = 12;

        // Hàng tiêu đề cột
        gbc.gridy = 0;
        for (int col = 0; col <= maxCols; col++) {
            gbc.gridx = col;
            if (col == 0) seatPanel.add(new JLabel(""), gbc);
            else {
                JLabel lbl = new JLabel(String.valueOf(col));
                lbl.setPreferredSize(new Dimension(38, 20));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                seatPanel.add(lbl, gbc);
            }
        }

        // Lấy danh sách ghế theo phòng chiếu
        Connection conn = null;
		try {
			conn = ConnectDB.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<Ghe> gheList = new GheDAO(conn).getGheTheoPhong(suatChieu.getPhongChieu().getMaPhongChieu());
        seatIdMap.clear();
        for (Ghe g : gheList) {
            String[] ids;
            if (g.getLoaiGhe().getPhuThu() > 0) {
                // Ghế đôi
                ids = g.getTenGhe().split(",");
            } else {
                ids = new String[]{ g.getTenGhe() };
            }
            for (String id : ids) seatIdMap.put(id, g);
        }

        // Các hàng ghế A–J
        for (int r = 0; r < rows.length; r++) {
            gbc.gridy = r + 1;
            for (int c = 0; c <= maxCols; c++) {
                gbc.gridx = c;
                if (c == 0) {
                    JLabel rowLabel = new JLabel(String.valueOf(rows[r]));
                    rowLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    rowLabel.setPreferredSize(new Dimension(28, 28));
                    seatPanel.add(rowLabel, gbc);
                } else {
                    String seatId = String.valueOf(rows[r]) + c;
                    JButton seatBtn = new JButton(seatId);
                    seatBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    seatBtn.setPreferredSize(new Dimension(35, 35));
                    seatBtn.setMargin(new Insets(1, 1, 1, 1));
                    seatBtn.setFocusPainted(false);
                    seatBtn.setOpaque(true);

                    Ghe g = seatIdMap.get(seatId);
                    if (g != null && g.getTenGhe().contains(",")) {
                        seatBtn.setBackground(Color.PINK); // Ghế đôi
                    } else {
                        seatBtn.setBackground(COLOR_AVAILABLE);
                    }


                    seatBtn.addActionListener(new SeatAction(seatId, seatBtn));
                    seatButtons.put(seatId, seatBtn);
                    seatPanel.add(seatBtn, gbc);
                }
            }
        }

        JScrollPane sp = new JScrollPane(seatPanel);
        sp.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế"));
        sp.setPreferredSize(new Dimension(720, 500));
        wrapper.add(sp, BorderLayout.CENTER);

        // ===== CHÚ THÍCH =====
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legend.setBackground(COLOR_BG);
        legend.add(createLegendBox(COLOR_AVAILABLE, "Ghế trống"));
        legend.add(createLegendBox(COLOR_SELECTED, "Ghế đã chọn"));
        legend.add(createLegendBox(COLOR_BOOKED, "Ghế đã đặt"));
        legend.add(createLegendBox(Color.PINK, "Ghế đôi")); // thêm legend ghế đôi
        wrapper.add(legend, BorderLayout.SOUTH);

        return wrapper;
    }


    // ===== CLASS ACTION CHO GHẾ ĐÔI =====
    private class DoubleSeatAction implements ActionListener {
        private char row;
        private int col;
        private JButton btn;

        public DoubleSeatAction(char row, int col, JButton btn) {
            this.row = row;
            this.col = col;
            this.btn = btn;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Tìm cột đầu của cặp ghế đôi
            int firstCol = col % 2 == 0 ? col - 1 : col;
            String seatId1 = "" + row + firstCol;
            String seatId2 = "" + row + (firstCol + 1);

            JButton btn1 = seatButtons.get(seatId1);
            JButton btn2 = seatButtons.get(seatId2);

            if (selectedSeats.contains(seatId1)) {
                // Bỏ chọn cả 2 ghế
                selectedSeats.remove(seatId1);
                selectedSeats.remove(seatId2);
                if (btn1 != null) btn1.setBackground(Color.PINK);
                if (btn2 != null) btn2.setBackground(Color.PINK);
            } else {
                // Chọn cả 2 ghế
                selectedSeats.add(seatId1);
                selectedSeats.add(seatId2);
                if (btn1 != null) btn1.setBackground(COLOR_SELECTED);
                if (btn2 != null) btn2.setBackground(COLOR_SELECTED);
            }

            updateSelectionCallback.run();
        }
    }

    private JPanel createLegendBox(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel box = new JPanel();
        box.setBackground(color);
        box.setPreferredSize(new Dimension(20, 20));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(box);
        JLabel lbl = new JLabel(text);
        p.add(lbl);
        return p;
    }

    private JPanel createInfoPanel() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(320, 0));
        p.setBackground(COLOR_BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        // ======= Poster phim =======
        JLabel poster = new JLabel("<html><center>Poster<br>không có</center></html>", JLabel.CENTER);
        poster.setPreferredSize(new Dimension(260, 350));
        poster.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        poster.setHorizontalAlignment(SwingConstants.CENTER);

        if (suatChieu != null && suatChieu.getPhim() != null && suatChieu.getPhim().getImg() != null) {
            try {
                String path = suatChieu.getPhim().getImg(); // ví dụ "/ImgPhim/endgame.jpg"
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(
                            new ImageIcon(imgURL).getImage().getScaledInstance(260, 350, Image.SCALE_SMOOTH)
                    );
                    poster.setIcon(icon);
                    poster.setText("");
                }
            } catch (Exception e) {
                // bỏ qua nếu lỗi ảnh
            }
        }
        p.add(poster);
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        // ======= Thông tin phim =======
        Phim phim = (suatChieu != null) ? suatChieu.getPhim() : null;
        String tenPhim = (phim != null && phim.getTenPhim() != null) ? phim.getTenPhim() : "Tên phim";
        String daoDien = (phim != null && phim.getDaoDien() != null) ? phim.getDaoDien() : "—";
        String doTuoi = (phim != null && phim.getDoTuoi() != null) ? phim.getDoTuoi() : "—";
        int thoiLuong = (phim != null && phim.getThoiLuong() > 0) ? phim.getThoiLuong() : 0;
        String gioChieu = (suatChieu != null && suatChieu.getGioChieu() != null)
                ? suatChieu.getGioChieu().toString()
                : "";

        JLabel lblTitle = new JLabel(tenPhim);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblTitle);
        p.add(Box.createRigidArea(new Dimension(0, 6)));

        lblSuat = new JLabel("Suất: " + gioChieu);
        lblSuat.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblSuat);

        JLabel lblDaoDien = new JLabel("Đạo diễn: " + daoDien);
        lblDaoDien.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblDaoDien);

        JLabel lblDoTuoi = new JLabel("Độ tuổi: " + doTuoi);
        lblDoTuoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblDoTuoi);

        if (thoiLuong > 0) {
            JLabel lblThoiLuong = new JLabel("Thời lượng: " + thoiLuong + " phút");
            lblThoiLuong.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(lblThoiLuong);
        }

        p.add(Box.createRigidArea(new Dimension(0, 10)));
        p.add(new JSeparator(SwingConstants.HORIZONTAL));
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        // ======= Danh sách ghế đã chọn =======
        JLabel lblSel = new JLabel("Ghế đã chọn:");
        lblSel.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblSel);

        JTextArea taSeats = new JTextArea(4, 1);
        taSeats.setEditable(false);
        taSeats.setLineWrap(true);
        taSeats.setWrapStyleWord(true);
        JScrollPane spSeats = new JScrollPane(taSeats);
        spSeats.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(spSeats);

        p.add(Box.createRigidArea(new Dimension(0, 10)));

        lblTotal = new JLabel("Tổng cộng: 0 đ");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblTotal);

        p.add(Box.createVerticalGlue());

        // ======= Các nút chức năng =======
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(COLOR_BG);

        JButton btnBack = taoBtn("Quay lại");
        btnContinue = taoBtn("Tiếp tục");
        btnContinue.setBackground(new Color(255, 153, 51));
        btnContinue.setForeground(Color.WHITE);

        btnBack.addActionListener(e -> {
            dispose(); // đóng giao diện hiện tại
            SwingUtilities.invokeLater(() -> new GiaoDienChonSuatChieu(phim, nhanVien).setVisible(true));
        });

        btnContinue.addActionListener(e -> {
            new GiaoDienThanhToan(countedGhe, suatChieu, nhanVien).setVisible(true);
            dispose();
        });

        btns.add(btnBack);
        btns.add(btnContinue);
        p.add(btns);

     // ======= Callback cập nhật thông tin =======
        this.updateSelectionCallback = () -> {
            StringBuilder sb = new StringBuilder();
            double total = 0;

            countedGhe = new HashSet<>(); // để ghế đôi không bị tính 2 lần

            for (String seatId : selectedSeats) {
                Ghe g = seatIdMap.get(seatId);
                if (g == null) continue;

                if (g.getTenGhe().contains(",") && !countedGhe.contains(g)) {
                    // Ghế đôi: 2 ghế + phụ thu
                    total += 2 * basePrice + g.getLoaiGhe().getPhuThu();
                    countedGhe.add(g);
                    sb.append(g.getTenGhe()).append(", ");
                } else if (!g.getTenGhe().contains(",")) {
                    // Ghế thường
                	countedGhe.add(g);
                    total += basePrice;
                    sb.append(seatId).append(", ");
                }
            }

            // Xóa dấu ", " cuối cùng
            if (sb.length() >= 2) sb.setLength(sb.length() - 2);

            // Cập nhật text và tổng cộng
            taSeats.setText(sb.toString());
            lblTotal.setText("Tổng cộng: " + formatMoney(total));
            btnContinue.setEnabled(!selectedSeats.isEmpty());
        };

        return p;
    }


    private Runnable updateSelectionCallback = () -> {};

    private String formatMoney(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(Locale.of("vi", "VN"));
        return nf.format(Math.round(v)) + " đ";
    }

    private class SeatAction implements ActionListener {
        private String seatId;
        private JButton btn;

        public SeatAction(String seatId, JButton btn) {
            this.seatId = seatId;
            this.btn = btn;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Lấy thông tin ghế
            Ghe g = seatIdMap.get(seatId);
            if (g == null) return;

            // Kiểm tra nếu là ghế đôi
            boolean isDouble = g.getLoaiGhe() != null && g.getLoaiGhe().getPhuThu() > 0;

            if (isDouble) {
                // Lấy danh sách ghế thực sự của ghế đôi
                String[] pairSeats = g.getTenGhe().split(",");
                boolean selected = selectedSeats.contains(pairSeats[0]);

                if (selected) {
                    // Bỏ chọn cả cặp
                    for (String s : pairSeats) {
                        selectedSeats.remove(s);
                        JButton b = seatButtons.get(s);
                        if (b != null) {
                            // Nếu ghế là đôi -> màu hồng, nếu ghế thường -> màu trống
                            if (g.getTenGhe().contains(",")) {
                                b.setBackground(Color.PINK);
                            } else {
                                b.setBackground(COLOR_AVAILABLE);
                            }
                        }
                    }
                } else {
                    // Chọn cả cặp
                    for (String s : pairSeats) {
                        selectedSeats.add(s);
                        JButton b = seatButtons.get(s);
                        if (b != null) b.setBackground(COLOR_SELECTED);
                    }
                }
            } else {
                // Ghế thường
                if (selectedSeats.contains(seatId)) {
                    selectedSeats.remove(seatId);
                    btn.setBackground(COLOR_AVAILABLE);
                } else {
                    selectedSeats.add(seatId);
                    btn.setBackground(COLOR_SELECTED);
                }
            }

            updateSelectionCallback.run();
        }
    }

    private void loadSuatChieu(int maPhim) {
    	SuatChieuDAO suatChieuDAO = new SuatChieuDAO();
    	this.listSuatChieu = suatChieuDAO.getAllSuatChieu(maPhim);
    }
    
    private JButton taoBtn(String ten) {
    	JButton btn = new JButton(ten);
    	btn.setBackground(RED_COLOR);
    	btn.setForeground(BTN_COLOR);
    	btn.setPreferredSize(new Dimension(90, 40));
    	btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
    	btn.setOpaque(true);
    	btn.setBorderPainted(false);
    	btn.setContentAreaFilled(true);
		btn.setFocusPainted(false);

    	return btn;
    }
}
