import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

public class TCPClient {

    // TCPClient class is responsible for handling client-side communication with the server.

    private Socket socket;
    private PrintWriter writer;
    public String userColor;
    private InteractiveFillableColorGridGUI gameGui;

    // Constructor initializes the client by establishing a connection to the server and creating a PrintWriter.
    public TCPClient() throws IOException {
        this.socket = new Socket("localhost", 7070);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    // Starts the threads for reading and writing data to the server.
    public void startThreads() {
        new ReadThread(socket, this).start(); // Pass this TCPClient instance
        new WriteThread(socket).start();
    }

    // Initializes the GUI for the game with the user's color.
    public void initializeGameGui(String color) {
        gameGui = new InteractiveFillableColorGridGUI(this, color);
    }

    // Sends a message to the server to lock a specific box identified by its row and column.
    public void lockBox(int row, int col) {
        sendMessage("LOCK(" + (row * 8 + col) + ")");
    }

    // Sends a message to the server to claim a specific box identified by its row and column.
    public void claimBox(int row, int col) {
        sendMessage("CLAIM(" + (row * 8 + col) + ")");
    }

    // Sends a message to the server to unlock a specific box identified by its row and column.
    public void unlockBox(int row, int col) {
        sendMessage("UNLOCK(" + (row * 8 + col) + ")");
    }

    // Sends a message to the server.
    public void sendMessage(String message) {
        writer.println(message);
    }

    // Updates the GUI to lock a specific cell identified by its cell number.
    public void lockCellInGui(int cellNumber) {
        gameGui.lockCell(cellNumber);
    }

    // Updates the GUI to unlock a specific cell identified by its cell number.
    public void unlockCellInGui(int cellNumber) {
        gameGui.unlockCell(cellNumber);
    }

    // Updates the GUI to claim a specific cell identified by its cell number with the given owner color.
    public void claimCellInGui(int cellNumber, String owner) {
        gameGui.claimCell(cellNumber, owner);
    }

    // Main method to create a new TCPClient instance and start the client.
    public static void main(String[] args) throws IOException {
        new TCPClient();
    }
}

// ReadThread class handles reading data from the server.
class ReadThread extends Thread {
    private BufferedReader reader;
    private TCPClient client; // added this to store reference to TCPClient

    // Constructor initializes the ReadThread with the socket and a reference to the TCPClient instance.
    public ReadThread(Socket socket, TCPClient client) {
        this.client = client; // store the TCPClient instance
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // The run method listens for incoming messages from the server.
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println(response);

                // Parse the server response and take appropriate actions based on the message received.

                if (response.startsWith("YOUR_COLOR")) {
                    // The server informs the client about its color in the game.
                    // The color information is extracted from the message and used to initialize the GUI.
                    String color = response.substring(11, response.length() - 1);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        client.userColor = color;
                        client.initializeGameGui(color); // Initialize the GUI here
                    });
                } else if (response.startsWith("LOCKED")) {
                    // The server informs that a box has been locked by another player.
                    // The cell number is extracted from the message and used to lock the cell in the GUI.
                    String cellStr = response.substring(7, response.length() - 1);
                    int cellNumber = Integer.parseInt(cellStr);
                    client.lockCellInGui(cellNumber);
                } else if (response.startsWith("UNLOCKED")) {
                    // The server informs that a box has been unlocked by the previous owner.
                    // The cell number is extracted from the message and used to unlock the cell in the GUI.
                    String cellStr = response.substring(9, response.length() - 1);
                    int cellNumber = Integer.parseInt(cellStr);
                    client.unlockCellInGui(cellNumber);
                } else if (response.startsWith("CLAIMED")) {
                    // The server informs that a box has been claimed by a player.
                    // The cell number and owner information are extracted from the message and used to update the GUI.
                    String[] parts = response.split(", ");
                    String cellStr = parts[0].substring(8);
                    String owner = parts[1].substring(0, parts[1].length() - 1);
                    int cellNumber = Integer.parseInt(cellStr);
                    client.claimCellInGui(cellNumber, owner);
                } else if (response.startsWith("GAME_OVER")) {
                    // The server informs that the game is over, and the result is provided.
                    // A message dialog is shown with the result, and the client exits the application.
                    String result = response.substring(10, response.length() - 1);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, result, "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    });
                }

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}

// WriteThread class handles writing data to the server.
class WriteThread extends Thread {
    private PrintWriter writer;

    // Constructor initializes the WriteThread with the socket.
    public WriteThread(Socket socket) {
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // The run method listens for input from the console and sends it to the server.
    public void run() {
        Console console = System.console();
        String text;

        while (true) {
            text = console.readLine("");
            writer.println(text);
        }
    }
}
