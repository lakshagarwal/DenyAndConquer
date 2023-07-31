import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DivideAndConquerGame extends JFrame {
    private static final int BOARD_SIZE = 8;
    private JPanel gameBoardPanel;
    private JButton[][] squares;
    private Color[] playerColors = {Color.RED, Color.BLUE}; // Colors for Player 1 and Player 2
    private int currentPlayer; // 1 for Player 1, 2 for Player 2

    public DivideAndConquerGame() {
        initUI();
    }

    private void initUI() {
        setTitle("Divide and Conquer Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameBoardPanel = new GameBoardPanel();
        add(gameBoardPanel, BorderLayout.CENTER);

        currentPlayer = 1; // Player 1

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private class GameBoardPanel extends JPanel {
        public GameBoardPanel() {
            setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
            squares = new JButton[BOARD_SIZE][BOARD_SIZE];

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    squares[row][col] = new JButton();
                    squares[row][col].setOpaque(true); // Set to true to allow background color painting
                    squares[row][col].addActionListener(new SquareClickListener(row, col));
                    add(squares[row][col]);
                }
            }
        }
    }

    private class SquareClickListener implements ActionListener {
        private int row;
        private int col;

        public SquareClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();

            // Check if the square is already claimed
            if (clickedButton.getText().isEmpty()) {
                // Claim the square for the current player
                clickedButton.setText("Player " + currentPlayer);
                clickedButton.setBackground(playerColors[currentPlayer - 1]); // Set the player's color
                // Update the player turn
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DivideAndConquerGame());
    }
}
