package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;

import ConnectDB.ConnectDB;
import DAO.PhongChieuDAO;
import Entity.PhongChieu;

@SuppressWarnings("serial")
public class GiaoDienQuanLyPhongChieu extends JPanel {

    private JSplitPane splitPane;
    private JPanel panelLeft;
    private JPanel panelRight;

    private JTextField txtMaPhong, txtTenPhong, txtSoLuongGhe;
    private JButton btnThem, btnXoa, btnLuu;

    private List<PhongChieu> dsPhong;
    private JPanel cardContainer;
    private PhongChieu phongSelected = null;
    private PhongChieuDAO phongDAO;
    LoadHinhAnh load = new LoadHinhAnh();
    
    private static final String imgStandard = "/img/Standard_room_icon.png";
    private static final String imgIMAX = "/img/imax_room_icon.png";
    private static final String img4DX = "/img/4DX_room_icon.png";
    private static final String imgVIP = "/img/VIP_room_icon.png";

    private JPanel selectedCard = null;

    private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color ORANGE_DARK = new Color(199, 91, 18);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    private static final Color BTN_COLOR = Color.WHITE;

    public GiaoDienQuanLyPhongChieu() {
        try {
            phongDAO = new PhongChieuDAO(ConnectDB.getConnection());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        initUI();
        loadData();
    }

    private void initUI() {
        // Panel trái: card container
    	cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS)); // mỗi rowPanel là 1 dòng
        cardContainer.setBackground(SEC_COLOR);
        
        JScrollPane scrollLeft = new JScrollPane(cardContainer);
        scrollLeft.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Panel phải: form
        panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelRight.setBackground(PRI_COLOR);
        buildForm(panelRight);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLeft, panelRight);
        splitPane.setResizeWeight(0.7);
        add(splitPane, BorderLayout.CENTER);
    }

    private void buildForm(JPanel panel) {
        JLabel lblTitle = new JLabel("Quản Lý Phòng Chiếu");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));

        panel.add(createFieldPanel("Mã phòng:", txtMaPhong = new JTextField(15)));
        txtMaPhong.setEnabled(false);

        panel.add(createFieldPanel("Tên phòng:", txtTenPhong = new JTextField(15)));
        panel.add(createFieldPanel("Số lượng ghế:", txtSoLuongGhe = new JTextField(15)));

        panel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(PRI_COLOR);
        btnThem = taoBtn("Thêm");
        btnXoa = taoBtn("Xóa");
        btnLuu = taoBtn("Lưu");

        btnPanel.add(btnThem);
        btnPanel.add(btnXoa);
        btnPanel.add(btnLuu);
        panel.add(btnPanel);

        // ActionListeners theo mẫu GiaoDienSuatChieu
        btnThem.addActionListener(e -> clearForm()); // Chế độ thêm: reset form
        btnXoa.addActionListener(e -> deleteSelectedPhong());
        btnLuu.addActionListener(e -> savePhong());
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(PRI_COLOR);
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 25));
        panel.add(lbl);
        panel.add(field);
        return panel;
    }

    private JButton taoBtn(String ten) {
        JButton btn = new JButton(ten);
        btn.setBackground(RED_COLOR);
        btn.setForeground(BTN_COLOR);
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
		btn.setFocusPainted(false);

        return btn;
    }

    private void loadData() {
        dsPhong = phongDAO.getAllPhongChieu();
        cardContainer.removeAll();

        // Thêm vertical glue trên cùng để đẩy xuống
        cardContainer.add(Box.createVerticalGlue());

        int count = 0;
        JPanel rowPanel = null;

        for (PhongChieu pc : dsPhong) {
            if (count % 3 == 0) { // mỗi 3 card 1 dòng
                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 20: khoảng cách ngang, 10: khoảng cách dọc
                rowPanel.setBackground(SEC_COLOR);
                cardContainer.add(rowPanel);
            }
            rowPanel.add(createPhongCard(pc));
            count++;
        }

        // Thêm vertical glue cuối cùng để đẩy nội dung lên nếu cần
        cardContainer.add(Box.createVerticalGlue());

        cardContainer.revalidate();
        cardContainer.repaint();
    }



    private JPanel createPhongCard(PhongChieu pc) {
    	JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
    	card.setPreferredSize(new Dimension(200, 140));
    	card.setOpaque(false); // để gradient hiển thị
    	card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    	card.setBorder(null);
    	card.setBackground(PRI_COLOR);

        // Chọn icon theo loại phòng
        String imgPath = null;
        String tenPhong = pc.getTenPhong().toLowerCase();
        if (tenPhong.contains("standard")) imgPath = imgStandard;
        else if (tenPhong.contains("imax")) imgPath = imgIMAX;
        else if (tenPhong.contains("4dx")) imgPath = img4DX;
        else if (tenPhong.contains("vip")) imgPath = imgVIP;

        // Icon
        ImageIcon iconPhong = load.taiHinhAnh(imgPath, 60, 60); 
        JLabel lblIcon = new JLabel(iconPhong);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(lblIcon);
        
        // Tên phòng
        JLabel lblName = new JLabel(pc.getTenPhong(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(lblName);

        // Số ghế
        JLabel lblInfo = new JLabel("Số ghế: " + pc.getSoLuongGhe(), SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(5));
        card.add(lblInfo);

        card.addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseEntered(MouseEvent e) {
            	if (card != selectedCard) {
            	    card.setBorder(BorderFactory.createLineBorder(RED_COLOR, 3));
            	}
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (card != selectedCard)
                	card.setBorder(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectCard(card, pc);
            }
        });

        return card;
    }

    private void selectCard(JPanel card, PhongChieu pc) {
        if (selectedCard == card) { // click lại để bỏ chọn
            card.setBackground(PRI_COLOR); 
            selectedCard = null;
            phongSelected = null;
            clearForm();
            return;
        }

        // nếu click vào card khác
        if (selectedCard != null) {
            selectedCard.setBackground(PRI_COLOR);
        }
        selectedCard = card;
        card.setBackground(ORANGE_DARK);
        card.setBorder(null);
        phongSelected = pc;
        loadSelectedPhongToForm(pc);
    }

    private void loadSelectedPhongToForm(PhongChieu pc) {
        if (pc == null) return;
        txtMaPhong.setText(String.valueOf(pc.getMaPhongChieu()));
        txtTenPhong.setText(pc.getTenPhong());
        txtSoLuongGhe.setText(String.valueOf(pc.getSoLuongGhe()));
    }

    private void clearForm() {
        txtMaPhong.setText("");
        txtTenPhong.setText("");
        txtSoLuongGhe.setText("");
        phongSelected = null;
        if (selectedCard != null) {
            selectedCard.setBackground(PRI_COLOR);
            selectedCard = null;
        }
    }

    private void deleteSelectedPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa phòng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int ma = Integer.parseInt(txtMaPhong.getText());
            boolean deleted = phongDAO.deletePhong(ma);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void savePhong() {
        try {
            Integer ma = txtMaPhong.getText().isEmpty() ? null : Integer.parseInt(txtMaPhong.getText());
            String ten = txtTenPhong.getText().trim();
            int soLuong = Integer.parseInt(txtSoLuongGhe.getText().trim());

            PhongChieu pc = new PhongChieu();
            pc.setMaPhongChieu(ma);
            pc.setTenPhong(ten);
            pc.setSoLuongGhe(soLuong);

            boolean success;
            if (ma == null) {
                success = phongDAO.insertPhong(pc); // Thêm mới
            } else {
                success = phongDAO.updatePhong(pc); // Cập nhật
            }

            if (success) {
                JOptionPane.showMessageDialog(this, (ma == null ? "Thêm" : "Cập nhật") + " phòng thành công!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng ghế phải là số!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    public class LoadHinhAnh {
        public ImageIcon taiHinhAnh(String path, int width, int height) {
            URL url = getClass().getResource(path); // tìm file trong classpath
            if (url == null) {
                System.err.println("Không tìm thấy ảnh: " + path);
                return null;
            }
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
    }
}
