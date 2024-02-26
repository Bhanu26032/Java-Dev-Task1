import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

class SnakeGamePanel extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_SIZE = 20;
    private final int TILE_SIZE = 20;
    private final int DELAY = 150;

    private LinkedList<Point> snake;
    private Point food;
    private Timer timer;
    private char direction;

    private int score;
    private boolean gameOver;

    public SnakeGamePanel() {
        setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        score = 0;
        initializeGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initializeGame() {
        snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        food = generateFood();
        direction = 'R'; // Start moving to the right
    }

   private Point generateFood() {
    Random random = new Random();
    int x, y;

    do {
        x = random.nextInt(BOARD_SIZE);
        y = random.nextInt(BOARD_SIZE);
    } while (snake.contains(new Point(x, y)) || isFoodAtEdge(x, y));

    return new Point(x, y);
}

private boolean isFoodAtEdge(int x, int y) {
    return x == 0 || x == BOARD_SIZE-1 || y == 0 || y == BOARD_SIZE - 1;
}





    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead;

        switch (direction) {
            case 'U':
                newHead = new Point(head.x, (head.y - 1 + BOARD_SIZE) % BOARD_SIZE);
                break;
            case 'D':
                newHead = new Point(head.x, (head.y + 1) % BOARD_SIZE);
                break;
            case 'L':
                newHead = new Point((head.x - 1 + BOARD_SIZE) % BOARD_SIZE, head.y);
                break;
            case 'R':
                newHead = new Point((head.x + 1) % BOARD_SIZE, head.y);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            food = generateFood();
            score++;
        } else {
            snake.removeLast();
        }
    }

    private boolean checkCollision() {
        Point head = snake.getFirst();

        // Check collision with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }

        // Check collision with board edges
	if (head.x == 0) return true;
        if (head.x == 0) return true;
	if ( head.x == BOARD_SIZE) return true;
	
	if (head.y == 0 ) return true;
	if (head.y == BOARD_SIZE) {
            return true;
        }

        return false;
    }
/*
    private void drawSnake(Graphics g) {
        g.setColor(Color.RED);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
*/
private void drawSnake(Graphics g) {
    g.setColor(Color.RED);
    for (Point p : snake) {
        // Fill the tile with a green color
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw a border around the tile
        g.setColor(Color.GREEN);
        g.drawRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
}

private void drawFood(Graphics g) {
    g.setColor(Color.YELLOW);
    
    // Fill the food tile with a yellow color
    g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

    // Draw a border around the food tile
    g.setColor(Color.BLACK);
    g.drawRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
}

    private void drawScore(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score:" + score, 10, 20);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 30));

        // Draw "Game Over" message
        String gameOverMessage = "Game Over";
        int gameOverWidth = g.getFontMetrics().stringWidth(gameOverMessage);
        g.drawString(gameOverMessage, (getWidth() - gameOverWidth) / 2, BOARD_SIZE * TILE_SIZE / 2);

        // Draw score below the "Game Over" message
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String scoreMessage = "Your Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreMessage);
        g.drawString(scoreMessage, (getWidth() - scoreWidth) / 2, BOARD_SIZE * TILE_SIZE / 2 + 30);

        // Draw "Start Game" message if the game is over
        if (gameOver) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String startGameMessage = "Press Enter to Start Game";
            int startGameWidth = g.getFontMetrics().stringWidth(startGameMessage);
            g.drawString(startGameMessage, (getWidth() - startGameWidth) / 2, BOARD_SIZE * TILE_SIZE / 2 + 60);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!checkCollision()) {
            drawSnake(g);
            drawFood(g);
            drawScore(g);
        } else {
            drawGameOver(g);
            timer.stop();
        }

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSnake();
        repaint();
        if (checkCollision()) {
            timer.stop();
            gameOver = true;
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameOver) {
                // Restart the game
                score = 0;
                initializeGame();
                timer.restart();
                gameOver = false;
                repaint();
            }
        } else {
            // Handle arrow key inputs for snake movement
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

public class SnakeGameFrame extends JFrame {
    public SnakeGameFrame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new SnakeGamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SnakeGameFrame().setVisible(true);
        });
    }
}
