import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class InteractiveFillableColorGridGUI extends JFrame {

    private static final int GRID_SIZE = 8;
    private JPanel[][] gridBoxes;
    private boolean isMousePressed = false;
    private boolean isScribbling = false;
    private List<List<Point>> player1ScribblePaths; // Player 1's paths (scribbles) for each panel
    private List<List<Point>> player2ScribblePaths; // Player 2's paths (scribbles) for each panel
    private Color currentPlayerColor;

    public InteractiveFillableColorGridGUI() {
        setTitle("Interactive Fillable Color Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        gridBoxes = new JPanel[GRID_SIZE][GRID_SIZE];
        player1ScribblePaths = new ArrayList<>();
        player2ScribblePaths = new ArrayList<>();
        currentPlayerColor = Color.RED; // Player 1 starts with red color

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int rowIndex = row;
                final int colIndex = col;
                gridBoxes[row][col] = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        int pathIndex = rowIndex * GRID_SIZE + colIndex;
                        List<Point> scribblePath;
                        if (currentPlayerColor == Color.RED) {
                            scribblePath = player1ScribblePaths.size() > pathIndex ? player1ScribblePaths.get(pathIndex) : null;
                        } else {
                            scribblePath = player2ScribblePaths.size() > pathIndex ? player2ScribblePaths.get(pathIndex) : null;
                        }

                        if (isScribbling && scribblePath != null) {
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setColor(currentPlayerColor);
                            for (Point point : scribblePath) {
                                g2d.fillRect(point.x, point.y, 3, 3);
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
            JPanel panel = (JPanel) e.getComponent();
            int pathIndex = row * GRID_SIZE + col;
            if (pathIndex >= getCurrentScribblePaths().size()) {
                getCurrentScribblePaths().add(new ArrayList<>());
            }
            fillBox(panel);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isMousePressed = false;
            isScribbling = false;
            JPanel panel = (JPanel) e.getComponent();
            int pathIndex = row * GRID_SIZE + col;
            List<Point> scribblePath = getCurrentScribblePaths().get(pathIndex);

            // Calculate covered area
            double coveredArea = calculateCoveredArea(scribblePath);

            // Check if the area is more than 50%
            if (coveredArea >= 0.5) {
                // Mark the box as taken over by the current player
                panel.setBackground(currentPlayerColor);
                // Remove the scribble path, as the box is taken over
                scribblePath.clear();
                // Set isFilled to true, so other players cannot scribble in this box
                panel.putClientProperty("isFilled", true);
            } else {
                // If less than 50%, reset the box to white
                panel.setBackground(Color.WHITE);
                // Clear the scribble path for the box
                scribblePath.clear();
            }

            // Repaint the panel to update the change
            panel.repaint();

            switchPlayer();
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
            int pathIndex = panel.getParent().getComponentZOrder(panel);
            if (pathIndex >= getCurrentScribblePaths().size()) {
                getCurrentScribblePaths().add(new ArrayList<>());
            }
            getCurrentScribblePaths().get(pathIndex).add(new Point(mouseX, mouseY));
            panel.repaint(); // Repaint the panel to update the scribbles
        }
    }

    private List<List<Point>> getCurrentScribblePaths() {
        return currentPlayerColor == Color.RED ? player1ScribblePaths : player2ScribblePaths;
    }

    private double calculateCoveredArea(List<Point> scribblePath) {
        if (scribblePath.isEmpty()) {
            return 0.0;
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : scribblePath) {
            int x = point.x;
            int y = point.y;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int boxArea = gridBoxes[0][0].getWidth() * gridBoxes[0][0].getHeight();
        int coveredArea = (maxX - minX + 1) * (maxY - minY + 1);

        double coveragePercentage = (double) coveredArea / boxArea;
        return coveragePercentage;
    }

    private void switchPlayer() {
        currentPlayerColor = currentPlayerColor == Color.RED ? Color.BLUE : Color.RED;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InteractiveFillableColorGridGUI::new);
    }
}
