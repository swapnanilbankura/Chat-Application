import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error setting up client handler: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            // Get username
            out.println("Enter your username:");
            username = in.readLine();
            
            if (username == null || username.trim().isEmpty()) {
                out.println("Invalid username. Disconnecting...");
                return;
            }
            
            ChatServer.addClient(username, this);
            out.println("Welcome to the chat, " + username + "!");
            
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                
                if (message.startsWith("/whisper ")) {
                    handlePrivateMessage(message);
                } else {
                    ChatServer.broadcastMessage(username, message);
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private void handlePrivateMessage(String message) {
        String[] parts = message.split(" ", 3);
        if (parts.length >= 3) {
            String targetUser = parts[1];
            String privateMessage = parts[2];
            
            // Implementation for private messaging can be added here
            sendMessage("Private messaging feature - send to " + targetUser + ": " + privateMessage);
        }
    }
    
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    private void cleanup() {
        try {
            if (username != null) {
                ChatServer.removeClient(username);
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}
