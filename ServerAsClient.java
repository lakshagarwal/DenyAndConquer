import java.io.IOException;

public class ServerAsClient {
    private TCPClient client;

    // The ServerAsClient class acts as a client to the TCPServer.

    public ServerAsClient() throws IOException {
        // Start the server in a new thread to handle incoming client connections.
        new Thread(() -> {
            try {
                TCPServer server = new TCPServer();
                server.start(); // Start the TCPServer and accept incoming client connections.
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error starting server: " + e.getMessage());
            }
        }).start();

        // Sleep for a short time to ensure the server has started.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize the client for the server.
        client = new TCPClient();
    }

    // This method is used to initialize the game GUI for the client.
    public void initializeGameGui() {
        client.startThreads(); // Start the client threads (ReadThread and WriteThread).
    }
}