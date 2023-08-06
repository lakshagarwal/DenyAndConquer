import java.io.IOException;

public class ServerAsClient {
    private TCPClient client;

    public ServerAsClient() throws IOException {
        // Start the server
        new Thread(() -> {
            try {
                TCPServer server = new TCPServer();
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error starting server: " + e.getMessage());
            }
        }).start();

        // Sleep for a short time to ensure the server has started
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize the client for the server
        client = new TCPClient();
    }

    public void initializeGameGui() {
        client.initializeGameGui();
    }
}
