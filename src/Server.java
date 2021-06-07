//Krystian Graba - Server

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame implements Runnable, ActionListener {
    JPanel jPanel;
    JToolBar jToolBar;
    JButton jbutton_show_clients;

    JTextArea jTextArea_mssg_history;

    JTextField jTextField_active_client;

    Settings ustawienia = new Settings();

    Socket socket = new Socket();

    ServerSocket serverSocket;

    List<Thread> arrayList_clients = new ArrayList<>();

    ServerThread serverThread;

    SendingObject msg_to_send;


    JMenuBar jMenuBar;
    JMenu jMenu_file;
    JMenuItem jMenuItem_file_save, jMenuItem_file_exit;


    public Server() throws IOException { //konstruktor
        super("Serwer - Chat");
        setSize(ustawienia.window_width, ustawienia.window_heigh);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 10,
                (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
        setIconImage(ustawienia.imageIconServer);


        jPanel = new JPanel(new BorderLayout());
        jToolBar = new JToolBar();
        jbutton_show_clients = new JButton(ustawienia.image_icon_client_list);
        jTextField_active_client = new JTextField();


        jTextArea_mssg_history = new JTextArea();
        jTextArea_mssg_history.setEditable(false);

        jToolBar.add(jbutton_show_clients);
        jToolBar.add(jTextField_active_client);

        jPanel.add(jToolBar, BorderLayout.SOUTH);
        jPanel.add(jTextArea_mssg_history, BorderLayout.CENTER);

        setContentPane(jPanel);
        setVisible(true);

        jbutton_show_clients.addActionListener(this);

        jTextField_active_client.setText("Active clients: 0");
        jTextField_active_client.setEditable(false);


        jMenuBar = new JMenuBar();
        jMenu_file = new JMenu("FILE");

        jMenuItem_file_exit = new JMenuItem("EXIT");
        jMenuItem_file_save = new JMenuItem("Save chat history to file");


        jMenu_file.add(jMenuItem_file_save);
        jMenu_file.add(jMenuItem_file_exit);
        jMenuBar.add(jMenu_file);

        jPanel.add(jMenuBar,BorderLayout.NORTH);

        jMenuItem_file_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String filename = "Server" + Thread.currentThread().getId() + ".txt";
                    File file = new File(filename);
                    FileWriter fileWriter = new FileWriter(file);
                    System.out.println("Saved to the file!");

                    fileWriter.write(jTextArea_mssg_history.getText());
                    fileWriter.close();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        jMenuItem_file_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        serverSocket = new ServerSocket(ustawienia.port);
        System.out.println("Server alive, waiting for clients!\t" + serverSocket.getLocalSocketAddress());
        Thread thread_check_new_messages = new Thread(new Runnable() { //if Thread has new message catch it and call send function(send to all)
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < arrayList_clients.size(); i++) {
                        if (!(((ServerThread) arrayList_clients.get(i)).jTextFieldList == null)) {
                            if (!((ServerThread) arrayList_clients.get(i)).jTextFieldList.isEmpty()) {
                                msg_to_send = (SendingObject) ((ServerThread) arrayList_clients.get(i)).jTextFieldList.get(0);
                                ((ServerThread) arrayList_clients.get(i)).jTextFieldList.clear();
                                if (msg_to_send != null) {
                                    for (int y = 0; y < arrayList_clients.size(); y++) {
                                        System.out.println(y + ": Sending: " + msg_to_send.getText());
                                        ((ServerThread) arrayList_clients.get(y)).send(msg_to_send);
                                    }//for
                                }//if
                            }//if
                        }//if
                    }//for
                    try {
                        Thread.sleep(200);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }//while
            }//run
        });
        thread_check_new_messages.start();


        Thread thread_check_connections = new Thread(new Runnable() { //check if socekt is closed
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < arrayList_clients.size(); i++) {
                        if (!(((ServerThread) arrayList_clients.get(i)).is_conected)) {
                            System.out.println("Client deleted from the list");
                            jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nClient disconnected\t" + ((ServerThread) arrayList_clients.get(i)).socket.getInetAddress().toString());
                            arrayList_clients.remove(i);
                            jTextField_active_client.setText("Active clients: " + arrayList_clients.size());
                        }//if
                    }//for
                    try {
                        Thread.sleep(500);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        thread_check_connections.start();

//noinspection InfiniteLoopStatement
        while (true) {//wait for new client
            try {
                socket = serverSocket.accept();
                serverThread = new ServerThread(socket);
                serverThread.start();
                jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nNew client connected\t" + socket.getInetAddress().toString());
                arrayList_clients.add(serverThread);
                jTextField_active_client.setText("Active clients: " + arrayList_clients.size());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }//while new client


    }//constructor

    public static void main(String[] args) throws IOException {
        new Server();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object referrer = e.getSource();
        if (referrer == jbutton_show_clients) { //print all clients
            if (!arrayList_clients.isEmpty()) {
                jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nClients(one per Thread): ");
                for (int i = 0; i < arrayList_clients.size(); i++) {
                    jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\n" + arrayList_clients.get(i));
                }
            }//if
        }//if
    }//actionPerformed

    @Override
    public void run() {

    }
}//class