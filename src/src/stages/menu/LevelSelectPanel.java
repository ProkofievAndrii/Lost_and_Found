package stages.menu;

import stages.Master;
import stages.utils.UserDefaultManager;
import stages.cutscenes.IntroUniversalPanel;
import stages.levels.Level1Frame;
import assets.sprites.level2.Level2Frame;
import assets.sprites.level3.Level3Frame;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class LevelSelectPanel extends JPanel {

    public LevelSelectPanel() {
        setupUI();
    }

    private void setupUI() {
        setupPanel();
        setupBackground();
        setupElements();
        setupButtonEffect();
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
        if(Master.level1Frame != null) {
            Master.level1Frame.dispose();
            Master.level1Frame = null;
        } else if(Master.level2Frame != null) {
            Master.level2Frame.dispose();
            Master.level2Frame = null;
        } else if(Master.level3Frame != null) {
            Master.level3Frame.dispose();
            Master.level3Frame = null;
        }
        setupTitleLabel();
        setupLevels();
        setupActionListeners();
    }

    private void setupActionListeners() {
        level1Button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                playButtonAudioEffect();
                Master.menuBackgroundClip.stop();

                Master.levelSelectPanelIsOn = false;
                Master.menuPanel.setVisible(false);

                if(UserDefaultManager.getProgress() < 2) {
                    Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/intro_level1"));
                } else {
                    Master.gameIsOn = true;
                    Master.level1Frame = new Level1Frame();
                    Master.level1Frame.setVisible(true);
                }
                SwingUtilities.invokeLater(() -> Master.window.remove(Master.menuPanel));
            }
        });
        level2Button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(level2Button.isEnabled()) {
                    playButtonAudioEffect();
                    Master.menuBackgroundClip.stop();

                    Master.levelSelectPanelIsOn = false;
                    Master.menuPanel.setVisible(false);

                    if(UserDefaultManager.getProgress() < 3) {
                        Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/intro_level2"));
                    } else {
                        Master.window.setVisible(false);
                        Master.level2Frame = new Level2Frame();
                        Master.level2Frame.setVisible(true);
                    }
                    buttonClickSound.stop();
                    buttonClickSound.close();
                    SwingUtilities.invokeLater(() -> Master.window.remove(Master.menuPanel));
                }
            }
        });
        level3Button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(level3Button.isEnabled()) {
                    playButtonAudioEffect();
                    Master.menuBackgroundClip.stop();

                    Master.levelSelectPanelIsOn = false;
                    Master.menuPanel.setVisible(false);

                    if(UserDefaultManager.getProgress() < 4) {
                        Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/intro_level3"));
                    } else {
                        Master.window.setVisible(false);
                        Master.level3Frame = new Level3Frame();
                        Master.level3Frame.setVisible(true);
                    }
                    buttonClickSound.stop();
                    buttonClickSound.close();
                    SwingUtilities.invokeLater(() -> Master.window.remove(Master.menuPanel));
                }

            }
        });
    }

    private void setupTitleLabel() {
        JLabel selectLabel = new JLabel("Select level");
        selectLabel.setFont(new Font("Courier", Font.PLAIN, 18));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setBounds(300, 5, 150, 70);
        add(selectLabel);
    }

    private void setupLevels() {
        setupFirstPair();
        setupSecondPair();
        setupThirdPair();
        setupSecret();
    }

    private void setupFirstPair() {
        level1Button = new JButton(new ImageIcon("src/src/assets/sprites/menu/level_select1.png"));
        level1Button.setBounds(50, 65, 200, 300);
        add(level1Button);

        JLabel levelInfoLabel = new JLabel("Level 1");
        levelInfoLabel.setFont(new Font("Courier", Font.PLAIN, 14));
        levelInfoLabel.setForeground(Color.WHITE);
        levelInfoLabel.setBounds(120, 360, 100, 70);
        add(levelInfoLabel);
    }

    private void setupSecondPair() {
        level2Button = new JButton(new ImageIcon("src/src/assets/sprites/menu/level_select2.png"));
        level2Button.setBounds(265, 65, 200, 300);
        level2Button.setEnabled(false);
        if(UserDefaultManager.getProgress() > 1) level2Button.setEnabled(true);
        add(level2Button);

        JLabel levelInfoLabel = new JLabel("Level 2");
        levelInfoLabel.setFont(new Font("Courier", Font.PLAIN, 14));
        levelInfoLabel.setForeground(Color.WHITE);
        levelInfoLabel.setBounds(335, 360, 100, 70);
        add(levelInfoLabel);
    }

    private void setupThirdPair() {
        level3Button = new JButton(new ImageIcon("src/src/assets/sprites/menu/level_select3.png"));
        level3Button.setBounds(480, 65, 200, 300);
        level3Button.setEnabled(false);
        if(UserDefaultManager.getProgress() > 2) level3Button.setEnabled(true);
        add(level3Button);

        JLabel levelInfoLabel = new JLabel("Level 3");
        levelInfoLabel.setFont(new Font("Courier", Font.PLAIN, 14));
        levelInfoLabel.setForeground(Color.WHITE);
        levelInfoLabel.setBounds(550, 360, 100, 70);
        add(levelInfoLabel);
    }

    private void setupSecret() {
        secretButton = new JButton(new ImageIcon("src/src/assets/sprites/secret/secreticon.png"));
        secretButton.setBounds(670, 380, 41, 60);
        secretButton.setOpaque(false);
        secretButton.setContentAreaFilled(false);
        secretButton.setBorderPainted(false);
        secretButton.setVisible(false);
        secretButton.setEnabled(false);
        if(UserDefaultManager.getProgress() == 4) {
            secretButton.setVisible(true);
            secretButton.setEnabled(true);
        }

        secretButton.addActionListener(actionEvent -> {
            playButtonAudioEffect();
            Master.menuBackgroundClip.stop();


            Master.levelSelectPanelIsOn = false;
            Master.menuPanel.setVisible(false);
            Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/secret"));

            SwingUtilities.invokeLater(() -> Master.window.remove(Master.menuPanel));
        });
        add(secretButton);
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

    JButton level1Button, level2Button, level3Button, secretButton;
    static public Clip buttonClickSound;
}
