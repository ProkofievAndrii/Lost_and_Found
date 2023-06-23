package stages;

import javax.swing.*;

public class Window extends JFrame {

    public Window(String title) {
        setupUI(title);
    }

    private void setupUI(String title) {
        setupFrame(title);
    }

    private void setupFrame(String title) {
        setTitle(title);
    }
}
