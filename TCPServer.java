import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPServer {
    // TCPServer class is responsible for handling server-side logic and communication with multiple clients.

    private ServerSocket serverSocket;
    private List<ServerThread> clients = new ArrayList<>();
    private AtomicInteger clientCounter = new AtomicInteger();
    private String[] names = {"Red", "Blue", "Green", "Yellow"};
    private String[] cellOwners = new String[64];
    private int[] playerScores = new int[4]; // To keep track of scores for each player
    private boolean gameStarted = false;

    // Constructor initializes the server by creating a ServerSocket listening on port 7070.
    public TCPServer() throws IOException {
        serverSocket = new ServerSocket(7070);
    }

    // Starts the server and continuously accepts incoming client connections.
    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();

            if (clientCounter.get() >= 4) {
                // Reject the connection if there are already 4 clients connected.
                socket.close();
            } else {
                // Create a new ServerThread for the client, start it, and add it to the clients list.
                ServerThread st = new ServerThread(socket, this, names[clientCounter.get()]);
                clients.add(st);
                st.start();
                clientCounter.incrementAndGet();
            }
        }
    }

    // Broadcasts a message to all connected clients.
    public void broadcastMessage(String message) {
        for (ServerThread st : clients) {
            st.sendMessage(message);
        }
    }

    // ServerThread class handles communication with an individual client.
    class ServerThread extends Thread {
        private Socket socket;
        private TCPServer server;
        private PrintWriter writer;
        private String username;
        private Pattern pattern = Pattern.compile("\\(([^)]+)\\)");

        // Constructor initializes the ServerThread with the client's socket, the TCPServer instance, and the client's username.
        public ServerThread(Socket socket, TCPServer server, String username) {
            this.socket = socket;
            this.server = server;
            this.username = username;
        }

        // Sends a message to the client.
        public void sendMessage(String message) {
            writer.println(message);
        }

        // The run method is the main logic for handling client-server communication.
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);

                // Send the client's color to identify them in the game.
                sendMessage("YOUR_COLOR(" + username + ")");

                while (true) {
                    String clientMessage = reader.readLine();
                    Matcher matcher = pattern.matcher(clientMessage);
                    String number = "";
                    int cellNumber = -1;

                    // Extract the cell number from the client's message (if any).
                    if (matcher.find()) {
                        number = matcher.group(1);
                        cellNumber = Integer.parseInt(number);
                    }

                    synchronized(cellOwners) {
                        // Handle different client messages based on their content.

                        if (clientMessage.startsWith("LOCK") && cellNumber != -1) {
                            // The client is requesting to lock a box. If the box is unclaimed, lock it and inform all clients.
                            if (cellOwners[cellNumber] == null) {
                                cellOwners[cellNumber] = username;
                                server.broadcastMessage("LOCKED(" + number + ")");
                            }
                        } else if (clientMessage.startsWith("UNLOCK") && cellNumber != -1) {
                            // The client is requesting to unlock a box. If the box is owned by the client, unlock it and inform all clients.
                            if (cellOwners[cellNumber].equals(username)) {
                                cellOwners[cellNumber] = null;
                                server.broadcastMessage("UNLOCKED(" + number + ")");
                            }
                        } else if (clientMessage.startsWith("CLAIM") && cellNumber != -1) {
                            // The client is requesting to claim a box. If the box is owned by the client, claim it and inform all clients.
                            if (cellOwners[cellNumber].equals(username)) {
                                server.broadcastMessage("CLAIMED(" + number + ", " + username + ")");

                                // Increase the player's score by 1 for claiming the box.
                                playerScores[Arrays.asList(names).indexOf(username)] += 1;

                                // Check if all boxes are claimed, then determine the game winner.
                                if (Arrays.stream(cellOwners).allMatch(Objects::nonNull)) {
                                    int maxScore = Arrays.stream(playerScores).max().getAsInt();
                                    List<String> winners = new ArrayList<>();
                                    for (int i = 0; i < 4; i++) {
                                        if (playerScores[i] == maxScore) {
                                            winners.add(names[i]);
                                        }
                                    }
                                    if (winners.size() > 1) {
                                        server.broadcastMessage("GAME_OVER(Tie between " + String.join(", ", winners) + ")");
                                    } else {
                                        server.broadcastMessage("GAME_OVER(" + winners.get(0) + " wins)");
                                    }
                                }
                            }
                        } else if (clientMessage.equals("START") && username.equals("Red")) {
                            // The client with the username "Red" is requesting to start the game.
                            // Set the gameStarted flag to true and inform all clients to start the game.
                            gameStarted = true;
                            server.broadcastMessage("START_GAME");
                        } else {
                            // If the message doesn't match any specific command, treat it as a general chat message
                            // and broadcast it to all clients.
                            server.broadcastMessage(username + ": " + clientMessage);
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
            }
        }
    }
}

