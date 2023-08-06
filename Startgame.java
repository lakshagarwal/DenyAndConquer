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

public class Startgame extends JFrame {
    public Startgame() {
        setTitle("Group 22 Game");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome, Let's Play!!!!!");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton startButtonServer = new JButton("Join Game as Server");
        startButtonServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        TCPClient client = new TCPClient();
                        client.initializeGameGui(); // Initialize the GUI here
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
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButtonServer);
        buttonPanel.add(startButtonClient);
        buttonPanel.add(exitButton);

        add(titleLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Startgame();
        });
    }
}
