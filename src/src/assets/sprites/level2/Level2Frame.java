package assets.sprites.level2;

import stages.Master;
import stages.cutscenes.IntroUniversalPanel;
import stages.menu.MenuPanel;
import stages.utils.UserDefaultManager;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level2Frame extends JFrame {
    private JPanel gamePanel;
    private List<PuzzlePiece> puzzlePieces;
    private PuzzlePiece selectedPiece;
    private JPanel puzzlePanel;
    private JLabel hintLabel;
    private JLabel hintCounterLabel;
    private int hintCounter;
    private JDialog startDialog;
    private JDialog infoDialog;
    private Clip puzzleClickSound;
    private Clip hintButtonClickSound;
    private Clip infoButtonClickSound;
    private Clip backgroundClip;
    int width = 947;
    int height = 628;
    int windowWidth = 1200;
    int windowHeight = 800;

    public Level2Frame() {
        setBounds(0,0, windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        puzzlePieces = new ArrayList<>();
        hintCounter = 3;

        int numRows = 5;
        int numColumns = 5;
        int pieceWidth = width / numColumns;
        int pieceHeight = height / numRows;


        for (int i = 0; i < 25; i++) {
            String imagePath = "image" + (i + 1) + ".png";
            try {
                URL imageUrl = Level2Frame.class.getResource(imagePath);
                BufferedImage image = ImageIO.read(imageUrl);
                BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);

                Graphics2D g2d = transparentImage.createGraphics();
                g2d.drawImage(image, 0, 0, null);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                g2d.dispose();

                Image scaledImage = transparentImage.getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_DEFAULT);
                puzzlePieces.add(new PuzzlePiece(i + 1, scaledImage));
            } catch (IOException e) {
                e.printStackTrace();
                gamePanel = new JPanel(new BorderLayout());
            }
        }
        shufflePuzzlePieces(); //рандомне розставлення частин віражу

        final BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(new File("src/src/assets/sprites/level2/rocky planet.png"));
        } catch (IOException e) {
            e.printStackTrace();
            gamePanel = new JPanel(new BorderLayout());
            return;
        }


        gamePanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }
        };

        puzzlePanel = new JPanel(new GridLayout(numRows, numColumns));
        puzzlePanel.setOpaque(false);
        puzzlePanel.setPreferredSize(new Dimension(pieceWidth * numColumns, pieceHeight * numRows));

        URL lampImageUrl = Level2Frame.class.getResource("lamp.png");
        hintLabel = new JLabel(new ImageIcon(lampImageUrl));
        URL infoImageUrl = Level2Frame.class.getResource("inf.png");
        JLabel infoLabel = new JLabel(new ImageIcon(infoImageUrl));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(9, 13, 0, 0));
        hintCounterLabel = new JLabel(String.valueOf(hintCounter));
        URL exitImageUrl = Level2Frame.class.getResource("exiticon.png");
        JButton exitButton = new JButton(new ImageIcon(exitImageUrl));
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 13, 0, 0));

        hintLabel.addMouseListener(new HintMouseListener());
        infoLabel.addMouseListener(new InfoMouseListener());
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                backgroundClip.stop();
                Master.level2Frame.setVisible(false);
                Master.prepareMenuPanel();
            }
        });
        updateHintCounterLabel();

        for (PuzzlePiece piece : puzzlePieces) {
            piece.addMouseListener(new PieceMouseListener());
            puzzlePanel.add(piece);
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JPanel hintInfoPanel = new JPanel(new GridLayout(2, 1));
        hintInfoPanel.add(hintLabel);
        hintInfoPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(hintLabel);
        rightPanel.add(hintCounterLabel);
        rightPanel.add(infoLabel);
        rightPanel.add(exitButton);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(Box.createVerticalGlue());

        gamePanel.add(puzzlePanel, BorderLayout.CENTER);
        gamePanel.add(rightPanel, BorderLayout.LINE_END);
        gamePanel.setPreferredSize(new Dimension(width, height));
        JPanel placeholderPanel = new JPanel(new GridBagLayout());
        placeholderPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        placeholderPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints(); //сітка для складання вітражу
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        placeholderPanel.add(gamePanel, gbc);

        try {
            BufferedImage backgroundImage1 = ImageIO.read(new File("src/src/assets/sprites/level2/rocky planet.png"));
            JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage1));
            backgroundLabel.setLayout(new BorderLayout());
            backgroundLabel.add(placeholderPanel, BorderLayout.CENTER);
            add(backgroundLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pack();

        puzzleClickSound = loadSound("click.wav");
        hintButtonClickSound = loadSound("click.wav");
        infoButtonClickSound = loadSound("click.wav");

        pack();
        setLocationRelativeTo(null);
        setVisible(false);
        gamePanel.setOpaque(false);
        playBackgroundMusic();
        showStartDialog();
    }

    private void shufflePuzzlePieces() {
        Collections.shuffle(puzzlePieces);
    }

    private void playBackgroundMusic() {
        try {
            String musicPath = "ambient.wav";
            InputStream musicStream = new BufferedInputStream(getClass().getResourceAsStream(musicPath));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicStream);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            Master.setVolume(backgroundClip, (float) UserDefaultManager.getMusicVolume());
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
    }

    private Clip loadSound(String soundPath){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(soundPath)));
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
        return null;
    }
    private void showStartDialog() {
        JLabel startLabel = new JLabel("Зіберіть вітраж:");
        URL targetImageUrl = Level2Frame.class.getResource("glasses.png");
        ImageIcon targetImageIcon = new ImageIcon(targetImageUrl);
        JLabel targetImageLabel = new JLabel(targetImageIcon);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        dialogPanel.add(startLabel, BorderLayout.NORTH);
        dialogPanel.add(targetImageLabel, BorderLayout.CENTER);
        dialogPanel.setOpaque(false);

        JButton startButton = new JButton("Почати гру");
        startButton.addActionListener(e -> {
            setVisible(true);
            disposeStartDialog();
            infoButtonClickSound.setFramePosition(0);
            infoButtonClickSound.start();
        });
        dialogPanel.add(startButton, BorderLayout.NORTH);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("src/src/assets/sprites/level2/rocky planet.png");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        startDialog = new JDialog(this, "", true);
        startDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        startDialog.getContentPane().add(backgroundPanel, BorderLayout.CENTER);
        backgroundPanel.add(dialogPanel, BorderLayout.CENTER);

        startDialog.pack();
        startDialog.setLocationRelativeTo(null);
        startDialog.setVisible(true);
    }

    private void disposeStartDialog() {
        startDialog.dispose();
    }

    private void showInfoDialog() {
        URL targetImageUrl = Level2Frame.class.getResource("glasses (1).png");
        ImageIcon targetImageIcon = new ImageIcon(targetImageUrl);
        JLabel targetImageLabel = new JLabel(targetImageIcon);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(targetImageLabel, BorderLayout.CENTER);

        infoDialog = new JDialog(this, "Вітраж", true);
        infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        infoDialog.getContentPane().add(dialogPanel);
        infoDialog.pack();
        infoDialog.setLocationRelativeTo(null);
        infoDialog.setVisible(true);
    }

    private class PuzzlePiece extends JLabel {
        private int pieceNumber;
        private boolean isCorrect;
        private BufferedImage image;
        public PuzzlePiece(int number, Image image) {
            pieceNumber = number;
            isCorrect = false;
            this.image = toBufferedImage(image);
            setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        }
        private BufferedImage toBufferedImage(Image image) {
            if (image instanceof BufferedImage) {
                return (BufferedImage) image;
            }

            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null),
                    image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            return bufferedImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }

        public int getPieceNumber() {
            return pieceNumber;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
            if (correct) {
                setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            } else {
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
        }

        public boolean isCorrect() {
            return isCorrect;
        }
    }

    private class PieceMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            PuzzlePiece clickedPiece = (PuzzlePiece) e.getSource();

            if (selectedPiece == null) {
                selectedPiece = clickedPiece;
                selectedPiece.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                puzzleClickSound.setFramePosition(0);
                puzzleClickSound.start();
            } else {
                swapPieces(selectedPiece, clickedPiece);
                selectedPiece.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                selectedPiece = null;
                puzzleClickSound.setFramePosition(0);
                puzzleClickSound.start();
            }
        }

        private void swapPieces(PuzzlePiece piece1, PuzzlePiece piece2) {
            int index1 = puzzlePieces.indexOf(piece1);
            int index2 = puzzlePieces.indexOf(piece2);

            if (index1 != -1 && index2 != -1) {
                Collections.swap(puzzlePieces, index1, index2);
                updatePuzzlePanel();
                checkIfCompleted();
            }
        }

        private void updatePuzzlePanel() {
            puzzlePanel.removeAll();

            for (PuzzlePiece piece : puzzlePieces) {
                puzzlePanel.add(piece);
            }

            puzzlePanel.revalidate();
            puzzlePanel.repaint();
        }

        private void checkIfCompleted() {
            boolean completed = true;

            for (int i = 0; i < puzzlePieces.size(); i++) {
                PuzzlePiece piece = puzzlePieces.get(i);

                if (piece.getPieceNumber() != i + 1) {
                    completed = false;
                    break;
                }
            }

            if (completed) {
                backgroundClip.stop();
                backgroundClip.close();
                try {
                    String musicPath = "you win.wav";
                    InputStream musicStream = new BufferedInputStream(getClass().getResourceAsStream(musicPath));
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicStream);
                    backgroundClip = AudioSystem.getClip();
                    backgroundClip.open(audioInputStream);
                    Master.setVolume(backgroundClip, (float) UserDefaultManager.getAudioEffectsVolume());
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                    backgroundClip.start();
                    JPanel win = new JPanel();
                    win.setBackground(new Color(120, 120, 120, 120));
                    win.setBounds(300, 220, 500, 360);
                    win.setPreferredSize(new Dimension(500, 360));
                    win.setLayout(null);

                    JLabel hintLabel1 = new JLabel();
                    hintLabel1.setText("Відмінно! Вітраж зібрано!");
                    hintLabel1.setForeground(Color.WHITE);
                    hintLabel1.setFont(new Font("Courier", Font.BOLD, 20));
                    hintLabel1.setBounds(300, 220, 440, 60);
                    win.add(hintLabel1, BorderLayout.CENTER);

                    JButton okButton = new JButton();
                    okButton.setText("OK");
                    okButton.setForeground(Color.WHITE);
                    okButton.setFont(new Font("Courier", Font.BOLD, 20));
                    okButton.setBorderPainted(false);
                    okButton.setContentAreaFilled(false);
                    okButton.setBounds(410, 320, 80, 30);
                    okButton.setVisible(true);
                    okButton.addActionListener(e -> {
                        win.getParent().remove(win);
                        gamePanel.revalidate();
                        gamePanel.repaint();

                        if(UserDefaultManager.getProgress() == 2) {
                            Master.window.add(Master.introUniversalPanel = new IntroUniversalPanel("src/src/assets/sprites/outro_level2"));
                            Master.window.setVisible(true);
                        } else {
                            Master.window.add(Master.menuPanel = new MenuPanel());
                            Master.window.setVisible(true);
                        }
                        if(UserDefaultManager.getProgress() < 3) UserDefaultManager.setProgress(3);
                        if(Master.level2Frame != null) {
                            Master.level2Frame.dispose();
                            Master.level2Frame = null;
                        }
                    });
                    win.add(okButton);

                    gamePanel.add(win);

                    gamePanel.setComponentZOrder(win, 0);

                    gamePanel.revalidate();
                    gamePanel.repaint();

                    hintButtonClickSound.setFramePosition(0);
                    hintButtonClickSound.start();
                    backgroundClip.close();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
                    e.printStackTrace();
                }

            }
        }
    }
    private class HintMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (hintCounter == 0) {
                hintButtonClickSound.setFramePosition(0);
                hintButtonClickSound.start();

                JPanel hint = new JPanel();
                hint.setBackground(new Color(120, 120, 120, 120));
                hint.setBounds(350, 220, 500, 360);
                hint.setPreferredSize(new Dimension(500, 360));
                hint.setLayout(null);

                JLabel hintLabel1 = new JLabel();
                hintLabel1.setText("Ви використали всі підказки.");
                hintLabel1.setForeground(Color.WHITE);
                hintLabel1.setFont(new Font("Courier", Font.BOLD, 20));
                hintLabel1.setBounds(320, 220, 440, 60);
                hint.add(hintLabel1, BorderLayout.CENTER);

                JButton okButton = new JButton();
                okButton.setText("OK");
                okButton.setForeground(Color.WHITE);
                okButton.setFont(new Font("Courier", Font.BOLD, 20));
                okButton.setBorderPainted(false);
                okButton.setContentAreaFilled(false);
                okButton.setBounds(410, 300, 80, 30);
                okButton.setVisible(true);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hint.getParent().remove(hint);
                        gamePanel.revalidate();
                        gamePanel.repaint();
                    }
                });
                hint.add(okButton);

                gamePanel.add(hint);

                gamePanel.setComponentZOrder(hint, 0);

                gamePanel.revalidate();
                gamePanel.repaint();

                hintButtonClickSound.setFramePosition(0);
                hintButtonClickSound.start();

                return;
            }


            if (selectedPiece == null) {
                hintButtonClickSound.setFramePosition(0);
                hintButtonClickSound.start();
                //JOptionPane.showMessageDialog(gamePanel, "Виберіть частину вітражу, щоб отримати підказку.");
                JPanel hint = new JPanel();
                hint.setBackground(new Color(120, 120, 120, 120));
                hint.setBounds(350, 220, 500, 360);
                hint.setPreferredSize(new Dimension(500, 360));
                hint.setLayout(null);

                JLabel hintLabel1 = new JLabel("", SwingConstants.CENTER);
                hintLabel1.setText("Виберіть частину вітражу, щоб отримати підказку.");
                hintLabel1.setForeground(Color.WHITE);
                hintLabel1.setFont(new Font("Courier", Font.BOLD, 20));
                hintLabel1.setBounds(160, 250, 600, 60);
                hint.add(hintLabel1, BorderLayout.CENTER);

                JButton okButton = new JButton();
                okButton.setText("OK");
                okButton.setForeground(Color.WHITE);
                okButton.setFont(new Font("Courier", Font.BOLD, 20));
                okButton.setBorderPainted(false);
                okButton.setContentAreaFilled(false);
                okButton.setBounds(400, 320, 80, 30);
                okButton.setVisible(true);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hint.getParent().remove(hint);
                        gamePanel.revalidate();
                        gamePanel.repaint();
                    }
                });
                hint.add(okButton);

                gamePanel.add(hint);

                gamePanel.setComponentZOrder(hint, 0);

                gamePanel.revalidate();
                gamePanel.repaint();
                hintButtonClickSound.setFramePosition(0);
                hintButtonClickSound.start();
                return;
            }

            if (!selectedPiece.isCorrect()) {
                int correctIndex = selectedPiece.getPieceNumber() - 1;
                PuzzlePiece correctPiece = puzzlePieces.get(correctIndex);
                correctPiece.setCorrect(true);
                hintCounter--;
                updateHintCounterLabel();
                if (correctIndex == puzzlePieces.indexOf(selectedPiece)) {
                    hintButtonClickSound.setFramePosition(0);
                    hintButtonClickSound.start();
                    //JOptionPane.showMessageDialog(gamePanel, "Ця частина вітражу знаходиться на правильному місці!");
                    JPanel hint = new JPanel();
                    hint.setBackground(new Color(120, 120, 120, 120));
                    hint.setBounds(350, 220, 500, 360);
                    hint.setPreferredSize(new Dimension(500, 360));
                    hint.setLayout(null);

                    JLabel hintLabel1 = new JLabel();
                    hintLabel1.setText("Ця частина вітражу знаходиться на правильному місці!");
                    hintLabel1.setForeground(Color.WHITE);
                    hintLabel1.setFont(new Font("Courier", Font.BOLD, 20));
                    hintLabel1.setBounds(130, 250, 700, 60);
                    hint.add(hintLabel1, BorderLayout.CENTER);

                    JButton okButton = new JButton();
                    okButton.setText("OK");
                    okButton.setForeground(Color.WHITE);
                    okButton.setFont(new Font("Courier", Font.BOLD, 20));
                    okButton.setBorderPainted(false);
                    okButton.setContentAreaFilled(false);
                    okButton.setBounds(410, 350, 80, 30);
                    okButton.setVisible(true);
                    okButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            hint.getParent().remove(hint);
                            gamePanel.revalidate();
                            gamePanel.repaint();
                        }
                    });
                    hint.add(okButton);

                    gamePanel.add(hint);

                    gamePanel.setComponentZOrder(hint, 0);

                    gamePanel.revalidate();
                    gamePanel.repaint();
                    hintButtonClickSound.setFramePosition(0);
                    hintButtonClickSound.start();
                }
            }
            hintButtonClickSound.setFramePosition(0);
            hintButtonClickSound.start();
        }
    }
    private JLabel hintLabel1;

    private class InfoMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            infoButtonClickSound.setFramePosition(0);
            infoButtonClickSound.start();
            showInfoDialog();
            infoButtonClickSound.setFramePosition(0);
            infoButtonClickSound.start();
        }
    }
    private class CornerMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            infoButtonClickSound.setFramePosition(0);
            infoButtonClickSound.start();
        }
    }

    private void updateHintCounterLabel() {
        hintCounterLabel.setForeground(Color.WHITE);
        hintCounterLabel.setText("         " + hintCounter + "        ");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(Level2Frame::new);
//    }
}
