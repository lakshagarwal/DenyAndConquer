import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoiningScreen extends JFrame {
    public JoiningScreen() {
        // Set window properties
        setTitle("Game Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800); // Set the size of the window
        setLocationRelativeTo(null); // Center the window on the screen

        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout to position components

        // Create four text labels
        JLabel label1 = new JLabel("Label 1");
        JLabel label2 = new JLabel("Label 2");
        JLabel label3 = new JLabel("Label 3");
        JLabel label4 = new JLabel("Label 4");

        // Create the button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close Startgame
                dispose();
                // Open DivideAndConquerGame 
                SwingUtilities.invokeLater(() -> {
                    new InteractiveFillableColorGridGUI();
                });
            }
        });

        // Add the components to the panel using GridBagConstraints to position them
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER; // Center the label vertically
        panel.add(label1, constraints);

        constraints.gridy = 1;
        panel.add(label2, constraints);

        constraints.gridy = 2;
        panel.add(label3, constraints);

        constraints.gridy = 3;
        panel.add(label4, constraints);

        constraints.gridy = 4;
        constraints.gridwidth = 2; // Make the button span two columns
        panel.add(startButton, constraints);

        // Add the panel to the frame
        add(panel);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JoiningScreen());
    }
}
