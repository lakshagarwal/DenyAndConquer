import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class WinnerScreen {
    private static void Winner_Screen() {
        JFrame frame = new JFrame("Deny And Conquer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        // Create and add a label in the center of the frame
        JLabel label = new JLabel("Label", JLabel.CENTER);
        frame.add(label, BorderLayout.CENTER);

        // Create and add an exit button at the bottom center of the frame
        JButton exitButton = new JButton("Exit");

        // Add ActionListener to the exit button to close the application when clicked
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exitButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> Winner_Screen());
    }
}
