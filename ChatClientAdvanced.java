// Add these methods to your ChatClient class for enhanced functionality

private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    
    // File menu
    JMenu fileMenu = new JMenu("File");
    JMenuItem connectItem = new JMenuItem("Connect");
    JMenuItem disconnectItem = new JMenuItem("Disconnect");
    JMenuItem exitItem = new JMenuItem("Exit");
    
    connectItem.addActionListener(e -> reconnect());
    disconnectItem.addActionListener(e -> disconnect());
    exitItem.addActionListener(e -> System.exit(0));
    
    fileMenu.add(connectItem);
    fileMenu.add(disconnectItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);
    
    // Options menu
    JMenu optionsMenu = new JMenu("Options");
    JMenuItem clearChatItem = new JMenuItem("Clear Chat");
    JMenuItem changeFontItem = new JMenuItem("Change Font");
    
    clearChatItem.addActionListener(e -> chatArea.setText(""));
    changeFontItem.addActionListener(e -> changeFont());
    
    optionsMenu.add(clearChatItem);
    optionsMenu.add(changeFontItem);
    
    menuBar.add(fileMenu);
    menuBar.add(optionsMenu);
    
    setJMenuBar(menuBar);
}

private void changeFont() {
    Font currentFont = chatArea.getFont();
    Font newFont = JFontChooser.showDialog(this, "Choose Font", currentFont);
    if (newFont != null) {
        chatArea.setFont(newFont);
    }
}
