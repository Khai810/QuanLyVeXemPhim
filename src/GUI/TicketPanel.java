package GUI;
import javax.swing.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import Entity.ChiTietHoaDon;
import Entity.Ve;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class TicketPanel extends JPanel {
//    private List<ChiTietHoaDon> listCTHD;
    private Ve ve;
	private BufferedImage qrCodeImage;
    public TicketPanel(Ve ve) {
        this.ve = ve;
        setPreferredSize(new Dimension(400, 350)); // kích thước vé
        try {
            this.qrCodeImage = generateQRCode("VE-" + ve.getMaVe(), 100, 100);
        } catch (WriterException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi tạo QR code: " + e.getMessage());
        }

    }
    
	private static final Color PRI_COLOR = new Color(252, 247, 223);
    private static final Color SEC_COLOR = new Color(253, 252, 241);
    private static final Color RED_COLOR = new Color(212, 54, 37);
    
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BTN_COLOR = Color.WHITE;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Khử răng cưa, làm mịn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền vé
        g2.setColor(SEC_COLOR);
        g2.fillRoundRect(10, 10, 380, 320, 30, 30);

        // Viền
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(10, 10, 380, 320, 30, 30);

        // Tiêu đề
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.setColor(RED_COLOR);
        g2.drawString("🎬 DK CINEMA", 120, 40);

        // Nội dung vé
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int x = 40, y = 70, line = 25;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        g2.drawString("Mã vé: " + ve.getMaVe(), x, y);
        
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0));
        g2.drawLine(30, 75, 370, 75);
        
        g2.drawString("Phim: " + ve.getSuatChieu().getPhim().getTenPhim(), x, y += line);
        g2.drawString("Ngày chiếu: " + ve.getNgayChieu().format(df), x, y += line);
        g2.drawString("Giờ chiếu: " + ve.getGioChieu().toString(), x, y += line);
        g2.drawString("Ghế: " + ve.getTenGhe(), x, y += line);
        g2.drawString("Rạp : " + ve.getTenPhongChieu(), x, y += line);
        
        g2.drawString("Giá vé: " + String.format("%,.0f VND", ve.getGiaVe()), x, y += line);

        // Đường gạch đứt (giữa vé)
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0));
        g2.drawLine(30, 200, 370, 200);

        // QR / Mã vạch giả lập
        if (qrCodeImage != null) {
            g2.drawImage(qrCodeImage, 270, 220, 100, 100, this);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(270, 245, 100, 100);
            g2.setColor(Color.BLACK);
            g2.drawString("QR Code", 290, 295);
        }
    }

    // Xuất panel ra ảnh PNG
    public void saveAsImage(String filePath) throws Exception {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();
        javax.imageio.ImageIO.write(image, "png", new java.io.File(filePath));
    }
    
    private BufferedImage generateQRCode(String text, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
