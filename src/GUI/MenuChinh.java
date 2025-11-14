package GUI; // Hoặc package của bạn

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import DAO.SuatChieuDAO;
import Entity.Phim;
import Entity.SuatChieu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

public class MenuChinh extends JMenuBar {
	JMenu menuVe, menuNhanVien, menuSuatChieu, menuPhongChieu, menuHoaDon, menuPhim;
	JMenuItem itemMuaVe, itemQuanLyVe, itemQuanLyNhanVien, itemQuanLySuatChieu
		, itemQuanLyHoaDon, itemQuanLyPhongChieu, itemQuanLyPhim;
	
    private JFrame parentFrame;

    public MenuChinh(JFrame parentFrame) {
        super();
        this.parentFrame = parentFrame; // Lưu frame cha lại
        
        // Menu vé
        menuVe = new JMenu("Vé");
        
        itemMuaVe = new JMenuItem("Mua vé");
        itemMuaVe.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              new GiaoDienChonPhim().setVisible(true);
              parentFrame.dispose(); 
          }
      });
        
        itemQuanLyVe = new JMenuItem("Quản lý vé");
        
        menuVe.add(itemMuaVe);
//        menuVe.addSeparator(); // Đường kẻ ngang
        menuVe.add(itemQuanLyVe);
        
        // Menu Nhân Viên
        menuNhanVien = new JMenu("Nhân Viên");
        itemQuanLyNhanVien = new JMenuItem("Quản lý nhân viên");
        menuNhanVien.add(itemQuanLyNhanVien);
        // Menu Suất Chiếu
        menuSuatChieu = new JMenu("Suất Chiếu");
        itemQuanLySuatChieu = new JMenuItem("Quản lý suất chiếu");
        itemQuanLySuatChieu.addActionListener(e -> {
            GiaoDienQuanLySuatChieu panel = new GiaoDienQuanLySuatChieu();
            parentFrame.setContentPane(panel);
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        menuSuatChieu.add(itemQuanLySuatChieu);
        
        // Menu Phòng Chiếu
        menuPhongChieu = new JMenu("Phòng Chiếu");
        itemQuanLyPhongChieu = new JMenuItem("Quản lý phòng chiếu");
        menuPhongChieu.add(itemQuanLyPhongChieu);
        
        // Menu Hóa đơn
        menuHoaDon = new JMenu("Hóa Đơn");
        itemQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
        itemQuanLyHoaDon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GiaoDienQuanLyHoaDon().setVisible(true);
                parentFrame.dispose(); 
            }
        });
        menuHoaDon.add(itemQuanLyHoaDon);
        
      //Menu Phim
        menuPhim = new JMenu("Phim");
        itemQuanLyPhim = new JMenuItem("Quản lý phim");
        menuPhim.add(itemQuanLyPhim);
        
        itemQuanLyPhim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            	GiaoDienQuanLyPhim panel = new GiaoDienQuanLyPhim();
                parentFrame.setContentPane(panel);
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        // Thêm hành động cho "Exit"
//        itemExit.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Đóng chính cửa sổ đã gọi nó
//                parentFrame.dispose(); 
//                
//                // Hoặc tắt toàn bộ ứng dụng
//                // System.exit(0);
//            }
//        });
//        
//        // --- 2. Tạo Menu "Help" ---
//        JMenu menuHelp = new JMenu("Help");
//        JMenuItem itemAbout = new JMenuItem("About");
//        
//        itemAbout.addActionListener(e -> {
//            JOptionPane.showMessageDialog(parentFrame, 
//                "Đây là ứng dụng quản lý rạp phim...", 
//                "About", 
//                JOptionPane.INFORMATION_MESSAGE);
//        });
//        
//        menuHelp.add(itemAbout);

        // --- 3. Thêm các JMenu vào JMenuBar (chính là 'this') ---
        this.add(menuVe);
        this.add(menuNhanVien);
        this.add(menuSuatChieu);
        this.add(menuPhongChieu);
        this.add(menuHoaDon);
        this.add(menuPhim);

    }
    
}