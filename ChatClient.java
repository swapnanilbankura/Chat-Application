import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private String username;
    
    public ChatClient() {
        initializeGUI();
        connectToServer();
    }
    
    private void initializeGUI() {
        setTitle("Java Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Chat area
        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // User list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setPreferredSize(new Dimension(150, 0));
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Online Users"));
        
        // Message input
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        
        messageField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());
        
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        // Layout
        add(chatScrollPane, BorderLayout.CENTER);
        add(userScrollPane, BorderLayout.EAST);
        add(messagePanel, BorderLayout.SOUTH);
        
        // Window settings
        pack();
        setLocationRelativeTo(null);
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
    }
    
    private void connectToServer() {
        try {
            // Get username
            username = JOptionPane.showInputDialog(this, "Enter your username:", 
                "Username", JOptionPane.PLAIN_MESSAGE);
            
            if (username == null || username.trim().isEmpty()) {
                System.exit(0);
            }
            
            // Connect to server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Send username
            String serverPrompt = in.readLine(); // "Enter your username:"
            out.println(username);
            
            // Start listening for messages
            new Thread(this::listenForMessages).start();
            
            setTitle("Java Chat - " + username);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("USERS:")) {
                    updateUserList(message.substring(6));
                } else {
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append(message + "\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    });
                }
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Connection to server lost.\n");
            });
        }
    }
    
    private void updateUserList(String users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            if (!users.trim().isEmpty()) {
                String[] userArray = users.split(",");
                for (String user : userArray) {
                    userListModel.addElement(user.trim());
                }
            }
        });
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            messageField.setText("");
        }
    }
    
    private void disconnect() {
        try {
            if (out != null) {
                out.println("/quit");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                // Use default look and feel
            }
            new ChatClient().setVisible(true);
        });
    }
}
