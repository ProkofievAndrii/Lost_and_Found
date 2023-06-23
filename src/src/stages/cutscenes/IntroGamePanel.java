package stages.cutscenes;

import stages.Master;
import stages.menu.MenuPanel;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class IntroGamePanel extends JPanel {

    public IntroGamePanel() {
        UserDefaultManager.setProgress(1);
        setupUI();
        setupAudioEffects();
        awaitForInput();
    }

    private void awaitForInput() {
        MouseListener mouseListener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                buttonPanel.setVisible(true);
                imagePanel.remove(tooltipLabel);
                viewSingularCutscene();
                removeMouseListener(this);
            }

            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        };
        addMouseListener(mouseListener);
    }

    private void viewSingularCutscene(){
        SwingUtilities.invokeLater(() -> {
            showSlide();
        });
        if(progressIndex == 0) { progressButton.setText("NEXT SLIDE"); }
        if(progressIndex == 9) { progressButton.setText("FINISH"); }
        showSlideScript();
        progressIndex++;
    }
    private void showSlide() {
        if(background != null) imagePanel.remove(background);
        backgroundImage = new ImageIcon("src/src/assets/sprites/intro_menu/slide"+ progressIndex +".png"); // Путь к первому изображению
        background = new JLabel("", backgroundImage, JLabel.CENTER);
        background.setBounds(0, 0, 1150, 675);
        imagePanel.add(background);
        // Обновление отображения
        repaint();
    }

    private void showSlideScript() {
        scriptLabel.setText("");
        startTextTransition(textArray[progressIndex]);
        progressButton.setEnabled(false);
    }

    private void startTextTransition(String text) {
        ActionListener actionListener = e -> {
            if (charIndex < text.length()) {
                scriptLabel.setText(scriptLabel.getText() + text.charAt(charIndex));
                charIndex++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
                charIndex = 0;
                progressButton.setEnabled(true); // Активируем кнопку после завершения анимации
            }
        };

        javax.swing.Timer timer = new javax.swing.Timer(65, actionListener); // Установите желаемую задержку в миллисекундах
        timer.start();
    }


    private void setupUI() {
        setupMainPanel();
        setupElements();
    }
    private void setupMainPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(51,51,51));
    }

    private void setupElements() {
        setupImagePanel();
        setupTooltipLabel();
        setupScriptLabel();
        setupProgressButton();
        setupSkipButton();
        setupButtonPanel();
    }

    private void setupImagePanel() {
        imagePanel = new JPanel(new BorderLayout());
        Dimension imagePanelSize = new Dimension(1175, 690);
        imagePanel.setPreferredSize(imagePanelSize);
        imagePanel.setBackground(new Color(51,51,51));

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.insets = new Insets(-15, 0, 0,0);
        add(imagePanel, panelConstraints);
    }

    private void setupTooltipLabel() {
        tooltipLabel = new JLabel("-- Click to start --");
        tooltipLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        tooltipLabel.setForeground(Color.WHITE);

        tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(tooltipLabel, BorderLayout.CENTER);
    }

    private void setupScriptLabel() {
        scriptLabel = new JLabel();
        scriptLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scriptLabel.setForeground(Color.WHITE);
        scriptLabel.setVerticalAlignment(JLabel.BOTTOM);
        scriptLabel.setHorizontalAlignment(JLabel.CENTER);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 1.0;
        labelConstraints.weighty = 1.0;
        labelConstraints.fill = GridBagConstraints.BOTH;
        labelConstraints.insets = new Insets(0, 0, 0, 0);
        add(scriptLabel, labelConstraints);
    }

    private void setupProgressButton() {
        progressButton = new JButton("START");
        applySimilarButtonStyle(progressButton);
        applySimilarButtonMouseAction(progressButton);
        progressButton.addActionListener(e -> {
            playButtonAudioEffect();
            if(progressIndex == 10) {
                playButtonAudioEffect();
                backgroundClip.stop();
                prepareMenuPanel();
            } else {
                viewSingularCutscene();
            }
        });
    }

    private void setupSkipButton() {
        skipButton = new JButton("SKIP ALL");
        applySimilarButtonStyle(skipButton);
        applySimilarButtonMouseAction(skipButton);
        skipButton.addActionListener(e -> {
            playButtonAudioEffect();
            backgroundClip.stop();
            prepareMenuPanel();
        });
    }

    private static void prepareMenuPanel() {
        SwingUtilities.invokeLater(() -> {
            Master.introGamePanel.setVisible(false);
            Master.window.add(Master.menuPanel = new MenuPanel());
        });
    }

    private void applySimilarButtonStyle(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
    }

    private void applySimilarButtonMouseAction(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setForeground(Color.WHITE);
            }
        });
    }

    private void setupButtonPanel() {
        buttonPanel = new JPanel();
        Dimension buttonPanelSize = new Dimension(240, 40);
        buttonPanel.setPreferredSize(buttonPanelSize);
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setBackground(Color.darkGray);
        buttonPanel.setVisible(false);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 1;
        buttonPanel.add(skipButton);
        buttonPanel.add(progressButton);
        add(buttonPanel, panelConstraints);
    }

    private void setupAudioEffects() {
        playBackgroundMusic("src/src/assets/audio/cutscene.wav");
        setupButtonEffect("src/src/assets/audio/ui-click.wav");
    }

    private void playBackgroundMusic(String soundPath) {
        try {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(AudioSystem.getAudioInputStream(new File(soundPath)));
            setVolume(backgroundClip, (float) UserDefaultManager.getMusicVolume());
            backgroundClip.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void setupButtonEffect(String soundPath) {
        try {
            buttonClickSound = AudioSystem.getClip();
            buttonClickSound.open(AudioSystem.getAudioInputStream(new File(soundPath)));
            setVolume(buttonClickSound, (float) UserDefaultManager.getAudioEffectsVolume());
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void playButtonAudioEffect() {
        buttonClickSound.setFramePosition(0);
        buttonClickSound.start();
    }

    public void setVolume(Clip soundClip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    private Clip backgroundClip, buttonClickSound;
    private JLabel scriptLabel, background, tooltipLabel;
    private JPanel buttonPanel, imagePanel;
    private JButton progressButton, skipButton;
    private ImageIcon backgroundImage;
    private String[] textArray = new String[]{
            "Сьома ранку. Хлопчик Тім, якому шість років, стурбований, стоїть перед входом у дитячий садок.",
            "Усі діти грають, але йому не цікаві їхні ігри.", "Однолітків, в свою чергу, Тім ой як не цікавить.",
            "Знову він залишиться сам на сам зі своїми думками та смутком.",
            "Засмучений ще одним невдалим днем у садочку, хлопчик повертається до своєї затишної домівки.",
            "На годиннику друга ночі, а він досі не спить. Думає про те, де він має знайти справжнього друга.",
            "Бідолахо вирішує піднятися на дах свого будинку, уявляючи, яким міг би бути друг з космосу.",
            "Зірки вже так близько... І хтось розуміючий, мабуть, десь там.",
            "Замріявшись на хвилинку, який друг на нього чекає ТАМ, Тім заплющує очі.",
            "Розплющивши очі, він опиняється на зовсім невідомій йому планеті..."
    };
    private int charIndex = 0, progressIndex = 0;
}
