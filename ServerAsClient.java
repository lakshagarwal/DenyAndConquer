import java.io.IOException;

public class ServerAsClient {
    public static void main(String[] args) throws IOException {
        // Run the server in a new thread
        new Thread(() -> {
            try {
                TCPServer.main(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Start a new client for the server
        TCPClient.main(args);
    }
}
