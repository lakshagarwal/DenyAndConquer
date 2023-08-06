import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The Startgame class represents the main entry point of the Group 22 Game application.
 * It provides a graphical user interface for the user to choose between starting the game as a server or a client,
 * and also an option to exit the application.
 */
public class Startgame extends JFrame {
    /**
     * Constructor for the Startgame class. Sets up the GUI and handles button actions.
     */
    public Startgame() {
        // Set the title and size of the window
        setTitle("Group 22 Game");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create and customize the title label
        JLabel titleLabel = new JLabel("Welcome, Let's Play!!!!!");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create buttons for different options
        JButton startButtonServer = new JButton("Join Game as Server");
        startButtonServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the Startgame window and start the game as a server
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        ServerAsClient serverClient = new ServerAsClient();
                        serverClient.initializeGameGui(); // Initialize the GUI here
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Error starting server client: " + ex.getMessage());
                    }
                });
            }
        });

        JButton startButtonClient = new JButton("Join Game as Client");
        startButtonClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the Startgame window and start the game as a client
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        TCPClient client = new TCPClient();
                        client.startThreads(); // Start the threads
                    } catch(IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Error starting client: " + ex.getMessage());
                    }
                });
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the application when Exit button is clicked
                System.exit(0);
            }
        });

        // Create a button panel and add buttons to it
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButtonServer);
        buttonPanel.add(startButtonClient);
        buttonPanel.add(exitButton);

        // Add the title label and button panel to the main frame
        add(titleLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Make the window visible
        setVisible(true);
    }

    /**
     * Main method to start the application.
     * It creates an instance of the Startgame class and sets up the GUI.
     */
    public static void main(String[] args) {
        // Start the application by creating a new instance of Startgame
        SwingUtilities.invokeLater(() -> {
            new Startgame();
        });
    }
}
