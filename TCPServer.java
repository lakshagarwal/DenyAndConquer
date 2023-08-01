import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7070);
        //System.out.println("Server started...");

        ArrayList<ServerThread> clients = new ArrayList<>();
        AtomicInteger clientCounter = new AtomicInteger(0); 
        String[] names = {"Red", "Blue", "Green", "Yellow"}; 
        boolean[] cells = new boolean[64]; 
        String[] cellOwners = new String[64]; 
        boolean[] cellClaimed = new boolean[64]; 

        while (true) {
            Socket socket = serverSocket.accept();
            
            if (clientCounter.get() >= 4) {
                //System.out.println("Maximum client limit reached");
                socket.close();
            } else {
                ServerThread st = new ServerThread(socket, clients, names[clientCounter.get()], cells, cellOwners, cellClaimed);
                clients.add(st);
                st.start();
                for(ServerThread client : clients) {
                    if (client != st) {
                        client.sendMessage(names[clientCounter.get()] + " connected");
                    }
                }
                clientCounter.incrementAndGet();
            }
        }
    }
}



class ServerThread extends Thread {
    private Socket socket;
    private ArrayList<ServerThread> clients;
    private PrintWriter writer;
    private String username;
    private boolean[] cells;
    private String[] cellOwners;
    private boolean[] cellClaimed;
    private Pattern pattern = Pattern.compile("\\(([^)]+)\\)"); // regex to extract number from messages

    public ServerThread(Socket socket, ArrayList<ServerThread> clients, String username, boolean[] cells, String[] cellOwners, boolean[] cellClaimed) {
        this.socket = socket;
        this.clients = clients;
        this.username = username;
        this.cells = cells;
        this.cellOwners = cellOwners;
        this.cellClaimed = cellClaimed;
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

            writer.println("You are client " + username); // tell the client its username

            while (true) {
                String clientMessage = reader.readLine();
                Matcher matcher = pattern.matcher(clientMessage);
                String number = "";
                if (matcher.find()) {
                    number = matcher.group(1); // extract number
                }
                int cellNumber = Integer.parseInt(number);

                synchronized(cells) {
                    if (clientMessage.startsWith("LOCK")) {
                        if(cellNumber >= 0 && cellNumber < 64 && !cells[cellNumber] && !cellClaimed[cellNumber]) {
                            // System.out.println("Lock command received: " + clientMessage);
                            cells[cellNumber] = true; // lock the cell
                            cellOwners[cellNumber] = username; // set the owner
                            broadcastMessage("LOCKED(" + number + ")");
                        }
                    } else if (clientMessage.startsWith("UNLOCK")) {
                        if(cellNumber >= 0 && cellNumber < 64 && cells[cellNumber] && cellOwners[cellNumber].equals(username) && !cellClaimed[cellNumber]) {
                            System.out.println("Unlock command received: " + clientMessage);
                            cells[cellNumber] = false; // unlock the cell
                            cellOwners[cellNumber] = null; // remove the owner
                            broadcastMessage("UNLOCKED(" + number + ")");
                        }
                    } else if (clientMessage.startsWith("CLAIM")) {
                        if(cellNumber >= 0 && cellNumber < 64 && cells[cellNumber] && cellOwners[cellNumber].equals(username) && !cellClaimed[cellNumber]) {
                            // System.out.println("Claim command received: " + clientMessage);
                            cellClaimed[cellNumber] = true; // the cell has been claimed
                            broadcastMessage("CLAIMED(" + number + ", " + username + ")");
                        }
                    } else {
                        // System.out.println("Message received: " + clientMessage);
                        broadcastMessage(username + ": " + clientMessage);
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this); // Remove the client when it's disconnected
        }
    }

    void broadcastMessage(String message) {
        for (ServerThread st : clients) {
            // Skip the client that sent the message
            if (st != this) {
                st.writer.println(message);
            }
        }
    }
}
