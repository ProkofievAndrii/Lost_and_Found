package stages.cutscenes;

import stages.Master;
import stages.menu.MenuPanel;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StoryModePanel extends JPanel {

    public StoryModePanel() {
        setupUI();
        setupAudioEffects();
        viewSingularCutscene();
    }

    private void viewSingularCutscene(){
        SwingUtilities.invokeLater(this::showSlide);
        switch (progressIndex) {
            case 0 -> {
                packageName = "intro_level1";
                progressButton.setText("NEXT SLIDE");
            }
            case 5 -> {
                packageName = "outro_level1";
                packageIndex = 0;
            }
            case 11 -> {
                packageName = "intro_level2";
                packageIndex = 0;
            }
            case 17 -> {
                packageName = "outro_level2";
                packageIndex = 0;
            }
            case 23 -> {
                packageName = "intro_level3";
                packageIndex = 0;
            }
            case 28 -> {
                packageName = "outro_level3";
                packageIndex = 0;
            }
            case 36 -> progressButton.setText("FINISH");
        }
        progressIndex++;
        packageIndex++;
    }
    private void showSlide() {
        if(background != null) imagePanel.remove(background);
        ImageIcon backgroundImage = new ImageIcon("src/src/assets/sprites/" + packageName + "/slide" + packageIndex + ".png"); // Путь к первому изображению
        background = new JLabel("", backgroundImage, JLabel.CENTER);
        background.setBounds(0, 0, 1175, 690);
        imagePanel.add(background);
        // Обновление отображения
        repaint();
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
        if(Master.intro != null) {
            Master.intro.dispose();
            Master.intro = null;
        }
        setupImagePanel();
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

    private void setupProgressButton() {
        progressButton = new JButton("START");
        applySimilarButtonStyle(progressButton);
        applySimilarButtonMouseAction(progressButton);
        progressButton.addActionListener(e -> {
            playButtonAudioEffect();
            if(progressIndex == 37) {
                playButtonAudioEffect();
                prepareMenuPanel();
                backgroundClip.stop();
            }
            else viewSingularCutscene();
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

    public static void prepareMenuPanel() {
        Master.storyModePanel.setVisible(false);
        Master.menuPanel = new MenuPanel();
        Master.window.add(Master.menuPanel);
        Master.window.setVisible(true);
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
        JPanel buttonPanel = new JPanel();
        Dimension buttonPanelSize = new Dimension(240, 40);
        buttonPanel.setPreferredSize(buttonPanelSize);
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setBackground(Color.darkGray);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 1;
        panelConstraints.insets = new Insets(20, 0, 0,0);
        buttonPanel.add(skipButton);
        buttonPanel.add(progressButton);
        add(buttonPanel, panelConstraints);
    }

    private void setupAudioEffects() {
        playBackgroundMusic();
        setupButtonEffect();
    }

    private void playBackgroundMusic() {
        try {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/cutscene.wav")));
            Master.setVolume(backgroundClip, (float) UserDefaultManager.getMusicVolume());
            backgroundClip.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void setupButtonEffect() {
        try {
            buttonClickSound = AudioSystem.getClip();
            buttonClickSound.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/ui-click.wav")));
            Master.setVolume(buttonClickSound, (float) UserDefaultManager.getAudioEffectsVolume());
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void playButtonAudioEffect() {
        buttonClickSound.setFramePosition(0);
        buttonClickSound.start();
    }

    private Clip backgroundClip, buttonClickSound;
    private JLabel background;
    private JPanel imagePanel;
    private JButton progressButton, skipButton;

    private int progressIndex = 0, packageIndex = 0;
    private String packageName;
}

