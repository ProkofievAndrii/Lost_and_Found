package stages;

import stages.cutscenes.IntroFrame;
import stages.menu.MenuPanel;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

public class Master {

    public static void main(String[] args) {
        setupWindow();
        prepareIntroFrame();
    }

    private static void setupWindow() {
        window = new Window("Lost And Found");
        window.setSize(windowWidth, windowHeight);
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void prepareIntroFrame() {
        Master.intro = new IntroFrame();
        Master.intro.setVisible(true);
    }

    public static void prepareMenuPanel() {
        Master.menuPanel = new MenuPanel();
        window.add(Master.menuPanel);
        window.setVisible(true);
    }

    public static void setVolume(Clip soundClip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }


    public static JFrame intro, window, level1Frame, level2Frame, level3Frame;
    public static JPanel introGamePanel, introUniversalPanel, storyModePanel, menuPanel, settingsPanel, instructionPanel, permissionPanel, levelSelectPanel;
    public static Clip menuBackgroundClip, menuButtonClickSound;
    public static JLabel menuBackgroundImage;

    public static int windowWidth = 1200;
    public static int windowHeight = 800;

    public static boolean permissionPanelIsOn = false;
    public static boolean levelSelectPanelIsOn = false;
    public static boolean instructionPanelIsOn = false;
    public static boolean gameIsOn = false;
}