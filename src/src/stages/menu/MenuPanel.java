package stages.menu;

import stages.Master;
import stages.cutscenes.StoryModePanel;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MenuPanel extends JPanel {

    public MenuPanel() {
        setupUI();
    }

    private void setupUI() {
        setupPanel();
        setupBackground();
        setupElements();
        setupAudioEffects();
        setVisible(true);
    }

    private void setupPanel() {
        setBounds(0, 0, Master.windowWidth, Master.windowHeight);

    }

    private void setupBackground() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/menu/menu_background.jpg");
        Master.menuBackgroundImage = new JLabel("", image, JLabel.CENTER);
        Master.menuBackgroundImage.setBounds(0, 50, Master.windowWidth, Master.windowHeight);
        add(Master.menuBackgroundImage);
    }

    private void setupElements() {
        if(Master.introUniversalPanel != null) {
            Master.menuPanel.remove(Master.introUniversalPanel);
            Master.introUniversalPanel = null;
        }
        setupMenuLabel();
        setupCreateGameButton();
        setupSelectLevelButton();
        setupStoryModeButton();
        setupInstructionButton();
        setupSettingsButton();
        setupExitButton();
        setupCopyrightLabel();
    }

    private void setupMenuLabel() {
        JLabel menuLabel = new JLabel("<html>Welcome to<br>'Lost And Found'!<html>");
        menuLabel.setForeground(Color.MAGENTA);
        menuLabel.setFont(new Font("Courier", Font.ITALIC, 25));
        menuLabel.setBounds(870, 50, 300, 100);
        Master.menuBackgroundImage.add(menuLabel);
    }

    private void applySimilarButtonCustomization(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Courier", Font.BOLD, 27));
        button.setVisible(true);
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

    public void setupGeneralButtonStatus(boolean enabledStatus, JButton button) {
        checkButton(startGameButton, button, enabledStatus);
        if(UserDefaultManager.getProgress() >= 1) checkButton(selectLevelButton, button, enabledStatus);
        if(UserDefaultManager.getProgress() >= 1) checkButton(storymodeButton, button, enabledStatus);
        checkButton(instructionButton, button, enabledStatus);
        checkButton(settingsButton, button, enabledStatus);
    }

    private void checkButton(JButton menuButton, JButton selectedButton, boolean enabledStatus) {
        if(menuButton != selectedButton) {
            menuButton.setEnabled(enabledStatus);
            setGeneralForeground(menuButton, enabledStatus);
        }
    }

    private void setGeneralForeground(JButton button, boolean enabledStatus) {
        if(enabledStatus) button.setForeground(Color.WHITE);
        else button.setForeground(Color.DARK_GRAY);
    }

    private void setupCreateGameButton() {
        startGameButton = new JButton("Create game");
        startGameButton.setBounds(buttonsXOffset, 210, buttonsWidth, buttonsHeight);
        startGameButton.setEnabled(true);
        applySimilarButtonCustomization(startGameButton);
        applySimilarButtonMouseAction(startGameButton);
        Master.menuBackgroundImage.add(startGameButton);
        setupPermissionPanel();

        startGameButton.addActionListener(e -> {
            if(!Master.permissionPanelIsOn) {
                Master.permissionPanel.setVisible(true);
                Master.permissionPanelIsOn = true;
            } else {
                Master.permissionPanel.setVisible(false);
                Master.permissionPanelIsOn = false;
            }
            playButtonAudioEffect();
        });
    }

    private void setupSelectLevelButton() {
        selectLevelButton = new JButton("Select level");
        selectLevelButton.setBounds(buttonsXOffset, 280, buttonsWidth, buttonsHeight);
        applySimilarButtonCustomization(selectLevelButton);
        applySimilarButtonMouseAction(selectLevelButton);
        Master.menuBackgroundImage.add(selectLevelButton);

        if(UserDefaultManager.getProgress() == 0) {
            selectLevelButton.setEnabled(false);
            selectLevelButton.setForeground(Color.gray);
        }

        selectLevelButton.addActionListener(e -> {
            playButtonAudioEffect();
            if(Master.levelSelectPanelIsOn) {
                setupGeneralButtonStatus(true, selectLevelButton);
                Master.menuBackgroundImage.remove(Master.levelSelectPanel);
                Master.levelSelectPanel = null;
                Master.levelSelectPanelIsOn = false;
                Master.menuBackgroundImage.repaint();
            } else {
                setupGeneralButtonStatus(false, selectLevelButton);
                setupLevelSelectPanel();
                Master.levelSelectPanel.setVisible(true);
                Master.levelSelectPanelIsOn = true;
            }
        });
    }

    private void setupStoryModeButton() {
        storymodeButton = new JButton("Story mode");
        storymodeButton.setBounds(buttonsXOffset, 350, buttonsWidth, buttonsHeight);
        applySimilarButtonCustomization(storymodeButton);
        applySimilarButtonMouseAction(storymodeButton);
        Master.menuBackgroundImage.add(storymodeButton);

        if(UserDefaultManager.getProgress() == 0) {
            storymodeButton.setEnabled(false);
            storymodeButton.setForeground(Color.gray);
        } else {
            setEnabled(true);
        }

        storymodeButton.addActionListener(e -> {
            playButtonAudioEffect();
            Master.menuBackgroundClip.stop();

            Master.menuPanel.setVisible(false);
            Master.storyModePanel = new StoryModePanel();
            Master.window.add(Master.storyModePanel);
            Master.storyModePanel.setVisible(true);
        });
    }

    private void setupInstructionButton() {
        instructionButton = new JButton("Instructions");
        instructionButton.setBounds(buttonsXOffset, 420, buttonsWidth, buttonsHeight);
        applySimilarButtonCustomization(instructionButton);
        applySimilarButtonMouseAction(instructionButton);
        Master.menuBackgroundImage.add(instructionButton);

        instructionButton.addActionListener(e -> {
            playButtonAudioEffect();

            if(Master.instructionPanelIsOn) {
                setupGeneralButtonStatus(true, instructionButton);
                Master.menuBackgroundImage.remove(Master.instructionPanel);
                Master.instructionPanel = null;
                Master.instructionPanelIsOn = false;
                Master.menuBackgroundImage.repaint();
            } else {
                setupGeneralButtonStatus(false, instructionButton);
                setupInstructionPanel();
                Master.instructionPanel.setVisible(true);
                Master.instructionPanelIsOn = true;
            }
        });
    }

    private void setupSettingsButton() {
        settingsButton = new JButton("Settings");
        settingsButton.setBounds(buttonsXOffset, 490, buttonsWidth, buttonsHeight);
        applySimilarButtonCustomization(settingsButton);
        applySimilarButtonMouseAction(settingsButton);
        Master.menuBackgroundImage.add(settingsButton);

        settingsButton.addActionListener(e -> {
            playButtonAudioEffect();
            Master.settingsPanel = new SettingsPanel();
            Master.window.add(Master.settingsPanel);
            Master.settingsPanel.setVisible(true);
            Master.menuPanel.setVisible(false);
        });
    }

    private void setupExitButton() {
        exitButton = new JButton("Exit");
        exitButton.setBounds(buttonsXOffset, 560, buttonsWidth, buttonsHeight);
        applySimilarButtonCustomization(exitButton);
        applySimilarButtonMouseAction(exitButton);
        Master.menuBackgroundImage.add(exitButton);
        exitButton.addActionListener(e -> {
            playButtonAudioEffect();
            Master.menuBackgroundClip.stop();
            Master.window.dispose();
        });
    }

    private void setupLevelSelectPanel() {
        if(Master.levelSelectPanel != null) {
            Master.menuBackgroundImage.remove(Master.levelSelectPanel);
            Master.levelSelectPanel = null;
            Master.levelSelectPanelIsOn = false;
        }
        Master.levelSelectPanel = new LevelSelectPanel();
        Master.menuBackgroundImage.add(Master.levelSelectPanel);
        Master.levelSelectPanelIsOn = true;
    }

    private void setupInstructionPanel() {
        if(Master.instructionPanel != null) {
            Master.menuBackgroundImage.remove(Master.instructionPanel);
            Master.instructionPanel = null;
            Master.instructionPanelIsOn = false;
        }
        Master.instructionPanel = new InstructionsPanel();
        Master.menuBackgroundImage.add(Master.instructionPanel);
        Master.instructionPanelIsOn = true;
    }

    private void setupPermissionPanel() {
        Master.permissionPanel = new PermissionPanel();
        Master.menuBackgroundImage.add(Master.permissionPanel);
    }

    private void setupCopyrightLabel() {
        JLabel copyrightLabel = new JLabel("<html>Copyright: LostAndFound©, inc.<br>All right are reserved</html>");
        copyrightLabel.setFont(new Font("Courier", Font.PLAIN, 12));
        copyrightLabel.setBounds(500, 720, 300, 50);
        copyrightLabel.setForeground(Color.lightGray);
        copyrightLabel.setBackground(Color.BLACK);
        Master.menuBackgroundImage.add(copyrightLabel);
    }

    private void setupAudioEffects() {
        playBackgroundMusic();
        setupButtonEffect();
    }

    private void playBackgroundMusic() {
        try {
            Master.menuBackgroundClip = AudioSystem.getClip();
            Master.menuBackgroundClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/reflected-light.wav")));
            Master.setVolume(Master.menuBackgroundClip, (float) UserDefaultManager.getMusicVolume());
            Master.menuBackgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            Master.menuBackgroundClip.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void setupButtonEffect() {
        try {
            Master.menuButtonClickSound = AudioSystem.getClip();
            Master.menuButtonClickSound.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/ui-click.wav")));
            Master.setVolume(Master.menuButtonClickSound, (float) UserDefaultManager.getAudioEffectsVolume());
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void playButtonAudioEffect() {
        Master.menuButtonClickSound.setFramePosition(0);
        Master.menuButtonClickSound.start();
    }

    static public JButton startGameButton, selectLevelButton, instructionButton, storymodeButton, settingsButton, exitButton;

    private final int buttonsXOffset = 100;
    private final int buttonsWidth = 260;
    private final int buttonsHeight = 70;
}
