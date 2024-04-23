import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappySpriteCan extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image SpriteCanImg;
    Image topCanOpenerImg;
    Image bottomCanOpenerImg;

    // SpriteCan class
    int SpriteCanX = boardWidth / 8;
    int SpriteCanY = boardWidth / 2;
    int SpriteCanWidth = 24;
    int SpriteCanHeight = 34;

    class SpriteCan {
        int x = SpriteCanX;
        int y = SpriteCanY;
        int width = SpriteCanWidth;
        int height = SpriteCanHeight;
        Image img;

        SpriteCan(Image img) {
            this.img = img;
        }
    }

    // CanOpener class
    int CanOpenerX = boardWidth;
    int CanOpenerY = 0;
    int CanOpenerWidth = 64; // scaled by 1/6
    int CanOpenerHeight = 512;

    class CanOpener {
        int x = CanOpenerX;
        int y = CanOpenerY;
        int width = CanOpenerWidth;
        int height = CanOpenerHeight;
        Image img;
        boolean passed = false;

        CanOpener(Image img) {
            this.img = img;
        }
    }

    // game logic
    SpriteCan SpriteCan;
    int velocityX = -4; // move CanOpeners to the left speed (simulates SpriteCan moving right)
    int velocityY = 0; // move SpriteCan up/down speed.
    int gravity = 1;

    ArrayList<CanOpener> CanOpeners;
    Random random = new Random();

    Timer gameLoop;
    Timer placeCanOpenerTimer;
    boolean gameOver = false;
    double score = 0;

    FlappySpriteCan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./sodajumpbg.png")).getImage();
        SpriteCanImg = new ImageIcon(getClass().getResource("./can.png")).getImage();
        topCanOpenerImg = new ImageIcon(getClass().getResource("./topcanopener.png")).getImage();
        bottomCanOpenerImg = new ImageIcon(getClass().getResource("./bottomcanopener.png")).getImage();

        // SpriteCan
        SpriteCan = new SpriteCan(SpriteCanImg);
        CanOpeners = new ArrayList<CanOpener>();

        // place CanOpeners timer
        placeCanOpenerTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to be executed
                placeCanOpeners();
            }
        });
        placeCanOpenerTimer.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this); // how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();
    }

    void placeCanOpeners() {
        // (0-1) * CanOpenerHeight/2.
        // 0 -> -128 (CanOpenerHeight/4)
        // 1 -> -128 - 256 (CanOpenerHeight/4 - CanOpenerHeight/2) = -3/4 CanOpenerHeight
        int randomCanOpenerY = (int) (CanOpenerY - CanOpenerHeight / 4 - Math.random() * (CanOpenerHeight / 2));
        int openingSpace = boardHeight / 4;

        CanOpener topCanOpener = new CanOpener(topCanOpenerImg);
        topCanOpener.y = randomCanOpenerY;
        CanOpeners.add(topCanOpener);

        CanOpener bottomCanOpener = new CanOpener(bottomCanOpenerImg);
        bottomCanOpener.y = topCanOpener.y + CanOpenerHeight + openingSpace;
        CanOpeners.add(bottomCanOpener);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        // SpriteCan
        g.drawImage(SpriteCanImg, SpriteCan.x, SpriteCan.y, SpriteCan.width, SpriteCan.height, null);

        // CanOpeners
        for (int i = 0; i < CanOpeners.size(); i++) {
            CanOpener CanOpener = CanOpeners.get(i);
            g.drawImage(CanOpener.img, CanOpener.x, CanOpener.y, CanOpener.width, CanOpener.height, null);
        }

        // score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        // SpriteCan
        velocityY += gravity;
        SpriteCan.y += velocityY;
        SpriteCan.y = Math.max(SpriteCan.y, 0); // apply gravity to current SpriteCan.y, limit the SpriteCan.y to top of the canvas

        // CanOpeners
        for (int i = 0; i < CanOpeners.size(); i++) {
            CanOpener CanOpener = CanOpeners.get(i);
            CanOpener.x += velocityX;

            if (!CanOpener.passed && SpriteCan.x > CanOpener.x + CanOpener.width) {
                score += 0.5; // 0.5 because there are 2 CanOpeners! so 0.5*2 = 1, 1 for each set of CanOpeners
                CanOpener.passed = true;
            }

            if (collision(SpriteCan, CanOpener)) {
                gameOver = true;
            }
        }

        if (SpriteCan.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(SpriteCan a, CanOpener b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { // called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placeCanOpenerTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            velocityY = -9;

            if (gameOver) {
                // restart game by resetting conditions
                SpriteCan.y = SpriteCanY;
                velocityY = 0;
                CanOpeners.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placeCanOpenerTimer.start();
            }
        }
    }

    // not needed
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
