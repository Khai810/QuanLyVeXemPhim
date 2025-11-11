package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ConnectDB.ConnectDB;
import GUI.GiaoDienChonPhim;
import DAO.SuatChieuDAO;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import Entity.Phim;
import Entity.SuatChieu;
import Entity.Ve;
import Entity.Ghe;
import Entity.LoaiGhe;
import Entity.PhongChieu;

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
    private Set<String> selectedSeats = new LinkedHashSet<>();
    private List<SuatChieu> listSuatChieu;
    // colors
    private final Color COLOR_AVAILABLE = new Color(240, 240, 240);
    private final Color COLOR_BOOKED = Color.RED; // đỏ nhạt
    private final Color COLOR_SELECTED = Color.YELLOW; // vàng
    private final Color COLOR_BG = Color.WHITE;

    public GiaoDienChonGhe(Phim phim) {
        loadSuatChieu(phim.getMaPhim());
        
    	this.suatChieu = listSuatChieu.get(0);
        if (suatChieu != null && suatChieu.getGiaVeCoBan() != null) {
            this.basePrice = suatChieu.getGiaVeCoBan();
        }
        initUI();
        setTitle("Chọn ghế - Quản lý vé xem phim");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 760);
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
        left.add(createTimeSelectorPanel(), BorderLayout.NORTH);
        left.add(createSeatArea(), BorderLayout.CENTER);

        infoPanel = createInfoPanel();
        main.add(left, BorderLayout.CENTER);
        main.add(infoPanel, BorderLayout.EAST);

        getContentPane().add(main, BorderLayout.CENTER);
    }

    private JPanel createTimeSelectorPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(COLOR_BG);

        JLabel lbl = new JLabel("Đổi suất chiếu:");
        p.add(lbl);

        for (SuatChieu sc : listSuatChieu) {
            JButton b = new JButton(sc.getGioChieu().toString());
            b.setFocusPainted(false);

            b.addActionListener(e -> {
                //Reset lại màu các nút khác
                for (Component c : p.getComponents()) {
                    if (c instanceof JButton) {
                        ((JButton) c).setBackground(null);
                        ((JButton) c).setForeground(Color.BLACK);
                    }
                }
                //Tô màu nút đang chọn
                b.setBackground(new Color(0, 120, 215));
                b.setForeground(Color.WHITE);

                //Cập nhật suất chiếu hiện tại
                suatChieu = sc;

                //Cập nhật lại label "Suất: ..."
                if (lblSuat != null) {
                    lblSuat.setText("Suất: " + suatChieu.getGioChieu());
                }
            });

            p.add(b);
        }

        return p;
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
        seatPanel = new JPanel();
        seatPanel.setBackground(COLOR_BG);
        seatPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.NONE;

        char[] rows = "ABCDEFGHIJ".toCharArray();
        int maxCols = 12;

        // Hàng tiêu đề cột (1..12)
        gbc.gridy = 0;
        for (int col = 0; col <= maxCols; col++) {
            gbc.gridx = col;
            if (col == 0) {
                seatPanel.add(new JLabel(""), gbc);
            } else {
                JLabel lbl = new JLabel(String.valueOf(col));
                lbl.setPreferredSize(new Dimension(38, 20));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                seatPanel.add(lbl, gbc);
            }
        }

        // Các hàng ghế (A..J)
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
                	    seatBtn.setBackground(COLOR_AVAILABLE);
                	    seatBtn.setOpaque(true);

                    
                    boolean booked = ((r + c) % 11 == 0);
                    if (booked) {
                        seatBtn.setBackground(COLOR_BOOKED);
                        seatBtn.setEnabled(false);
                    } else {
                        seatBtn.addActionListener(new SeatAction(seatId, seatBtn));
                    }

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

        wrapper.add(legend, BorderLayout.SOUTH);
        return wrapper;
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

        JButton btnBack = new JButton("Quay lại");
        btnContinue = new JButton("Tiếp tục");
        btnContinue.setBackground(new Color(255, 153, 51));
        btnContinue.setForeground(Color.WHITE);

        btnBack.addActionListener(e -> {
            dispose(); // đóng giao diện hiện tại
            SwingUtilities.invokeLater(() -> new GiaoDienChonPhim().setVisible(true));
        });

        btnContinue.addActionListener(e -> {
            new GiaoDienThanhToan(selectedSeats, suatChieu).setVisible(true);
            dispose();
        });

        btns.add(btnBack);
        btns.add(btnContinue);
        p.add(btns);

        // ======= Callback cập nhật thông tin =======
        this.updateSelectionCallback = () -> {
            taSeats.setText(String.join(", ", selectedSeats));
            lblTotal.setText("Tổng cộng: " + formatMoney(selectedSeats.size() * basePrice));
            btnContinue.setEnabled(!selectedSeats.isEmpty());
        };
        btnContinue.setEnabled(false);
        updateSelectionCallback.run();

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
            if (selectedSeats.contains(seatId)) {
                selectedSeats.remove(seatId);
                btn.setBackground(COLOR_AVAILABLE);
            } else {
                selectedSeats.add(seatId);
                btn.setBackground(COLOR_SELECTED);
            }
            updateSelectionCallback.run();
        }
    }

    private void loadSuatChieu(int maPhim) {
        Connection conn = null;
    	try {
            conn = ConnectDB.getConnection();
            SuatChieuDAO suatChieuDAO = new SuatChieuDAO(conn);
            this.listSuatChieu = suatChieuDAO.getAllSuatChieu(maPhim);
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
