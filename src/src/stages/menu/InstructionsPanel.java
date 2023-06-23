package stages.menu;

import javax.swing.*;
import java.awt.*;

public class InstructionsPanel extends JPanel{

    public InstructionsPanel() {
        setupUI();
    }

    private void setupUI() {
        setupPanel();
        setupBackground();
        setupElements();
        setLayout(null);
        setVisible(false);
    }

    private void setupPanel() {
        setBounds(420, 200, 720, 450);
        setBorder(BorderFactory.createLineBorder(Color.white));
    }

    private void setupBackground() {
        setBackground(new Color(120,120,120,120));
    }

    private void setupElements() {
        setupTooltip1();
        setupTooltip2();
        setupTooltip3();
    }

    private void setupTooltip1() {
        createImage1();
        createText1();
    }

    private void createImage1() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/instructions/wasd.png");
        JLabel wasdImage = new JLabel("", image, JLabel.CENTER);
        wasdImage.setBounds(25, 25, 150, 90);
        add(wasdImage);
    }

    private void createText1() {
        JLabel textLabel = new JLabel("<html>На першому рівні ви керуєте відерцем, використовуючи<br>" +
                "комбінацію клавіш A S D. Ваша задача - зібрати<br>" +
                "краплі води, уникаючи палки та листя, що віднімають життя.<br>" +
                "Втративши всі три життя ви програєте." +
                "</html>");
        textLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        textLabel.setBounds(190, 8, 700, 100);
        textLabel.setForeground(Color.WHITE);
        textLabel.setBackground(Color.BLACK);
        add(textLabel);
    }

    private void setupTooltip2() {
        createImage2();
        createText2();
    }

    private void createImage2() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/instructions/glasses.png");
        JLabel glassesImage = new JLabel("", image, JLabel.CENTER);
        glassesImage.setBounds(25, 175, 150, 99);
        add(glassesImage);
    }

    private void createText2() {
        JLabel textLabel = new JLabel("<html>На другому рівні ви збиратимете вітраж зі шматків,<br>" +
                "тасуючи їх місцями. Існує можливість 3 рази використати<br>" +
                "підказку, що вкаже куди треба вставити конкретну частину.<br>" +
                "Рівень вважається пройденим коли вітраж зібрано." +
                "</html>");
        textLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        textLabel.setBounds(190, 170, 700, 100);
        textLabel.setForeground(Color.WHITE);
        textLabel.setBackground(Color.BLACK);
        add(textLabel);
    }

    private void setupTooltip3() {
        createImage3();
        createText3();
    }

    private void createImage3() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/instructions/rebus.png");
        JLabel rebusImage = new JLabel("", image, JLabel.CENTER);
        rebusImage.setBounds(25, 350, 150, 40);
        add(rebusImage);
    }

    private void createText3() {
        JLabel textLabel = new JLabel("<html>На третьому рівні ви відгадуватимете ребуси.<br>" +
                "Відповіді зашифровані у відповідних зображеннях.<br>" +
                "Відповіді вводяться в поле для вводу, якщо відповідь<br>" +
                "правильна - просуваєтесь далі і до кінця, якщо ні - <br>" +
                "втрачаєте життя. Втративши всі три життя ви програєте." +
                "</html>");
        textLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        textLabel.setBounds(190, 330, 700, 100);
        textLabel.setForeground(Color.WHITE);
        textLabel.setBackground(Color.BLACK);
        add(textLabel);
    }
}
