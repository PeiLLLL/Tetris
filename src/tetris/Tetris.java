package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {
    // Game states
    private int state;
    public static final int RUNNING = 0;
    public static final int PAUSE = 1;
    public static final int GAME_OVER = 2;

    // Display variables
    private int score;
    private int lines;
    private int level;
    private Timer timer;

    // State variables
    private long index = 0;
    private int speed = 40;

    // Game wall dimensions
    public static final int ROWS = 20;
    public static final int COLS = 10;
    private Cell[][] wall = new Cell[ROWS][COLS];
    private Tetromino tetromino;
    private Tetromino nextOne;

    // Images
    private static BufferedImage background;
    public static BufferedImage T;
    public static BufferedImage S;
    public static BufferedImage Z;
    public static BufferedImage L;
    public static BufferedImage J;
    public static BufferedImage O;
    public static BufferedImage I;
    public static BufferedImage gameOver;
    public static BufferedImage pause;

    // Static block to load images
    static {
        try {
            background = ImageIO.read(Tetris.class.getResource("tetris.png"));
            T = ImageIO.read(Tetris.class.getResource("T.png"));
            S = ImageIO.read(Tetris.class.getResource("S.png"));
            Z = ImageIO.read(Tetris.class.getResource("Z.png"));
            J = ImageIO.read(Tetris.class.getResource("J.png"));
            L = ImageIO.read(Tetris.class.getResource("L.png"));
            O = ImageIO.read(Tetris.class.getResource("O.png"));
            I = ImageIO.read(Tetris.class.getResource("I.png"));
            gameOver = ImageIO.read(Tetris.class.getResource("game-over.png"));
            pause = ImageIO.read(Tetris.class.getResource("pause.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to start the game
    public void action() {
        // Create new tetrominoes
        tetromino = Tetromino.randomOne();
        nextOne = Tetromino.randomOne();
        state = RUNNING;

        // Key listener for game controls
        KeyListener l = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (state) {
                    case RUNNING:
                        processRunningKey(key);
                        break;
                    case PAUSE:
                        processPauseKey(key);
                        break;
                    case GAME_OVER:
                        processGameOverKey(key);
                        break;
                }
                Tetris.this.repaint();
            }
        };

        this.addKeyListener(l);
        this.setFocusable(true);
        this.requestFocus();

        // Timer to control game speed and actions
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                // Adjust speed based on score
                speed = 30 - (score / 50);// Calculate speed: higher score means faster speed
                speed = speed <= 1 ? 1 : speed;// Ensure speed does not go below 1
                level = 31 - speed;
                if (index % speed == 0) {
                    if (state == RUNNING) {
                        softDropAction();
                    }
                }
                index++;
                repaint();
            }
        };
        timer.schedule(task, 10, 10);
    }

    // Handle key events when the game is over
    protected void processGameOverKey(int key) {
        switch (key) {
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_S:
                wall = new Cell[ROWS][COLS];
                tetromino = Tetromino.randomOne();
                nextOne = Tetromino.randomOne();
                score = 0;
                lines = 0;
                index = 0;
                state = RUNNING;
        }
    }

    // Handle key events when the game is paused
    protected void processPauseKey(int key) {
        switch (key) {
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_C:
                state = RUNNING;
                index = 1;
                break;
        }
    }

    // Handle key events when the game is running
    protected void processRunningKey(int key) {
        switch (key) {
            case KeyEvent.VK_RIGHT:
                moveRightAction();
                break;
            case KeyEvent.VK_LEFT:
                moveLeftAction();
                break;
            case KeyEvent.VK_DOWN:
                softDropAction();
                break;
            case KeyEvent.VK_UP:
                rotateRightAction();
                break;
            case KeyEvent.VK_Z:
                rotateLeftAction();
                break;
            case KeyEvent.VK_SPACE:
                hardDropAction();
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_P:
                state = PAUSE;
                break;
        }
    }

    // Override the paint method to customize drawing
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        g.translate(15, 15); // Shift coordinate system
        paintWall(g);
        this.paintTetromino(g);
        paintNextOne(g);
        paintScore(g);
        paintState(g);
    }

    // Paint the current game state (pause or game over)
    private void paintState(Graphics g) {
        switch (state) {
            case PAUSE:
                g.drawImage(pause, -15, -15, null);
                break;
            case GAME_OVER:
                g.drawImage(gameOver, -15, -15, null);
                break;
        }
    }

    // Paint the score, lines, and level
//    private void paintScore(Graphics g) {
//        int x = 292;
//        int y = 162;
//        int color = 0xffffff;
//        g.setColor(new Color(color));
//        Font f = new Font(Font.SERIF, Font.BOLD, 30);
//        g.setFont(f);
//        g.drawString("Score:" + score, x, y);
//        y += 56;
//        g.drawString("Lines:" + lines, x, y);
//        y += 56;
//        g.drawString("Level:" + level, x, y);
//        x = 290;
//        y = 160;
//        color = 0x667799;
//        g.setColor(new Color(color));
//        g.setFont(f);
//        g.drawString("Score:" + score, x, y);
//        y += 56;
//        g.drawString("Lines:" + lines, x, y);
//        y += 56;
//        g.drawString("Level:" + level, x, y);
//    }

    private void paintScore(Graphics g) {
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        int x = windowWidth / 2 + 130; // Center horizontally then move
        int y = windowHeight / 2 - 50; // Center vertically
        int offset = 56; // Offset between lines

        // Set the text color to black
        g.setColor(Color.BLACK);
        Font f = new Font(Font.SERIF, Font.PLAIN, 30);
        g.setFont(f);

        // Draw the score, lines, and level text centered
        String scoreText = "SCORE: " + score;
        String linesText = "LINES: " + lines;
        String levelText = "LEVEL: " + level;

        // Get the width of the text to center it
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        int linesWidth = g.getFontMetrics().stringWidth(linesText);
        int levelWidth = g.getFontMetrics().stringWidth(levelText);

        g.drawString(scoreText, x - scoreWidth / 2, y - offset);
        g.drawString(linesText, x - linesWidth / 2, y);
        g.drawString(levelText, x - levelWidth / 2, y + offset);
    }

    
    // Paint the next tetromino piece
    private void paintNextOne(Graphics g) {
        if (nextOne == null)
            return;
        Cell[] cells = nextOne.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            int row = c.getRow() + 1;
            int col = c.getCol() + 10;
            int x = col * CELL_SIZE;
            int y = row * CELL_SIZE;
            g.drawImage(c.getImage(), x, y, null);
        }
    }

    // Paint the current falling tetromino piece
    private void paintTetromino(Graphics g) {
        if (tetromino == null)
            return;
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            int col = c.getCol();
            int row = c.getRow();
            int x = col * CELL_SIZE;
            int y = row * CELL_SIZE;
            g.drawImage(c.getImage(), x, y, null);
        }
    }

    public static final int CELL_SIZE = 26;

    // Paint the game wall
    private void paintWall(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Cell cell = wall[row][col];
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                if (cell == null) {
                    // g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    // Move the tetromino to the right
    public void moveRightAction() {
        tetromino.moveRight();
        if (outOfBounds() || concide()) {
            tetromino.moveLeft();
        }
    }

    // Rotate the tetromino to the right
    public void rotateRightAction() {
        tetromino.rotateRight();
        if (outOfBounds() || concide()) {
            tetromino.rotateLeft();
        }
    }

    // Rotate the tetromino to the left
    public void rotateLeftAction() {
        tetromino.rotateLeft();
        if (outOfBounds() || concide()) {
            tetromino.rotateRight();
        }
    }

    // Check if the tetromino collides with the wall
    private boolean concide() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    // Move the tetromino to the left
    public void moveLeftAction() {
        tetromino.moveLeft();
        if (outOfBounds() || concide()) {
            tetromino.moveRight();
        }
    }

    // Check if the tetromino is out of bounds
    private boolean outOfBounds() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            int row = c.getRow();
            int col = c.getCol();
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return true;
            }
        }
        return false;
    }

    // Perform a hard drop
    public void hardDropAction() {
        while (canDrop()) {
            tetromino.softDrop();
        }
        landIntoWall();
        int lines = destroyLines();
        this.lines += lines;
        score += scoreTable[lines];
        if (isGameOver()) {
            state = GAME_OVER;
        } else {
            tetromino = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    private int[] scoreTable = { 0, 10, 50, 80, 200 };

    // Perform a soft drop
    public void softDropAction() {
        if (canDrop()) {
            tetromino.softDrop();
        } else {
            landIntoWall();
            int lines = destroyLines();
            this.lines += lines;
            score += scoreTable[lines];
            if (isGameOver()) {
                state = GAME_OVER;
            } else {
                tetromino = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    // Check if the tetromino can drop further
    private boolean canDrop() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (row == (ROWS - 1)) {
                return false;
            }
        }
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }

    // Land the tetromino into the wall
    private void landIntoWall() {
        Cell[] cells = tetromino.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    // Destroy full lines and return the number of lines destroyed
    private int destroyLines() {
        int lines = 0;
        for (int row = 0; row < ROWS; row++) {
            if (fullCells(row)) {
                deleteRow(row);
                lines++;
            }
        }
        return lines;
    }

    // Delete a row and shift the above rows down
    private void deleteRow(int row) {
        for (int i = row; i >= 1; i--) {
            System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
        }
        Arrays.fill(wall[0], null);
    }

    // Check if a row is full
    private boolean fullCells(int row) {
        Cell[] line = wall[row];
        for (int i = 0; i < line.length; i++) {
            Cell cell = line[i];
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    // Check if the game is over
    private boolean isGameOver() {
        Cell[] cells = nextOne.cells;
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    // Main method to start the game
    public static void main(String[] args) {
        // Create the game window
        JFrame frame = new JFrame();
        Tetris tetris = new Tetris();
        frame.add(tetris);
        frame.setSize(525, 550);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Tetris - Peiwen Liu");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        tetris.action();
    }
}
