import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPServer {
    private ServerSocket serverSocket;
    private List<ServerThread> clients = new ArrayList<>();
    private AtomicInteger clientCounter = new AtomicInteger();
    private String[] names = {"Red", "Blue", "Green", "Yellow"};
    private String[] cellOwners = new String[64];
    private int[] playerScores = new int[4]; // <-- NEW: To keep track of scores
    private boolean gameStarted = false;

    public TCPServer() throws IOException {
        serverSocket = new ServerSocket(7070);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();

            if (clientCounter.get() >= 4) {
                socket.close();
            } else {
                ServerThread st = new ServerThread(socket, this, names[clientCounter.get()]);
                clients.add(st);
                st.start();
                clientCounter.incrementAndGet();
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ServerThread st : clients) {
            st.sendMessage(message);
        }
    }

    class ServerThread extends Thread {
        private Socket socket;
        private TCPServer server;
        private PrintWriter writer;
        private String username;
        private Pattern pattern = Pattern.compile("\\(([^)]+)\\)");

        public ServerThread(Socket socket, TCPServer server, String username) {
            this.socket = socket;
            this.server = server;
            this.username = username;
        }

        public void sendMessage(String message) {
            writer.println(message);
        }

        public void run() {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                sendMessage("YOUR_COLOR(" + username + ")");
                
                while (true) {
                    String clientMessage = reader.readLine();
                    Matcher matcher = pattern.matcher(clientMessage);
                    String number = "";
                    int cellNumber = -1;

                    if (matcher.find()) {
                        number = matcher.group(1);
                        cellNumber = Integer.parseInt(number);
                    }

                    synchronized(cellOwners) {
                        if (clientMessage.startsWith("LOCK") && cellNumber != -1) {
                            if(cellOwners[cellNumber] == null) {
                                cellOwners[cellNumber] = username;
                                server.broadcastMessage("LOCKED(" + number + ")");
                            }
                        } else if (clientMessage.startsWith("UNLOCK") && cellNumber != -1) {
                            if(cellOwners[cellNumber].equals(username)) {
                                cellOwners[cellNumber] = null;
                                server.broadcastMessage("UNLOCKED(" + number + ")");
                            }
                        } else if (clientMessage.startsWith("CLAIM") && cellNumber != -1) {
                            if(cellOwners[cellNumber].equals(username)) {
                                server.broadcastMessage("CLAIMED(" + number + ", " + username + ")");
                                
                                playerScores[Arrays.asList(names).indexOf(username)] += 1;
                                
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
                            gameStarted = true;
                            server.broadcastMessage("START_GAME");
                        } else {
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
