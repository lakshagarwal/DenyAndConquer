import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InteractiveFillableColorGridGUI extends JFrame {

    private static final int GRID_SIZE = 8;
    private JPanel[][] gridBoxes;
    private boolean isMousePressed = false;
    private boolean isScribbling = false;
    private int[][] pixelsFilled; // Counter for each box

    public InteractiveFillableColorGridGUI() {
        setTitle("Interactive Fillable Color Grid");
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
                            g2d.setColor(Color.BLACK);
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
                gridBoxes[row][col].setBackground(Color.WHITE); // Set initial color to white
                gridBoxes[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridBoxes[row][col].setLayout(null); // Set layout to null for precise control
                gridBoxes[row][col].addMouseListener(new ColorMouseListener(rowIndex, colIndex));
                gridBoxes[row][col].addMouseMotionListener(new ColorMouseMotionListener(rowIndex, colIndex));
                add(gridBoxes[row][col]);
            }
        }

        setSize(800, 800); // Set the window size to 800x800
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
            isMousePressed = true;
            isScribbling = true;
            if (isScribbling) {
                pixelsFilled[row][col] = 0;
            }
            JPanel panel = (JPanel) e.getComponent();
            fillBox(panel);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isMousePressed = false;
            isScribbling = false;
            JPanel panel = (JPanel) e.getComponent();
            int totalPixels = panel.getWidth() * panel.getHeight();
            int filledPixels = pixelsFilled[row][col];
            double percentageFilled = (double) filledPixels / totalPixels;
            if (percentageFilled > 0.5) {
                fillEntireBox(panel); // Fill the box if the percentage is more than 50%
            } else {
                clearBox(panel); // Clear the box if the percentage is less than 50%
            }
            pixelsFilled[row][col] = 0; // Reset the pixel count for the next scribble
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
            if (isMousePressed && isScribbling) {
                JPanel panel = (JPanel) e.getComponent();
                fillBox(panel);
            }
        }
    }

    private void fillBox(JPanel panel) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        int mouseX = panel.getMousePosition().x;
        int mouseY = panel.getMousePosition().y;

        if (mouseX >= 0 && mouseY >= 0 && mouseX < panelWidth && mouseY < panelHeight) {
            panel.getGraphics().fillRect(mouseX, mouseY, 5, 5); // Larger stroke width for crayon-like effect
            pixelsFilled[panel.getParent().getComponentZOrder(panel)][panel.getComponentZOrder(panel)]++;
            panel.repaint(); // Repaint the panel to update the scribbles
        }
    }

    private void fillEntireBox(JPanel panel) {
        panel.setBackground(Color.BLACK); // Change the color to be filled with
        panel.repaint(); // Repaint the panel to fill the entire box with black
    }

    private void clearBox(JPanel panel) {
        panel.setBackground(Color.WHITE); // Change the color to be cleared
        panel.repaint(); // Repaint the panel to clear the box
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InteractiveFillableColorGridGUI());
    }
}
