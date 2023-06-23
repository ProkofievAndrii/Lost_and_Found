package assets.sprites.level3;

import stages.Master;
import stages.cutscenes.IntroUniversalPanel;
import stages.menu.MenuPanel;
import stages.utils.UserDefaultManager;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Level3Frame extends JFrame {
    private JLabel rebusLabel;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel heartsLabel;
    private JLabel livesLabel;
    private JLabel backgroundLabel;
    private JButton hintButton;
    private JButton exitButton;
    private JLabel hintsCountLabel;

    private int currentRebusIndex = 0;
    private int lives = 3;
    private int hintsCount = 5;
    private Clip backgroundMusicClip;
    private Clip click;
    private Clip win;

    private String[] rebuses = {
            "src/src/assets/sprites/level3/rebus1.png",
            "src/src/assets/sprites/level3/rebus2.png",
            "src/src/assets/sprites/level3/rebus3.png",
            "src/src/assets/sprites/level3/rebus4.png",
            "src/src/assets/sprites/level3/rebus5.png",
            "src/src/assets/sprites/level3/rebus6.png"
    };

    private int[] rebusWidths = {
            810,
            343,
            415,
            369,
            748,
            306
    };

    private int[] rebusHeights = {
            159,
            207,
            213,
            190,
            188,
            179
    };

    private String[] answers = {
            "астронавт",
            "зірка",
            "кисень",
            "ракета",
            "туманність",
            "шолом"
    };

    public Level3Frame() {
        defineDifficulty();
        setTitle("Rebus Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        URL backgroundUrl = Level3Frame.class.getResource("src/src/assets/sprites/level3/third_picture.png");
        ImageIcon backgroundIcon = new ImageIcon("src/src/assets/sprites/level3/third_picture.png");
        backgroundLabel = new JLabel(backgroundIcon);
        add(backgroundLabel);

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/src/assets/sprites/level3/at-the-clouds.wav"));
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);
            Master.setVolume(backgroundMusicClip, (float) UserDefaultManager.getMusicVolume());
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        rebusLabel = new JLabel(new ImageIcon(rebuses[currentRebusIndex]));
        rebusLabel.setBounds((1200 - rebusWidths[currentRebusIndex]) / 2, (800 - rebusHeights[currentRebusIndex]) / 2 - 100, rebusWidths[currentRebusIndex], rebusHeights[currentRebusIndex]);
        rebusLabel.setHorizontalAlignment(JLabel.CENTER);
        backgroundLabel.add(rebusLabel);

        inputField = new JTextField();
        inputField.setBounds(400, 450, 400, 40);
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.requestFocus();
        backgroundLabel.add(inputField);

        submitButton = new JButton("");

        ImageIcon submitButtonIcon = new ImageIcon("src/src/assets/sprites/level3/mark.png");
        submitButton = new JButton(submitButtonIcon);
        submitButton.setBounds(800, 445, submitButtonIcon.getIconWidth(), submitButtonIcon.getIconHeight());
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        backgroundLabel.add(submitButton);

        heartsLabel = new JLabel(new ImageIcon("src/src/assets/sprites/level3/heart.png"));
        heartsLabel.setBounds(10, 10, 50, 50);
        backgroundLabel.add(heartsLabel);

        livesLabel = new JLabel("3");
        livesLabel.setBounds(30, 55, 20, 30);
        livesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(livesLabel);

        hintButton = new JButton();
        hintButton.setBounds(100, 10, 70, 50);
        hintButton.setIcon(new ImageIcon("src/src/assets/sprites/level3/hint1 (1).png"));
        hintButton.setOpaque(false);
        hintButton.setContentAreaFilled(false);
        hintButton.setBorderPainted(false);
        backgroundLabel.add(hintButton);

        hintsCountLabel = new JLabel("5");
        hintsCountLabel.setBounds(130, 55, 100, 30);
        hintsCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundLabel.add(hintsCountLabel);

        exitButton = new JButton();
        exitButton.setBounds(200, 15, 40, 40);
        exitButton.setIcon(new ImageIcon("src/src/assets/sprites/level3/exiticon.png"));
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        backgroundLabel.add(exitButton);

        click = loadSound("src/src/assets/sprites/level3/cliсk.wav");
        win = loadSound("src/src/assets/sprites/level3/you win.wav");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText().trim();
                if (checkAnswer(userInput)) {
                    click.setFramePosition(0);
                    click.start();
                    inputField.setText("");
                    nextRebus();
                    inputField.requestFocus();
                } else {
                    if (lives > 1) {
                        lives--;
                        livesLabel.setText(String.valueOf(lives));
                        click.setFramePosition(0);
                        click.start();

                        JPanel livePanel = new JPanel();
                        livePanel.setBackground(new Color(120, 120, 120, 120));
                        livePanel.setBounds(350, 250, 500, 300);
                        livePanel.setLayout(null);

                        JLabel liveLabel = new JLabel();
                        liveLabel.setText("<html>Неправильна відповідь!<br> Залишилось " + lives + " життя.<html>");
                        liveLabel.setForeground(Color.WHITE);
                        liveLabel.setFont(new Font("Courier", Font.BOLD, 22));
                        liveLabel.setBounds(120, 40, 500, 200);
                        livePanel.add(liveLabel, BorderLayout.CENTER);

                        JButton okButton = new JButton();
                        okButton.setText("OK");
                        okButton.setForeground(Color.WHITE);
                        okButton.setFont(new Font("Courier", Font.BOLD, 20));
                        okButton.setBorderPainted(false);
                        okButton.setContentAreaFilled(false);
                        okButton.setBounds(210, 200, 80, 30);
                        okButton.setVisible(true);
                        okButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                click.setFramePosition(0);
                                click.start();
                                livePanel.getParent().remove(livePanel);
                                backgroundLabel.revalidate();
                                backgroundLabel.repaint();
                            }
                        });
                        livePanel.add(okButton);

                        backgroundLabel.add(livePanel);

                        backgroundLabel.setComponentZOrder(livePanel, 0);

                        backgroundLabel.revalidate();
                        backgroundLabel.repaint();


                        inputField.setText("");
                    } else {
                        click.setFramePosition(0);
                        click.start();

                        JPanel livePanel = new JPanel();
                        livePanel.setBackground(new Color(120, 120, 120, 120));
                        livePanel.setBounds(350, 250, 500, 300);
                        livePanel.setPreferredSize(new Dimension(500, 360));
                        livePanel.setLayout(null);

                        JLabel liveLabel = new JLabel();
                        liveLabel.setText("<html>Неправильна відповідь!<br>Гра завершена.</html>");
                        liveLabel.setForeground(Color.WHITE);
                        liveLabel.setFont(new Font("Courier", Font.BOLD, 24));
                        liveLabel.setBounds(110, 100, 500, 60);
                        livePanel.add(liveLabel, BorderLayout.CENTER);

                        JButton okButton = new JButton();
                        okButton.setText("OK");
                        okButton.setForeground(Color.WHITE);
                        okButton.setFont(new Font("Courier", Font.BOLD, 20));
                        okButton.setBorderPainted(false);
                        okButton.setContentAreaFilled(false);
                        okButton.setBounds(210, 200, 80, 30);
                        okButton.setVisible(true);
                        okButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                click.setFramePosition(0);
                                click.start();
                                livePanel.getParent().remove(livePanel);
                                backgroundLabel.revalidate();
                                backgroundLabel.repaint();

                                Master.prepareMenuPanel();
                            }
                        });
                        livePanel.add(okButton);

                        backgroundLabel.add(livePanel);
                        backgroundLabel.setComponentZOrder(livePanel, 0);

                        backgroundLabel.revalidate();
                        backgroundLabel.repaint();
                    }
                }
            }
        });

        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hintsCount > 0) {
                    hintsCount--;
                    hintsCountLabel.setText(String.valueOf(hintsCount));
                    String hint = answers[currentRebusIndex];
                    String userInput = inputField.getText().trim();
                    if (!userInput.equals(hint)) {
                        int userAnswerLength = userInput.length();
                        if (userAnswerLength < hint.length()) {
                            char nextHintChar = hint.charAt(userAnswerLength);
                            userInput += nextHintChar;
                            inputField.setText(userInput);
                        }
                    }

                } else {
                    click.setFramePosition(0);
                    click.start();
                    //JOptionPane.showMessageDialog(null, "Ви використали всі підказки.");
                    JPanel livePanel = new JPanel();
                    livePanel.setBackground(new Color(120, 120, 120, 120));
                    livePanel.setBounds(350, 250, 500, 300);
                    livePanel.setPreferredSize(new Dimension(500, 360));
                    livePanel.setLayout(null);

                    JLabel liveLabel = new JLabel();
                    liveLabel.setText("Ви використали всі підказки.");
                    liveLabel.setForeground(Color.WHITE);
                    liveLabel.setFont(new Font("Courier", Font.BOLD, 20));
                    liveLabel.setBounds(80, 100, 500, 60);
                    livePanel.add(liveLabel, BorderLayout.CENTER);

                    JButton okButton = new JButton();
                    okButton.setText("OK");
                    okButton.setForeground(Color.WHITE);
                    okButton.setFont(new Font("Courier", Font.BOLD, 20));
                    okButton.setBorderPainted(false);
                    okButton.setContentAreaFilled(false);
                    okButton.setBounds(210, 200, 80, 30);
                    okButton.setVisible(true);
                    okButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            click.setFramePosition(0);
                            click.start();
                            livePanel.getParent().remove(livePanel);
                            backgroundLabel.revalidate();
                            backgroundLabel.repaint();
                        }
                    });
                    livePanel.add(okButton);

                    backgroundLabel.add(livePanel);

                    backgroundLabel.setComponentZOrder(livePanel, 0);

                    backgroundLabel.revalidate();
                    backgroundLabel.repaint();
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundMusicClip.stop();
                Master.level3Frame.setVisible(false);
                Master.prepareMenuPanel();
            }
        });
    }

    private boolean checkAnswer(String userInput) {
        String correctAnswer = answers[currentRebusIndex];
        return userInput.equalsIgnoreCase(correctAnswer);
    }

    private void nextRebus() {
        currentRebusIndex++;
        if (currentRebusIndex >= rebuses.length) {
            win.setFramePosition(0);
            win.start();

            backgroundMusicClip.stop();
            Master.level3Frame.setVisible(false);
            if(UserDefaultManager.getProgress() == 3) {
                Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/outro_level3"));
                Master.window.setVisible(true);
            } else {
                Master.window.add(Master.menuPanel = new MenuPanel());
                Master.window.setVisible(true);
            }
            if(UserDefaultManager.getProgress() < 4) UserDefaultManager.setProgress(4);
            if(Master.level3Frame != null) {
                Master.level3Frame.dispose();
                Master.level3Frame = null;
            }


        } else {
            rebusLabel.setIcon(new ImageIcon(rebuses[currentRebusIndex]));
            rebusLabel.setBounds((1200 - rebusWidths[currentRebusIndex]) / 2, (800 - rebusHeights[currentRebusIndex]) / 2 - 100, rebusWidths[currentRebusIndex], rebusHeights[currentRebusIndex]);
        }
    }

    private Clip loadSound(String soundPath){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(soundPath)));
            Master.setVolume(clip, (float) UserDefaultManager.getAudioEffectsVolume());
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
        return null;
    }

    public void dispose() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
        super.dispose();
    }

    private void defineDifficulty() {
        switch (UserDefaultManager.getDifficulty()) {
            case 0:
                lives = 4;
                hintsCount = 5;
                break;
            case 1:
                lives = 2;
                hintsCount = 3;
                break;
            case 2:
                lives = 1;
                hintsCount = 1;
                break;
        }
    }
}
