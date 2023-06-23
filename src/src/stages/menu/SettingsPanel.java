package stages.menu;

import stages.Master;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setupUI();
        setupButtonEffect();
        setVisible(true);
    }

    private void setupUI() {
        setupPanel();
        setupElements();
    }

    private void setupPanel() {
        setBounds(0, 0, Master.windowWidth, Master.windowHeight);
        setupBackground();
    }

    private void setupBackground() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/menu/menu_background.jpg");
        menuBackgroundImage = new JLabel("", image, JLabel.CENTER);
        menuBackgroundImage.setBounds(0, 0, Master.windowWidth, Master.windowHeight);
        add(menuBackgroundImage);
    }

    private void setupElements() {
        setupNamingLabels();
        setupMusicSlider();
        setupAudioEffectsSlider();
        setupDifficultyComboBox();
        setupExitButton();
    }

    private void setupNamingLabels() {
        JLabel nameLabel1 = new JLabel("Background music volume");
        nameLabel1.setForeground(Color.YELLOW);
        nameLabel1.setFont(new Font("Courier", Font.ITALIC, 20));
        nameLabel1.setBounds(250, 200, 300, 50);
        menuBackgroundImage.add(nameLabel1);

        JLabel nameLabel2 = new JLabel("Sounds and effects volume");
        nameLabel2.setForeground(Color.YELLOW);
        nameLabel2.setFont(new Font("Courier", Font.ITALIC, 20));
        nameLabel2.setBounds(250, 325, 300, 50);
        menuBackgroundImage.add(nameLabel2);

        JLabel nameLabel3 = new JLabel("Game difficulty");
        nameLabel3.setForeground(Color.YELLOW);
        nameLabel3.setFont(new Font("Courier", Font.ITALIC, 20));
        nameLabel3.setBounds(250, 450, 300, 50);
        menuBackgroundImage.add(nameLabel3);
    }

    private void setupMusicSlider() {
        musicSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (UserDefaultManager.getMusicVolume() * 100));
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setMinorTickSpacing(5);
        musicSlider.setPaintTicks(true);
        musicSlider.setPaintLabels(true);
        musicSlider.setSnapToTicks(true);
        musicSlider.setBounds(250,250,700,50);
        musicSlider.setForeground(Color.WHITE);
        musicSlider.setFont(new Font("", Font.BOLD, 14));

        musicSlider.addChangeListener(changeEvent -> {
            float value = (float) musicSlider.getValue() / (float) 100;
            UserDefaultManager.setMusicVolume(value);
            Master.setVolume(Master.menuBackgroundClip, value);
        });
        menuBackgroundImage.add(musicSlider);
    }

    private void setupAudioEffectsSlider() {
        audioEffectsSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (UserDefaultManager.getAudioEffectsVolume() * 100));
        audioEffectsSlider.setMajorTickSpacing(25);
        audioEffectsSlider.setMinorTickSpacing(5);
        audioEffectsSlider.setPaintTicks(true);
        audioEffectsSlider.setPaintLabels(true);
        audioEffectsSlider.setSnapToTicks(true);
        audioEffectsSlider.setBounds(250,375,700,50);
        audioEffectsSlider.setForeground(Color.WHITE);
        audioEffectsSlider.setFont(new Font("", Font.BOLD, 14));

        audioEffectsSlider.addChangeListener(changeEvent -> {
            float value = (float) musicSlider.getValue() / (float) 100;
            UserDefaultManager.setAudioEffectsVolume(value);
            Master.setVolume(Master.menuButtonClickSound, value);
        });
        menuBackgroundImage.add(audioEffectsSlider);
    }

    private void setupDifficultyComboBox() {
        String[] items = {
                "Easy",
                "Medium",
                "Hard"
        };

        JComboBox difficultyComboBox = new JComboBox(items);
        difficultyComboBox.setSelectedIndex(UserDefaultManager.getDifficulty());
        difficultyComboBox.setBounds(450, 425, 100, 100);
        difficultyComboBox.setAlignmentX(LEFT_ALIGNMENT);
        difficultyComboBox.addActionListener(actionEvent -> {
            UserDefaultManager.setDifficulty(difficultyComboBox.getSelectedIndex());
        });
        menuBackgroundImage.add(difficultyComboBox);
    }

    private void setupExitButton() {
        exitButton = new JButton(new ImageIcon("src/src/assets/sprites/level1/exiticon.png"));
        exitButton.setBounds(1125, 25, 50, 50);
        exitButton.addActionListener(e -> {
            playButtonAudioEffect();
            Master.window.remove(Master.settingsPanel);
            Master.settingsPanel = null;

            Master.menuPanel.setVisible(true);
            buttonClickSound.stop();
            buttonClickSound.close();

        });
        menuBackgroundImage.add(exitButton);
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

    public void setVolume(Clip soundClip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    Clip buttonClickSound;
    JLabel menuBackgroundImage;
    JButton exitButton;
    JSlider musicSlider, audioEffectsSlider;
    JPanel centralPanel;
}
