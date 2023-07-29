import javax.swing.*;
import java.awt.*;

public class StartPage {

    public StartPage() {
        // Set up the main frame
        JFrame frame = new JFrame("Deny and Conquer");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Start button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Roboto", Font.PLAIN, 24));

        // Create a panel to hold the button with padding
        JPanel buttonPnl = new JPanel();
        int padding = 250;
        buttonPnl.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        // Add the start button to the panel
        buttonPnl.add(startButton);

        // Create the main panel
        JPanel mainPnl = new JPanel(new BorderLayout());

        // Add the main panel to the frame
        frame.add(mainPnl);
        
        // Add the button panel to the main panel
        mainPnl.add(buttonPnl, BorderLayout.CENTER);

        frame.setVisible(true);

        // Center the frame on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - frame.getWidth()) / 2;
        int centerY = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(centerX, centerY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StartPage();
        });
    }
}
