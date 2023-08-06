import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class InteractiveFillableColorGridGUI extends JFrame {
    // This class represents the GUI for the interactive color grid game.

    private static final int GRID_SIZE = 8;
    private JPanel[][] gridBoxes;
    private boolean isMousePressed = false;
    private boolean isScribbling = false;
    private int[][] pixelsFilled;
    private TCPClient client;
    private Color userColor;
    private boolean[][] cellClaimed = new boolean[GRID_SIZE][GRID_SIZE];

    // Constructor initializes the GUI with the given TCPClient instance and the user's color.
    public InteractiveFillableColorGridGUI(TCPClient client, String color) {
        this.client = client;
        this.userColor = getColorForOwner(color);
        setTitle("Deny and Conquer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        // Create a 2D array of JPanels to represent the grid.
        gridBoxes = new JPanel[GRID_SIZE][GRID_SIZE];
        pixelsFilled = new int[GRID_SIZE][GRID_SIZE];

        // Initialize the grid by adding JPanels to the JFrame.
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int rowIndex = row;
                final int colIndex = col;
                gridBoxes[row][col] = new JPanel() {
                    // Override paintComponent to fill the panel with random small squares based on pixelsFilled array.

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

                // Set initial properties for the grid boxes.
                gridBoxes[row][col].setBackground(Color.WHITE);
                gridBoxes[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridBoxes[row][col].setLayout(null);

                // Add mouse listeners to each grid box for interactivity.
                gridBoxes[row][col].addMouseListener(new ColorMouseListener(rowIndex, colIndex));
                gridBoxes[row][col].addMouseMotionListener(new ColorMouseMotionListener(rowIndex, colIndex));
                add(gridBoxes[row][col]);
            }
        }

        setSize(800, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Inner class to handle mouse events on the grid boxes.
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

            // When the mouse is pressed, update the mouse state and lock the current box (cell).
            isMousePressed = true;
            isScribbling = true;
            if (isScribbling) {
                pixelsFilled[row][col] = 0;
            }
            JPanel panel = (JPanel) e.getComponent();

            // Send a message to the server to lock the box.
            client.lockBox(row, col);
            fillBox(panel, row, col);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (cellClaimed[row][col]) return;  // Return early if cell has been claimed

            // When the mouse is released, update the mouse state and check if the box should be claimed or unlocked.
            isMousePressed = false;
            isScribbling = false;
            JPanel panel = (JPanel) e.getComponent();
            int totalPixels = panel.getWidth() * panel.getHeight();
            int filledPixels = pixelsFilled[row][col];
            double percentageFilled = (double) filledPixels / totalPixels * 100;

            if (percentageFilled >= 50) {
                // If more than 50% of the box is filled, send a message to the server to claim the box.
                client.claimBox(row, col);
                fillBox(panel, row, col);
            } else {
                // Otherwise, send a message to the server to unlock the box.
                client.unlockBox(row, col);
                clearBox(panel, row, col);
            }
        }
    }

    // Inner class to handle mouse motion events on the grid boxes.
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

            // When the mouse is dragged, update the box (cell) if the mouse is pressed and scribbling is allowed.
            if (isMousePressed && isScribbling) {
                JPanel panel = (JPanel) e.getComponent();
                fillBox(panel, row, col);
            }
        }
    }

    // Helper method to fill the box (cell) with random small squares and update the pixelsFilled array.
    private void fillBox(JPanel panel, int row, int col) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        int mouseX = panel.getMousePosition().x;
        int mouseY = panel.getMousePosition().y;

        if (mouseX >= 0 && mouseY >= 0 && mouseX < panelWidth && mouseY < panelHeight) {
            panel.getGraphics().fillRect(mouseX, mouseY, 8, 8);
            pixelsFilled[row][col] += 64; // Increment by the area of the small square (8x8)
            panel.repaint();
        }
    }

    // Helper method to clear the box (cell) and reset the pixelsFilled array.
    private void clearBox(JPanel panel, int row, int col) {
        panel.setBackground(Color.WHITE);
        pixelsFilled[row][col] = 0;  // Reset the pixel count for the box
        panel.repaint();
    }

    // Method to update the GUI when a cell is locked by another player.
    public void lockCell(int cellNumber) {
        int row = cellNumber / GRID_SIZE;
        int col = cellNumber % GRID_SIZE;
        gridBoxes[row][col].setBackground(Color.GRAY);
    }

    // Method to update the GUI when a cell is unlocked by the previous owner.
    public void unlockCell(int cellNumber) {
        int row = cellNumber / GRID_SIZE;
        int col = cellNumber % GRID_SIZE;
        gridBoxes[row][col].setBackground(Color.WHITE);
    }

    // Method to update the GUI when a cell is claimed by a player.
    public void claimCell(int cellNumber, String owner) {
    int row = cellNumber / GRID_SIZE;
    int col = cellNumber % GRID_SIZE;
    Color ownerColor = getColorForOwner(owner);
    gridBoxes[row][col].setBackground(ownerColor);
    cellClaimed[row][col] = true;  // Mark the cell as claimed
}

// Helper method to get the Color object corresponding to a player's color string.
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
