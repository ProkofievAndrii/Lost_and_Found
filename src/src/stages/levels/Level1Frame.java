package stages.levels;

import stages.Master;
import stages.cutscenes.IntroUniversalPanel;
import stages.menu.MenuPanel;
import stages.utils.SplashObserver;
import stages.utils.UserDefaultManager;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Level1Frame extends JFrame implements KeyListener {

    public Level1Frame() {
        defineDifficulty();
        setupUI();
        setupAudioEffects();
        setVisible(true);
    }

    Timer timer = new Timer(25, new ActionListener() {
        private final long startTime = System.currentTimeMillis();
        int threadDropCounter = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateHealthStatusLabel();
            updateDropStatusLabel();
            checkForWin();
            manageTooltips();
            manageItemsFrequencyScale();
            manageItemsFrequency();
            manageElementsMovement();
            checkForCollisions();
            checkForSplashRemoval();
            threadDropCounter++;
            generalDelay += 25;
            stickDelay += 3;
            leafDelay += 3;
            if(splashTimerStarted) SplashObserver.setCounter(25);
        }

        private void checkForWin() {
            boolean condition1 = dropsCollected >= dropsNeeded;
            boolean condition2 = healthPoints <= 0;
            if(condition1) isAWin = true;
            else if(condition2) isAWin = false;
            if(condition1 || condition2) {
                timer.stop();
                splashTimerStarted = false;
                splashModel.setVisible(false);
                Master.gameIsOn = false;
                backgroundClip.stop();
                waterSplashClip.stop();
                showGameResult();
            }
        }

        private void manageTooltips() {
            if(elapsedTime >= 24000 && instructionLabel != null) {
                instructionLabel.setVisible(false);
                background.remove(instructionLabel);
            } else if(elapsedTime >= 16000 && instructionLabel != null) {
                instructionLabel.setBounds(450, 670, 500, 100);
                instructionLabel.setText("Collect 33 drops to win!");
            } else if(elapsedTime >= 8000 && instructionLabel != null) {
                instructionLabel.setBounds(455, 670, 500, 100);
                instructionLabel.setText("Avoid sticks and leafs!");
            }
        }

        private void manageItemsFrequencyScale() {
            if(generalDelay >= 12000) {
                stickFrequency -= 5;
                leafFrequency -= 5;
                generalDelay = 0;
            }
        }

        private void manageItemsFrequency() {
            if(threadDropCounter >= dropFrequency) {
                createDrop();
                threadDropCounter = 0;
            }
            if(elapsedTime >= 90000) {
                dropYVelocity = 9;
                bucketXVelocity = 6;
            } else if(elapsedTime >= 30000) {
                dropYVelocity = 8;
                bucketXVelocity = 7;
            }
            if(stickDelay >= stickFrequency) {
                createStick();
                stickDelay = 0;
            }
            if(leafDelay >= leafFrequency) {
                createLeaf();
                leafDelay = 0;
            }
        }

        private void checkForSplashRemoval() {
            if (SplashObserver.getCounter() >= 2200 && splashModel != null) {
                background.remove(splashModel);
                splashModel = null;
                splashObserver = null;
                splashTimerStarted = false;
            }
        }
    });


    private void setupUI() {
        SwingUtilities.invokeLater(() -> {
            if(Master.introUniversalPanel != null) {
                Master.window.remove(Master.introUniversalPanel);
                Master.introUniversalPanel = null;
            }
        });
        setupPanel();
        setupElements();
    }

    private void setupPanel() {
        setBounds(0, 0, Master.windowWidth, Master.windowHeight);
        setupBackground();
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        setLayout(null);
    }

    private void setupBackground() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/level1/level1_background.png");
        background = new JLabel("", image, JLabel.CENTER);
        background.setBounds(0, 0, Master.windowWidth, Master.windowHeight);
        add(background);
    }

    private void setupElements() {
        setupBucket();
        setupInstructionLabel();
        setupInterface();
    }

    private void setupInterface() {
        setupHealthInterface();
        setupDropInterface();
        setupSkipButton();
    }

    private void setupHealthInterface() {
        healthBar = new JPanel();
        healthBar.setBounds(40, 25, 120, 80);
        healthBar.setBackground(new Color(40,40,40,150));
        healthBar.setLayout(null);
        background.add(healthBar);
        setupHealthBar();
    }

    private void setupHealthBar() {
        setupHealthIcon();
        setupHealthStatusLabel();
    }

    private void setupHealthIcon() {
        ImageIcon image = new ImageIcon("src/src/assets/sprites/level1/heart.png");
        JLabel healthIcon = new JLabel("", image, JLabel.CENTER);
        healthIcon.setBounds(0, 0, 80, 70);
        healthBar.add(healthIcon);
    }

    private void setupHealthStatusLabel() {
        healthStatusLabel = new JLabel("X " + healthPoints);
        healthStatusLabel.setForeground(Color.WHITE);
        healthStatusLabel.setFont(new Font("Courier", Font.BOLD, 19));
        healthStatusLabel.setBounds(80, 30, 60, 50);
        healthBar.add(healthStatusLabel);
    }

    private void setupDropInterface() {
        dropBar = new JPanel();
        dropBar.setBounds(980, 25, 120, 70);
        dropBar.setBackground(new Color(40,40,40,150));
        dropBar.setLayout(null);
        background.add(dropBar);
        setupDropBar();
    }

    private void setupDropBar() {
        dropStatusLabel = new JLabel(dropsCollected + " / " + dropsNeeded);
        dropStatusLabel.setFont(new Font("Courier", Font.BOLD, 18));
        dropStatusLabel.setForeground(Color.WHITE);
        dropStatusLabel.setBounds(30,10,100,50);
        dropBar.add(dropStatusLabel);
    }

    private void updateHealthStatusLabel() {
        healthStatusLabel.setVisible(false);
        healthStatusLabel.setText("X " + healthPoints);
        healthStatusLabel.setVisible(true);
    }

    private void updateDropStatusLabel() {
        dropStatusLabel.setVisible(false);
        dropStatusLabel.setText(dropsCollected + " / " + dropsNeeded);
        dropStatusLabel.setVisible(true);
    }

    private void setupSkipButton() {
        skipButton = new JButton(new ImageIcon("src/src/assets/sprites/level1/exiticon.png"));
        skipButton.setBounds(1125, 25, 50, 50);
        skipButton.addActionListener(e -> {
            playButtonAudioEffect();
            timer.stop();
            backgroundClip.stop();

            Master.gameIsOn = false;
            Master.level1Frame.dispose();
            Master.level1Frame = null;

            Master.menuPanel = new MenuPanel();
            Master.window.add(Master.menuPanel);
            Master.window.setVisible(true);

        });
        background.add(skipButton);
    }

    private void setupBucket() {
        BufferedImage bucket;
        try {
            bucket = ImageIO.read(new File("src/src/assets/sprites/level1/bucket.png"));
            bucketModel = new JLabel(new ImageIcon(bucket));
        } catch (IOException e) { throw new RuntimeException(e); }
        bucketModel.setBounds(new Rectangle(550, 560, 100, 116));
        background.add(bucketModel, JLayeredPane.DEFAULT_LAYER);
    }

    private void createDrop() {
        BufferedImage dropImage;
        JLabel dropModel;
        try {
            dropImage = ImageIO.read(new File("src/src/assets/sprites/level1/waterdrop.png"));
            dropModel = new JLabel(new ImageIcon(dropImage));
        } catch (IOException e) { throw new RuntimeException(e); }
        dropModel.setBounds(getRandomNumber(100, 1044), -60, 56, 89);
        background.add(dropModel);
        waterDrops.add(dropModel);
    }
    private void createStick() {
        BufferedImage stickImage;
        JLabel stickModel;
        try {
            Random rd = new Random();
            if(rd.nextBoolean()) stickImage = ImageIO.read(new File("src/src/assets/sprites/level1/stick.png"));
            else stickImage = ImageIO.read(new File("src/src/assets/sprites/level1/m_stick.png"));
            stickModel = new JLabel(new ImageIcon(stickImage));
        } catch (IOException e) { throw new RuntimeException(e); }
        stickModel.setBounds(getRandomNumber(100, 1044), -40, 80, 40);
        background.add(stickModel);
        sticks.add(stickModel);
    }
    private void createLeaf() {
        BufferedImage leafImage;
        JLabel leafModel;
        try {
            Random rd = new Random();
            if(rd.nextBoolean()) leafImage = ImageIO.read(new File("src/src/assets/sprites/level1/leaf.png"));
            else leafImage = ImageIO.read(new File("src/src/assets/sprites/level1/m_leaf.png"));
            leafModel = new JLabel(new ImageIcon(leafImage));
        } catch (IOException e) { throw new RuntimeException(e); }
        leafModel.setBounds(getRandomNumber(100, 1044), -60, 109, 60);
        background.add(leafModel);
        leafs.add(leafModel);
    }

    private void manageElementsMovement() {
        for(int i = 0; i < waterDrops.size(); i++) {
            JLabel drop = waterDrops.get(i);
            if(drop.getY() >= Master.windowHeight) {
                playWaterMissedClip();
                waterDrops.remove(drop);
            }
        }
        for (JLabel drop : waterDrops) {
            drop.setLocation(drop.getX(), drop.getY() + dropYVelocity);
        }
        for(JLabel stick : sticks) {
            int stickYVelocity = 13;
            stick.setLocation(stick.getX(), stick.getY() + stickYVelocity);
        }
        for(JLabel leaf : leafs) {
            int leafXVelocity = 3;
            int leafYVelocity = 4;
            if(leafMoveDirection == -1) leaf.setLocation(leaf.getX() - leafXVelocity, leaf.getY() + leafYVelocity);
            else if(leafMoveDirection == 1) leaf.setLocation(leaf.getX() + leafXVelocity, leaf.getY() + leafYVelocity);
        }
        switch (bucketMoveDirection) {
            case 0:
                break;
            case -1:
                bucketModel.setLocation(bucketModel.getX() - bucketXVelocity, bucketModel.getY());
                if(splashModel != null) splashModel.setLocation(splashModel.getX() - bucketXVelocity, splashModel.getY());
                break;
            case 1:
                bucketModel.setLocation(bucketModel.getX() + bucketXVelocity, bucketModel.getY());
                if(splashModel != null) splashModel.setLocation(splashModel.getX() + bucketXVelocity, splashModel.getY());
                break;
        }
    }

    private void checkForCollisions() {
        if(bucketModel.getX() + bucketModel.getWidth() < 0) bucketModel.setLocation(Master.windowWidth, bucketModel.getY());
        else if(bucketModel.getX() > Master.windowWidth) bucketModel.setLocation(-bucketModel.getWidth(), bucketModel.getY());

        for(int i = 0; i < waterDrops.size(); i++) {
            JLabel drop = waterDrops.get(i);
            Rectangle labelBounds = drop.getBounds();
            Rectangle bucketBounds = bucketModel.getBounds();
            if(labelBounds.intersects(bucketBounds)) {
                dropsCollected++;
                waterDrops.remove(drop);
                background.remove(drop);
                playWaterSplashClip();
                createSplash();
            }
        }
        for(int i = 0; i < leafs.size(); i++) {
            JLabel leaf = leafs.get(i);
            Rectangle leafBounds = leaf.getBounds();
            Rectangle bucketBounds = bucketModel.getBounds();
            if(leafBounds.intersects(bucketBounds)) {
                leafs.remove(leaf);
                background.remove(leaf);
                playLeafHitClip();
                healthPoints--;
                createSplash();
            }
            if(leaf.getX() < 0) leafMoveDirection = 1;
            else if(leaf.getX() + leaf.getWidth() > Master.windowWidth) leafMoveDirection = -1;
        }
        for(int i = 0; i < sticks.size(); i++) {
            JLabel stick = sticks.get(i);
            Rectangle stickBounds = stick.getBounds();
            Rectangle bucketBounds = bucketModel.getBounds();
            if(stickBounds.intersects(bucketBounds)) {
                sticks.remove(stick);
                background.remove(stick);
                playStickHitClip();
                healthPoints--;
                createSplash();
            }
        }
    }

    private void createSplash() {
        if (SplashObserver.getCounter() <= 2200 && splashModel != null) {
            background.remove(splashModel);
            splashModel = null;
            splashObserver = null;
            splashTimerStarted = false;
        }
        setupSplashGif();
    }

    private void setupSplashGif() {
        ImageIcon splashImage = new ImageIcon("src/src/assets/sprites/level1/splash.gif");
        JLabel splashAnim = new JLabel("", splashImage, JLabel.CENTER);
        splashAnim.setBounds(bucketModel.getX() - 5, bucketModel.getY() - 55, 95, 80);
        splashModel = splashAnim;
        splashObserver = new SplashObserver();
        background.add(splashAnim, JLayeredPane.PALETTE_LAYER);
        splashTimerStarted = true;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        instructionLabel.setVisible(true);
        if(!gameTimerStarted) {
            timer.start();
            gameTimerStarted = true;
        }
        char input = Character.toLowerCase(keyEvent.getKeyChar());
        if(input == 'a' || input == 'ф') bucketMoveDirection = -1;
        else if(input == 'd' || input == 'в') bucketMoveDirection = 1;
        else if(input == 's' || input == 'і' || input == 'ы' || input == ' ')  bucketMoveDirection = 0;

        if(Master.gameIsOn) bucketModel.setVisible(true);
        else {
            timer.stop();
            Master.gameIsOn = false;
        }
    }

    private void setupInstructionLabel() {
        instructionLabel = new JLabel("Use 'A', 'S', 'D' to move a bucket");
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setFont(new Font("Courier", Font.BOLD, 20));
        instructionLabel.setBounds(400, 670, 500, 100);
        instructionLabel.setVisible(false);
        background.add(instructionLabel);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void showGameResult() {
        skipButton.setEnabled(false);
        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(120, 120, 120, 120));
        resultPanel.setBounds(350, 260, 500, 300);
        resultPanel.setLayout(null);

        JLabel resultLabel = new JLabel();
        resultLabel.setBounds(50, 70, 440, 60);
        resultLabel.setFont(new Font("Courier", Font.BOLD, 20));
        resultLabel.setForeground(Color.WHITE);

        if(isAWin) {
            resultLabel.setText("Congratulations! Level is completed.");
            playLevelCompletedClip();
        } else {
            resultLabel.setText("That is a shame... Level is failed.");
            playLevelFailedClip();
        }

        exitButton = new JButton("EXIT LEVEL");
        exitButton.setLayout(null);
        exitButton.setBounds(100, 180, 120, 60);
        exitButton.addActionListener(e -> {
            timer.stop();
            playButtonAudioEffect();
            Master.gameIsOn = false;
            Master.level1Frame.dispose();

            if(UserDefaultManager.getProgress() == 1) {
                Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/outro_level1"));
                Master.window.setVisible(true);
            } else {
                Master.window.add(Master.menuPanel = new MenuPanel());
                Master.window.setVisible(true);
            }
            if(isAWin && UserDefaultManager.getProgress() < 2) UserDefaultManager.setProgress(2);
        });

        repeatButton = new JButton("TRY AGAIN");
        repeatButton.setBounds(280, 180, 120, 60);
        repeatButton.addActionListener(e -> {
            timer.stop();
            playButtonAudioEffect();
            Master.menuBackgroundClip.stop();

            Master.gameIsOn = true;
            Master.level1Frame.dispose();
            Master.level1Frame = new Level1Frame();
            Master.level1Frame.setVisible(true);
        });

        resultPanel.add(resultLabel);
        resultPanel.add(exitButton);
        resultPanel.add(repeatButton);
        background.add(resultPanel);
    }

    private void setupAudioEffects() {
        playBackgroundMusic();
        setupSoundEffects();
    }

    private void playBackgroundMusic() {
        try {
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/space-chillout.wav")));
            Master.setVolume(backgroundClip, (float) UserDefaultManager.getMusicVolume());
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void setupSoundEffects() {
        try {
            buttonClickSound = AudioSystem.getClip();
            waterSplashClip = AudioSystem.getClip();
            waterMissedClip = AudioSystem.getClip();
            stickHitClip = AudioSystem.getClip();
            leafHitClip = AudioSystem.getClip();
            levelCompletedClip = AudioSystem.getClip();
            levelFailedClip = AudioSystem.getClip();
            buttonClickSound.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/ui-click.wav")));
            waterSplashClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/water-splash.wav")));
            waterMissedClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/water-missed.wav")));
            stickHitClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/stick-splash.wav")));
            leafHitClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/leaf-splash.wav")));
            levelCompletedClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/level-completed.wav")));
            levelFailedClip.open(AudioSystem.getAudioInputStream(new File("src/src/assets/audio/level-failed.wav")));
            Master.setVolume(buttonClickSound, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(waterMissedClip, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(waterSplashClip, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(stickHitClip, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(leafHitClip, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(levelCompletedClip, (float) UserDefaultManager.getAudioEffectsVolume());
            Master.setVolume(levelFailedClip, (float) UserDefaultManager.getAudioEffectsVolume());
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void playButtonAudioEffect() {
        buttonClickSound.setFramePosition(0);
        buttonClickSound.start();
    }
    private void playWaterSplashClip() {
        waterSplashClip.setFramePosition(0);
        waterSplashClip.start();
    }
    private void playWaterMissedClip() {
        waterMissedClip.setFramePosition(0);
        waterMissedClip.start();
    }
    private void playStickHitClip() {
        stickHitClip.setFramePosition(0);
        stickHitClip.start();
    }
    private void playLeafHitClip() {
        leafHitClip.setFramePosition(0);
        leafHitClip.start();
    }
    private void playLevelCompletedClip() {
        levelCompletedClip.setFramePosition(0);
        levelCompletedClip.start();
    }
    private void playLevelFailedClip() {
        levelFailedClip.setFramePosition(0);
        levelFailedClip.start();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {}
    @Override
    public void keyReleased(KeyEvent keyEvent) {}

    private JButton exitButton, skipButton, repeatButton;
    private JPanel healthBar, dropBar;
    private JLabel background, healthStatusLabel, dropStatusLabel, instructionLabel, bucketModel, splashModel = null;
    private Clip backgroundClip, waterSplashClip, waterMissedClip, stickHitClip, leafHitClip, levelCompletedClip, levelFailedClip, buttonClickSound;
    private long elapsedTime, generalDelay, stickDelay, leafDelay;
    private final int dropFrequency = 120;
    private int stickFrequency = 380;
    private int leafFrequency = 380;
    private int dropYVelocity = 7, bucketXVelocity = 8;
    private int dropsCollected, healthPoints = 7, bucketMoveDirection = 0, leafMoveDirection = 1;
    private int dropsNeeded;

    private void defineDifficulty() {
        switch (UserDefaultManager.getDifficulty()) {
            case 0:
                dropsNeeded = 15;
                break;
            case 1:
                dropsNeeded = 33;
                break;
            case 2:
                dropsNeeded = 45;
                break;
        }
    }

    private ArrayList<JLabel> waterDrops = new ArrayList<>(), leafs = new ArrayList<>(), sticks = new ArrayList<>();
    private SplashObserver splashObserver = null;
    private boolean gameTimerStarted = false, splashTimerStarted = false, isAWin = false;


}
