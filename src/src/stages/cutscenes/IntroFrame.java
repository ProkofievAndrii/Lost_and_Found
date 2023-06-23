package stages.cutscenes;

import stages.Master;
import stages.menu.MenuPanel;
import stages.Window;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static stages.Master.windowHeight;
import static stages.Master.windowWidth;


public class IntroFrame extends JFrame {

    Timer timer = new Timer(25, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(gifTimeout == 10000) {
                gifTimeout = 0;
                gifTimerStarted = false;
                backgroundAudio.stop();
                Master.intro.setVisible(false);
                setupWindow();
                prepareMenuPanel();
                timer.stop();
            } else if(gifTimerStarted) gifTimeout += 25;
        }
    });

//    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
//        SwingUtilities.invokeAndWait(() -> {
//            Master.intro = new IntroFrame();
//        });
//    }

    public IntroFrame() {
        setupUI();
        setupElements();
    }

    private void setupUI() {
        setupFrame();
    }

    private void setupFrame() {
        setBounds(0,0,1200,800);
        setVisible(true);
    }

    private static void setupWindow() {
        Master.window = new Window("Lost And Found");
        Master.window.setSize(windowWidth, windowHeight);
        Master.window.setResizable(false);
        Master.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void prepareMenuPanel() {
        Master.menuPanel = new MenuPanel();
        Master.window.add(Master.menuPanel);
        Master.window.setVisible(true);
    }

    private void setupElements() {
        setupScriptLabel();
    }

    private void setupScriptLabel() {
        ImageIcon gif = new ImageIcon("src/src/assets/sprites/intro_cutscene/intro.gif");
        backgroundGif = new JLabel("", gif, JLabel.CENTER);
        backgroundGif.setBounds(0, 0, 1200, 800);
        gifTimerStarted = true;
        add(backgroundGif);
        timer.start();
        setupAudioEffects();
    }

    private void setupAudioEffects() {
        playBackgroundMusic("src/src/assets/audio/intro-audio.wav");
    }

    private void playBackgroundMusic(String soundPath) {
        try {
            backgroundAudio = AudioSystem.getClip();
            backgroundAudio.open(AudioSystem.getAudioInputStream(new File(soundPath)));
            setVolume(backgroundAudio, 0.2f);
            backgroundAudio.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    public void setVolume(Clip soundClip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    private Clip backgroundAudio;
    private JLabel backgroundGif;
    private boolean gifTimerStarted = false;
    private int gifTimeout = 0;
}