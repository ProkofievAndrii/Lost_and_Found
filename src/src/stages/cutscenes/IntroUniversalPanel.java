package stages.cutscenes;

import assets.sprites.level2.Level2Frame;
import assets.sprites.level3.Level3Frame;
import stages.Master;
import stages.levels.Level1Frame;
import stages.menu.MenuPanel;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class IntroUniversalPanel extends JPanel {

    public IntroUniversalPanel(String src) {
        this.source = src;
        setupUI();
        setupAudioEffects();
        progressIndex = 0;
        viewSingularCutscene();
    }

    private void viewSingularCutscene(){
        SwingUtilities.invokeLater(this::showSlide);
        if(progressIndex == 0) { progressButton.setText("NEXT SLIDE"); }
        if(progressIndex == limit - 1) { progressButton.setText("FINISH"); }
        progressIndex++;
    }

    private void showSlide() {
        if(background != null) imagePanel.remove(background);
        ImageIcon backgroundImage = new ImageIcon(source + "/slide" + progressIndex + ".png"); // Путь к первому изображению
        background = new JLabel("", backgroundImage, JLabel.CENTER);
        background.setBounds(0, 0, 1175, 690);
        imagePanel.add(background);
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
        Dimension buttonPanelSize = new Dimension(1175, 690);
        imagePanel.setPreferredSize(buttonPanelSize);
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

        limit = 0;
        switch (source) {
            case "src/src/assets/sprites/intro_level1":
                playBackgroundMusic();
                stageIndex = 1;
                limit = 5;
                break;
            case "src/src/assets/sprites/outro_level1", "src/src/assets/sprites/outro_level2":
                playBackgroundMusic();
                limit = 6;
                break;
            case "src/src/assets/sprites/intro_level2":
                playBackgroundMusic();
                stageIndex = 2;
                limit = 6;
                break;
            case "src/src/assets/sprites/intro_level3":
                playBackgroundMusic();
                stageIndex = 3;
                limit = 5;
                break;
            case "src/src/assets/sprites/outro_level3":
                playBackgroundMusic();
                limit = 9;
                break;
            case "src/src/assets/sprites/secret":
                playBackgroundMusic();
                limit = 8;
                break;
        }
        int finalLimit = limit;
        progressButton.addActionListener(e -> {
            playButtonAudioEffect();
            if(progressIndex == finalLimit) {
                switch (stageIndex) {
                    case 1:
                        prepareLevel1Frame();
                        break;
                    case 2:
                        prepareLevel2Frame();
                        break;
                    case 3:
                        prepareLevel3Frame();
                        break;
                    default:
                        prepareMenuPanel();
                        break;
                }
            }

            else viewSingularCutscene();
        });
    }

    private void setupSkipButton() {
        skipButton = new JButton("SKIP ALL");
        applySimilarButtonStyle(skipButton);
        applySimilarButtonMouseAction(skipButton);
        skipButton.addActionListener(e -> {
            switch (stageIndex) {
                case 1:
                    prepareLevel1Frame();
                    break;
                case 2:
                    prepareLevel2Frame();
                    break;
                case 3:
                    prepareLevel3Frame();
                    break;
                default:
                    prepareMenuPanel();
                    break;
            }
            playButtonAudioEffect();
        });
    }

    private static void prepareLevel1Frame() {
        backgroundClip.close();
        Master.window.setVisible(false);
        Master.gameIsOn = true;
        Master.level1Frame = new Level1Frame();
        Master.level1Frame.setVisible(true);
    }
    private static void prepareLevel2Frame() {
        backgroundClip.close();
        Master.window.setVisible(false);
        Master.window.remove(Master.introUniversalPanel);
        Master.level2Frame = new Level2Frame();
        Master.level2Frame.setVisible(true);
    }
    private static void prepareLevel3Frame() {
        backgroundClip.close();
        Master.window.setVisible(false);
        Master.window.remove(Master.introUniversalPanel);
        Master.level3Frame = new Level3Frame();
        Master.level3Frame.setVisible(true);
    }

    public static void prepareMenuPanel() {
        backgroundClip.close();
        Master.introUniversalPanel.setVisible(false);
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
        setupButtonEffect();
    }

    private void playBackgroundMusic() {
        try {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/cutscene.wav")));
            setVolume(backgroundClip, (float) UserDefaultManager.getMusicVolume());
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

    public static void setVolume(Clip soundClip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    private Clip buttonClickSound;
    private static Clip backgroundClip;
    private JLabel background;
    private JPanel imagePanel;
    private JButton progressButton, skipButton;
    private final String source;
    private int limit, progressIndex = 0, stageIndex = 0;
}
