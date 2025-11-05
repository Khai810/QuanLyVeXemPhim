package GUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import DAO.PhimDAO;
import Entity.Phim;


public class GiaoDienChonPhim extends JFrame implements MouseListener{
	PhimDAO phimDAO = new PhimDAO();
	JLabel lblPhimDangChieu;
	JPanel pnlDanhSachPhim;
	JScrollPane spDanhSachPhim;
	ArrayList<Phim> listPhim;
    
	private static final Dimension CARD_SIZE = new Dimension(240, 460);
	
	public GiaoDienChonPhim() {
		super();
		
		listPhim = phimDAO.getAllPhim();
		int len = listPhim.size();
		int columns = len / 4;

		pnlDanhSachPhim = new JPanel(new GridLayout(columns, 5, 10, 10));		
        pnlDanhSachPhim.setBackground(Color.WHITE);
        
        // Xu ly add Card
        for(Phim phim : listPhim) {
        	pnlDanhSachPhim.add(CardPhim(phim));
        }
        
        // 3. Đặt pnlDanhSachPhim vào một JScrollPane
        // Điều này rất quan trọng để có thể cuộn (scroll) khi có nhiều phim
        spDanhSachPhim = new JScrollPane(pnlDanhSachPhim);
        spDanhSachPhim.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spDanhSachPhim.getVerticalScrollBar().setUnitIncrement(8); // Tăng tốc độ cuộn

        // Thêm tiêu đề "Phim Đang Chiếu"
        lblPhimDangChieu = new JLabel("Phim Đang Chiếu");
        lblPhimDangChieu.setFont(new Font("Arial", Font.BOLD, 24));
        lblPhimDangChieu.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        // Bố cục chính của JFrame
        setLayout(new BorderLayout());
        add(lblPhimDangChieu, BorderLayout.NORTH);
        add(spDanhSachPhim, BorderLayout.CENTER);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1400, 800);
		setLocationRelativeTo(null); // Căn giữa màn hình
	}

	public JPanel CardPhim(Phim phim) {
		JPanel cardPhim = new JPanel();
		cardPhim.setLayout(new BorderLayout());
		cardPhim.setPreferredSize(CARD_SIZE);
		cardPhim.setMaximumSize(CARD_SIZE);
		cardPhim.setMinimumSize(CARD_SIZE);
		cardPhim.setBackground(Color.WHITE);
		
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
        pnlThongTin.setBackground(Color.WHITE);
        pnlThongTin.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn lề trái

        // Tên Phim
        JLabel lblTenPhim = new JLabel(phim.getTenPhim());
        lblTenPhim.setFont(new Font("Arial", Font.BOLD, 16));
        lblTenPhim.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDaoDien = new JLabel("Đạo diễn: " + phim.getDaoDien());
        lblDaoDien.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDaoDien.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Thể loại (Giả sử getTheLoai() trả về đối tượng TheLoai
        // và TheLoai có getTenTheLoai())
        String tenTheLoai = (phim.getTheLoai() != null) ? phim.getTheLoai().getTenTheLoai() : "Chưa xác định";
        JLabel lblTheLoai = new JLabel("Thể loại: " + tenTheLoai);
        lblTheLoai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTheLoai.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDoTuoi = new JLabel("Độ tuổi: " + phim.getDoTuoi());
        lblDoTuoi.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDoTuoi.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Thời lượng (Giả sử getThoiLuong() trả về số phút (int))
        JLabel lblThoiLuong = new JLabel("Thời lượng: " + phim.getThoiLuong() + " phút");
        lblThoiLuong.setFont(new Font("Arial", Font.PLAIN, 12));
        lblThoiLuong.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Khởi chiếu (Giả sử getNgayKhoiChieu() trả về LocalDate)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String ngayKC = (phim.getNgayKhoiChieu() != null) ? phim.getNgayKhoiChieu().format(formatter) : "N/A";
        JLabel lblKhoiChieu = new JLabel("Khởi chiếu: " + ngayKC);
        lblKhoiChieu.setFont(new Font("Arial", Font.PLAIN, 12));
        lblKhoiChieu.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblQuocGia = new JLabel("Quốc gia: " + phim.getQuocGia());
        lblQuocGia.setFont(new Font("Arial", Font.PLAIN, 12));
        lblQuocGia.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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

	private void chonPhim(Phim phim, Component component) {
		System.out.print(phim.getTenPhim());
		// Tạo và hiển thị màn hình chọn suất chiếu
	    GiaoDienChonGhe frm = new GiaoDienChonGhe(phim);
	    frm.setVisible(true);
	    
	    // Đóng màn hình hiện tại
	    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(component);
	    currentFrame.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
