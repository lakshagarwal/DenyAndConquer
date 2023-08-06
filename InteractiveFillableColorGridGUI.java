import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class InteractiveFillableColorGridGUI extends JFrame {
    private static final int GRID_SIZE = 8;
    private JPanel[][] gridBoxes;
    private boolean isMousePressed = false;
    private boolean isScribbling = false;
    private int[][] pixelsFilled;
    private TCPClient client;
    private Color userColor;
    private boolean[][] cellClaimed = new boolean[GRID_SIZE][GRID_SIZE];

    public InteractiveFillableColorGridGUI(TCPClient client, String color) {
        this.client = client;
        this.userColor = getColorForOwner(color);
        setTitle("Deny and Conquer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        gridBoxes = new JPanel[GRID_SIZE][GRID_SIZE];
        pixelsFilled = new int[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int rowIndex = row;
                final int colIndex = col;
                gridBoxes[row][col] = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (pixelsFilled[rowIndex][colIndex] > 0) {
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setColor(userColor);
                            int panelWidth = getWidth();
                            int panelHeight = getHeight();
                            for (int i = 0; i < pixelsFilled[rowIndex][colIndex]; i++) {
                                int randX = (int) (Math.random() * panelWidth);
                                int randY = (int) (Math.random() * panelHeight);
                                g2d.fillRect(randX, randY, 3, 3);
                            }
                        }
                    }
                };
                gridBoxes[row][col].setBackground(Color.WHITE); 
                gridBoxes[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridBoxes[row][col].setLayout(null);
                gridBoxes[row][col].addMouseListener(new ColorMouseListener(rowIndex, colIndex));
                gridBoxes[row][col].addMouseMotionListener(new ColorMouseMotionListener(rowIndex, colIndex));
                add(gridBoxes[row][col]);
            }
        }

        setSize(800, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class ColorMouseListener extends MouseAdapter {
        private final int row;
        private final int col;

        public ColorMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (cellClaimed[row][col]) return;  // Return early if cell has been claimed

            isMousePressed = true;
            isScribbling = true;
            if (isScribbling) {
                pixelsFilled[row][col] = 0;
            }
            JPanel panel = (JPanel) e.getComponent();
            
            client.lockBox(row, col);
            fillBox(panel, row, col);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (cellClaimed[row][col]) return;  // Return early if cell has been claimed

            isMousePressed = false;
            isScribbling = false;
            JPanel panel = (JPanel) e.getComponent();
            int totalPixels = panel.getWidth() * panel.getHeight();
            int filledPixels = pixelsFilled[row][col];
            double percentageFilled = (double) filledPixels / totalPixels * 100;

            if (percentageFilled >= 50) {
                client.claimBox(row, col);
                fillBox(panel, row, col);
            } else {
                client.unlockBox(row, col);
                clearBox(panel, row, col);
            }
        }
    }

    private class ColorMouseMotionListener extends MouseAdapter {
        private final int row;
        private final int col;

        public ColorMouseMotionListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (cellClaimed[row][col]) return;  // Return early if cell has been claimed

            if (isMousePressed && isScribbling) {
                JPanel panel = (JPanel) e.getComponent();
                fillBox(panel, row, col);
            }
        }
    }

    private void fillBox(JPanel panel, int row, int col) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        int mouseX = panel.getMousePosition().x;
        int mouseY = panel.getMousePosition().y;

        if (mouseX >= 0 && mouseY >= 0 && mouseX < panelWidth && mouseY < panelHeight) {
            panel.getGraphics().fillRect(mouseX, mouseY, 8, 8);
            pixelsFilled[row][col] += 64; // Increment by the area of the small box
            panel.repaint();
        }
    }

    private void clearBox(JPanel panel, int row, int col) {
        panel.setBackground(Color.WHITE);
        pixelsFilled[row][col] = 0;  // Reset the pixel count for the box
        panel.repaint();
    }

    public void lockCell(int cellNumber) {
        int row = cellNumber / GRID_SIZE;
        int col = cellNumber % GRID_SIZE;
        gridBoxes[row][col].setBackground(Color.GRAY);
    }

    public void unlockCell(int cellNumber) {
        int row = cellNumber / GRID_SIZE;
        int col = cellNumber % GRID_SIZE;
        gridBoxes[row][col].setBackground(Color.WHITE);
    }

    public void claimCell(int cellNumber, String owner) {
        int row = cellNumber / GRID_SIZE;
        int col = cellNumber % GRID_SIZE;
        Color ownerColor = getColorForOwner(owner);
        gridBoxes[row][col].setBackground(ownerColor);
        cellClaimed[row][col] = true;  // Mark the cell as claimed
    }

    private Color getColorForOwner(String owner) {
        switch (owner) {
            case "Red": return Color.RED;
            case "Blue": return Color.BLUE;
            case "Green": return Color.GREEN;
            case "Yellow": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }
}
