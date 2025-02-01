import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 630;

    Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;
    int birdX = boardWidth / 8, birdY = boardWidth / 2, birdWidth = 34, birdHeight = 24;

    class Bird {
        int x = birdX, y = birdY, width = birdWidth, height = birdHeight;
        Image img;
        Bird(Image img) { this.img = img; }
    }

    int pipeX = boardWidth, pipeY = 0, pipeWidth = 64, pipeHeight = 512;
    class Pipe {
        int x = pipeX, y = pipeY, width = pipeWidth, height = pipeHeight;
        Image img; boolean passed = false;
        Pipe(Image img) { this.img = img; }
    }

    Bird bird;
    int velocityX = -4, velocityY = 0, gravity = 1;
    ArrayList<Pipe> pipes = new ArrayList<>();
    Random random = new Random();
    Timer gameLoop, placePipeTimer;
    boolean gameOver = false, gameStarted = false;
    double score = 0;
    JButton startButton, retryButton;
    String playerName;

    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("img/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("img/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("img/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("img/bottompipe.png")).getImage();

        bird = new Bird(birdImg);

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 - 25, 100, 50);
        startButton.addActionListener(e -> startGame());
        setLayout(null);
        add(startButton);

        retryButton = new JButton("Retry");
        retryButton.setFont(new Font("Arial", Font.BOLD, 20));
        retryButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 + 30, 100, 50);
        retryButton.setVisible(false);
        retryButton.addActionListener(e -> restartGame());
        add(retryButton);

        gameLoop = new Timer(1000 / 60, this);
        placePipeTimer = new Timer(1500, e -> placePipes());
    }

    void startGame() {
        playerName = JOptionPane.showInputDialog(this, "Masukkan Nama:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Pemain";
        }
        gameStarted = true;
        remove(startButton);
        repaint();
        gameLoop.start();
        placePipeTimer.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) g.drawString("Game Over: " + (int) score, 10, 35);
        else g.drawString(String.valueOf((int) score), 10, 35);
    }

    public void move() {
        if (!gameStarted) return;
        velocityY += gravity;
        bird.y = Math.max(bird.y + velocityY, 0);
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }
            if (collision(bird, pipe)) gameOver = true;
        }
        if (bird.y > boardHeight) gameOver = true;
        if (gameOver) {
            retryButton.setVisible(true);
            System.out.println(playerName + " score: " + (int) score);
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            move();
            repaint();
            if (gameOver) {
                placePipeTimer.stop();
                gameLoop.stop();
            }
        }
    }

    void restartGame() {
        playerName = JOptionPane.showInputDialog(this, "Masukkkan Nama:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Pemain";
        }
        retryButton.setVisible(false);
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        placePipeTimer.start();
    }

    @Override
    //Spasi dan UP untuk lompat
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            if (!gameStarted) {
                startGame();
            }
            if (!gameOver) {
                velocityY = -9;
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird Java");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.setSize(game.boardWidth, game.boardHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
