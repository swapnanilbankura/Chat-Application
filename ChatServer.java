import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static ServerSocket serverSocket;
    
    public static void main(String[] args) {
        System.out.println("Chat Server Started on port " + PORT);
        
        try {
            serverSocket = new ServerSocket(PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    
    public static synchronized void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        broadcastMessage("SERVER", username + " joined the chat!");
        broadcastUserList();
    }
    
    public static synchronized void removeClient(String username) {
        clients.remove(username);
        broadcastMessage("SERVER", username + " left the chat!");
        broadcastUserList();
    }
    
    public static synchronized void broadcastMessage(String sender, String message) {
        String fullMessage = "[" + new Date().toString() + "] " + sender + ": " + message;
        
        for (ClientHandler client : clients.values()) {
            client.sendMessage(fullMessage);
        }
    }
    
    private static void broadcastUserList() {
        String userList = "USERS:" + String.join(",", clients.keySet());
        for (ClientHandler client : clients.values()) {
            client.sendMessage(userList);
        }
    }
}
