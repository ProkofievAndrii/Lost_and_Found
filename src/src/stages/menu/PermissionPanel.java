package stages.menu;

import stages.cutscenes.IntroGamePanel;
import stages.Master;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PermissionPanel extends JPanel {

    public PermissionPanel() {
        setupUI();
        setupAudioEffects();
        setVisible(false);
    }

    private void setupUI() {
        setupPanel();
        setupElements();
    }

    private void setupPanel() {
        setBackground(new Color(120, 120, 120, 120));
        setBounds(400, 300, 400, 200);
        setLayout(null);
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
        setupQuestionLabel();
        setupContinueButton();
        setupReturnButton();
    }

    private void setupQuestionLabel() {
        JLabel questionLabel = new JLabel("Are you sure?");
        questionLabel.setFont(new Font("Courier", Font.PLAIN, 16));
        questionLabel.setBounds(140, 10, 150, 100);
        questionLabel.setForeground(Color.WHITE);
        add(questionLabel);
    }

    private void setupContinueButton() {
        continueButton = new JButton("Continue");
        continueButton.setBounds(50, 120, 125, 50);
        continueButton.addActionListener(e -> {
            UserDefaultManager.setProgress(0);
            playButtonAudioEffect();
            Master.menuBackgroundClip.stop();
            Master.permissionPanelIsOn = false;

            Master.menuPanel.setVisible(false);
            Master.window.add(Master.introGamePanel = new IntroGamePanel());
            SwingUtilities.invokeLater(() -> {
                Master.window.remove(Master.menuPanel);
            });
        });
        add(continueButton);
    }

    private void setupReturnButton() {
        returnButton = new JButton("Return");
        returnButton.setBounds(225, 120, 125, 50);
        returnButton.addActionListener(e -> {
            playButtonAudioEffect();
            Master.permissionPanel.setVisible(false);
            Master.permissionPanelIsOn = false;
        });
        add(returnButton);
    }

    private void setupAudioEffects() {
        setupButtonEffect("src/src/assets/audio/ui-click.wav");
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

    private Clip buttonClickSound;
    JButton continueButton, returnButton;
    private int buttonWidth = 150, buttonHeight = 50;
}
