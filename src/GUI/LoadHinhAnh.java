package GUI;

import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.ImageIcon;

public class LoadHinhAnh {

	public ImageIcon taiHinhAnh(String path, int width, int height) {
	    try {
	        // SỬA: Dùng getResource() để tìm file trong classpath (src)
	        java.net.URL imgURL = getClass().getResource(path);

	        // SỬA: Kiểm tra xem có tìm thấy tài nguyên không
	        if (imgURL == null) {
	            // Ném lỗi nếu không tìm thấy file
	            throw new Exception("Không tìm thấy tài nguyên: " + path);
	        }

	        // Tải ảnh từ URL
	        ImageIcon img = new ImageIcon(imgURL);

	        // (Phần còn lại giữ nguyên)
	        if (img.getImageLoadStatus() != MediaTracker.COMPLETE) {
	             throw new Exception("Không tải được ảnh");
	        }

	        Image image = img.getImage();
	        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	        return new ImageIcon(scaledImage);

	    } catch (Exception e) {
	        System.err.println(e.getMessage()); // In lỗi cụ thể
	        // Trả về một ảnh placeholder khi lỗi
	        return null;
	    }
	}
	
}
