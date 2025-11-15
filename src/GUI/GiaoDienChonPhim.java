package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ConnectDB.ConnectDB;
import DAO.PhimDAO;
import Entity.NhanVien;
import Entity.Phim;


public class GiaoDienChonPhim extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	JLabel lblPhimDangChieu;
	JButton btnTim;
	JTextField txtTim;
	JPanel pnlDanhSachPhim, pnlTitle;
	JScrollPane spDanhSachPhim;
	List<Phim> listPhim;
    
	private static final Dimension CARD_SIZE = new Dimension(240, 460);

	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
	
    NhanVien nhanVien;
    
	public GiaoDienChonPhim(NhanVien nhanVien) {
		super();
		this.nhanVien = nhanVien;
		
		MenuChinh menuBar = new MenuChinh(this, nhanVien);
		this.setJMenuBar(menuBar);
		MenuToggleUtil.addToggleSupport(this, menuBar);
		
		loadPhim();
		
		int len = listPhim.size();
		int columns = len / 4;

		pnlDanhSachPhim = new JPanel(new GridLayout(columns, 5, 10, 10));		
        pnlDanhSachPhim.setBackground(SEC_COLOR);
        
        // Xu ly add Card
        for(Phim phim : listPhim) {
        	pnlDanhSachPhim.add(CardPhim(phim));
        }
        
        // 3. Đặt pnlDanhSachPhim vào một JScrollPane
        // Điều này rất quan trọng để có thể cuộn (scroll) khi có nhiều phim
        spDanhSachPhim = new JScrollPane(pnlDanhSachPhim);
        spDanhSachPhim.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spDanhSachPhim.getVerticalScrollBar().setUnitIncrement(8); // Tăng tốc độ cuộn
        spDanhSachPhim.getViewport().setBackground(SEC_COLOR);

        // Thêm tiêu đề "Phim Đang Chiếu"
        pnlTitle = new JPanel();
        pnlTitle.setLayout(new BorderLayout());
        pnlTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlTitle.setBackground(PRI_COLOR);

        lblPhimDangChieu = new JLabel("Phim Đang Chiếu");
        lblPhimDangChieu.setFont(new Font("Arial", Font.BOLD, 24));
//        lblPhimDangChieu.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        lblPhimDangChieu.setForeground(TEXT_COLOR);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(PRI_COLOR);

        txtTim = new JTextField();
        txtTim.setPreferredSize(new Dimension(400, 50));
//        txtTim.setBackground(SEC_COLOR);
        txtTim.setForeground(TEXT_COLOR);
        txtTim.setCaretColor(TEXT_COLOR);
        
        btnTim = new JButton("Tìm tên phim");
        btnTim.setBackground(RED_COLOR);
        btnTim.setForeground(BTN_COLOR);
        btnTim.setPreferredSize(new Dimension(200, 50));
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnTim.addActionListener(this);
        btnTim.setOpaque(true);
        btnTim.setBorderPainted(false);
        btnTim.setContentAreaFilled(true);
        
        pnlSearch.add(txtTim);
        pnlSearch.add(btnTim);

        pnlTitle.add(lblPhimDangChieu, BorderLayout.WEST);
        pnlTitle.add(pnlSearch, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(pnlTitle, BorderLayout.NORTH);
        add(spDanhSachPhim, BorderLayout.CENTER);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1400, 800);
		setLocationRelativeTo(null); // Căn giữa màn hình
	}

	public JPanel CardPhim(Phim phim) {
		JPanel cardPhim = new JPanel();
		cardPhim.setLayout(new BorderLayout());
		cardPhim.setPreferredSize(CARD_SIZE);
		cardPhim.setMaximumSize(CARD_SIZE);
		cardPhim.setMinimumSize(CARD_SIZE);
        cardPhim.setBackground(SEC_COLOR);
		cardPhim.setBorder(new EmptyBorder(15, 20, 15, 20));
		
		// Thêm hình Ảnh
		LoadHinhAnh loadHinhAnh = new LoadHinhAnh();
		ImageIcon hinhAnh = loadHinhAnh.taiHinhAnh(phim.getImg(), CARD_SIZE.width, CARD_SIZE.height -110);
	
        JLabel lblHinhAnh = new JLabel(hinhAnh);
        lblHinhAnh.setBounds(0, 0, 218, 280);
        
        cardPhim.add(lblHinhAnh, BorderLayout.CENTER);
        cardPhim.add(thongTinPhim(phim), BorderLayout.SOUTH); // Thêm thông tin của phim
        
     // --- THÊM BỘ LẮNG NGHE SỰ KIỆN CHUỘT VÀO CARD ---
        cardPhim.addMouseListener(new java.awt.event.MouseAdapter() {
            
            // 1. Bắt sự kiện bấm chuột
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Gọi hàm helper
                chonPhim(phim, cardPhim); 
            }

            // 2. (Bonus) Thêm hiệu ứng khi di chuột vào
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Đổi viền sang màu đỏ đậm và dày hơn
                cardPhim.setBorder(BorderFactory.createLineBorder(new Color(180, 0, 0), 2));
                // Đổi con trỏ chuột thành hình bàn tay
                cardPhim.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            // 3. (Bonus) Trả lại như cũ khi di chuột ra
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trả lại viền xám mỏng
                cardPhim.setBorder(new EmptyBorder(5, 5, 5, 5));
                // Trả lại con trỏ chuột mặc định
                cardPhim.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        // --- KẾT THÚC PHẦN THÊM MOUSE LISTENER ---
		return cardPhim;
	}
	
	private Component thongTinPhim(Phim phim) {
        JPanel pnlThongTin = new JPanel();
        pnlThongTin.setLayout(new BoxLayout(pnlThongTin, BoxLayout.Y_AXIS));
        pnlThongTin.setBackground(SEC_COLOR);
        pnlThongTin.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn lề trái

        // Tên Phim
        JLabel lblTenPhim = new JLabel(phim.getTenPhim());
        lblTenPhim.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTenPhim.setForeground(TEXT_COLOR);
        lblTenPhim.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDaoDien = taoLabel("Đạo diễn: ", phim.getDaoDien());
        
        // Thể loại (Giả sử getTheLoai() trả về đối tượng TheLoai
        // và TheLoai có getTenTheLoai())
        String tenTheLoai = (phim.getTheLoai() != null) ? phim.getTheLoai().getTenTheLoai() : "Chưa xác định";
        JLabel lblTheLoai = taoLabel("Thể loại: ", tenTheLoai);
       
        JLabel lblDoTuoi = taoLabel("Độ tuổi: ", phim.getDoTuoi());
        
        // Thời lượng (Giả sử getThoiLuong() trả về số phút (int))
        JLabel lblThoiLuong = taoLabel("Thời lượng: ",phim.getThoiLuong() + " phút");
        
        // Khởi chiếu (Giả sử getNgayKhoiChieu() trả về LocalDate)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String ngayKC = (phim.getNgayKhoiChieu() != null) ? phim.getNgayKhoiChieu().format(formatter) : "N/A";
        JLabel lblKhoiChieu = taoLabel("Khởi chiếu: ", ngayKC);

        JLabel lblQuocGia = taoLabel("Quốc gia: ", phim.getQuocGia());

        pnlThongTin.add(lblTenPhim);
        pnlThongTin.add(lblTheLoai);
        pnlThongTin.add(Box.createRigidArea(new Dimension(0, 5))); // Khoảng cách
        pnlThongTin.add(lblDaoDien);
        pnlThongTin.add(lblDoTuoi);
        pnlThongTin.add(lblThoiLuong);
        pnlThongTin.add(lblKhoiChieu);
        pnlThongTin.add(lblQuocGia);

        return pnlThongTin;
    }
	
	private JLabel taoLabel(String string, String value) {
		JLabel lblTen = new JLabel(string + value);
		lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTen.setForeground(TEXT_COLOR);
        lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lblTen;
	}

	private void chonPhim(Phim phim, Component component) {
		// Tạo và hiển thị màn hình chọn suất chiếu
	    GiaoDienChonGhe frm = new GiaoDienChonGhe(phim, nhanVien);
	    frm.setVisible(true);
	    
	    // Đóng màn hình hiện tại
	    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(component);
	    currentFrame.dispose();
	}
	
	private void loadPhim() {
		Connection conn = null;
		try {
			conn = ConnectDB.getConnection();
			PhimDAO phimDAO = new PhimDAO(conn);
			this.listPhim = phimDAO.getAllPhim();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		var event = e.getSource();
		if(event.equals(btnTim)) {
			String tenPhim = txtTim.getText().trim();
			Connection conn;
			try {
				conn = ConnectDB.getConnection();
				PhimDAO phimDAO = new PhimDAO(conn);
				if(!tenPhim.isEmpty()){
					this.listPhim = phimDAO.search(tenPhim);
				} else {
					this.listPhim = phimDAO.getAllPhim();
				}
				
				// Xóa panel cũ
	            pnlDanhSachPhim.removeAll();

	            // Tạo lại card phim từ listPhim mới
	            int len = listPhim.size();
	            int columns = Math.max(len / 4, 1); // tránh chia 0
	            pnlDanhSachPhim.setLayout(new GridLayout(columns, 5, 10, 10));
	            for (Phim phim : listPhim) {
	                pnlDanhSachPhim.add(CardPhim(phim));
	            }

	            // Refresh panel
	            pnlDanhSachPhim.revalidate();
	            pnlDanhSachPhim.repaint();
	            
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}	
	
	
	
}
