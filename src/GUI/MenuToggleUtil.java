package GUI; // (Hoặc package của bạn)

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Lớp tiện ích để thêm chức năng Ẩn/Hiện Menu (bằng phím M)
 * cho bất kỳ JFrame nào.
 */
public class MenuToggleUtil {

    /**
     * Tên hành động (để tránh trùng lặp)
     */
    private static final String TOGGLE_ACTION_NAME = "toggleMenuAction";

    /**
     * Thêm phím tắt (M) vào một JFrame để bật/tắt JMenuBar.
     * @param frame JFrame cần thêm phím tắt.
     * @param menuBar Đối tượng JMenuBar để bật/tắt.
     */
    public static void addToggleSupport(JFrame frame, JMenuBar menuBar) {
        
        // 1. Lấy rootPane để gán Key Binding
        // Dùng rootPane tốt hơn contentPane
        JComponent rootPane = frame.getRootPane();

        // 2. Lấy InputMap và ActionMap
        // WHEN_IN_FOCUSED_WINDOW: Chỉ hoạt động khi cửa sổ đang được focus
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // 3. Tạo phím tắt (KeyStroke) cho phím "M"
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, 0);

        // 4. Liên kết phím "M" với tên hành động
        inputMap.put(keyStroke, TOGGLE_ACTION_NAME);

        // 5. Tạo hành động (Action)
        AbstractAction toggleAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra trạng thái HIỆN TẠI của menu
                if (frame.getJMenuBar() != null) {
                    // Nếu đang hiện -> Ẩn đi
                    frame.setJMenuBar(null);
                } else {
                    // Nếu đang ẩn -> Hiện lên
                    frame.setJMenuBar(menuBar);
                }
                
                // Yêu cầu Frame vẽ lại giao diện
                frame.revalidate();
                frame.repaint();
            }
        };
        frame.setJMenuBar(null);
        

        // 6. Liên kết tên hành động với đối tượng hành động
        actionMap.put(TOGGLE_ACTION_NAME, toggleAction);
    }
}