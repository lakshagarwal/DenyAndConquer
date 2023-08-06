import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

public class TCPClient {

    private Socket socket;
    private PrintWriter writer;
    public String userColor;
    private InteractiveFillableColorGridGUI gameGui;

    public TCPClient() throws IOException {
        this.socket = new Socket("localhost", 7070);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void startThreads() {
        new ReadThread(socket, this).start(); // Pass this TCPClient instance
        new WriteThread(socket).start();
    }

    public void initializeGameGui(String color) {
        gameGui = new InteractiveFillableColorGridGUI(this, color);
    }

    public void lockBox(int row, int col) {
        sendMessage("LOCK(" + (row * 8 + col) + ")");
    }

    public void claimBox(int row, int col) {
        sendMessage("CLAIM(" + (row * 8 + col) + ")");
    }

    public void unlockBox(int row, int col) {
        sendMessage("UNLOCK(" + (row * 8 + col) + ")");
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void lockCellInGui(int cellNumber) {
        gameGui.lockCell(cellNumber);
    }

    public void unlockCellInGui(int cellNumber) {
        gameGui.unlockCell(cellNumber);
    }

    public void claimCellInGui(int cellNumber, String owner) {
        gameGui.claimCell(cellNumber, owner);
    }

    public static void main(String[] args) throws IOException {
        new TCPClient();
    }
}

class ReadThread extends Thread {
    private BufferedReader reader;
    private TCPClient client; // added this to store reference to TCPClient

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

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println(response);

            if (response.startsWith("YOUR_COLOR")) {
                String color = response.substring(11, response.length() - 1);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    client.userColor = color;
                    client.initializeGameGui(color); // Initialize the GUI here
                });
            } else if (response.startsWith("LOCKED")) {
                String cellStr = response.substring(7, response.length() - 1);
                int cellNumber = Integer.parseInt(cellStr);
                client.lockCellInGui(cellNumber);
            } else if (response.startsWith("UNLOCKED")) {
                String cellStr = response.substring(9, response.length() - 1);
                int cellNumber = Integer.parseInt(cellStr);
                client.unlockCellInGui(cellNumber);
            } else if (response.startsWith("CLAIMED")) {
                String[] parts = response.split(", ");
                String cellStr = parts[0].substring(8);
                String owner = parts[1].substring(0, parts[1].length() - 1);
                int cellNumber = Integer.parseInt(cellStr);
                client.claimCellInGui(cellNumber, owner);
            }  else if (response.startsWith("GAME_OVER")) {
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

class WriteThread extends Thread {
    private PrintWriter writer;

    public WriteThread(Socket socket) {
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        Console console = System.console();
        String text;

        while (true) {
            text = console.readLine("");
            writer.println(text);
        }
    }
}
