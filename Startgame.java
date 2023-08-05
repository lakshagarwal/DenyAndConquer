import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Startgame extends JFrame {
    public Startgame() {
        //title
        setTitle("Group 22 Game");

        //size
        setSize(800, 800);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        //label for the title
        JLabel titleLabel = new JLabel("Welcome, Let's Play!!!!!");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // button to start the game
        JButton startButtonServer = new JButton("Join Game as Server");
        startButtonServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close Startgame
                dispose();
                // Open DivideAndConquerGame 
                SwingUtilities.invokeLater(() -> {
                    new JoiningScreen_Server();
                });
            }
        });

        JButton startButtonClient = new JButton("Join Game as Client");
        startButtonClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close Startgame
                dispose();
                // Open DivideAndConquerGame 
                SwingUtilities.invokeLater(() -> {
                    new JoiningScreen();
                });
            }
        });

        //button to exit the game
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButtonServer);
        buttonPanel.add(startButtonClient);
        buttonPanel.add(exitButton);

        // Add components to the frame using the BorderLayout
        add(titleLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        // Create the start page of the game
        SwingUtilities.invokeLater(() -> {
            new Startgame();
        });
    }
}
