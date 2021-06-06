import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Client extends JFrame implements Runnable, ActionListener {
    JPanel jPanel;
    JToolBar jToolBar;
    JButton jButton_send_to_server, jButton_connect_to_server, jButton_select_color;

    SendingObject sending_object_jTextField_mssg_input;
    JTextArea jTextArea_mssg_history;

    Settings settings = new Settings(); //settings class

    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
//
    Color color = Color.black;

    String nickname = "";

    JTextField jTextField_nickname_input;
    JButton jButton_set_nickname;


    JMenuBar jMenuBar;
    JMenu jMenu_file, jMenu_Connection;
    JMenuItem jMenuItem_file_save, jMenuItem_file_exit;
    JMenuItem jMenuItem_connection_connect_disconnect;


    public Client() { //konstruktor
        super("Client - Chat");
        setSize(settings.window_width, settings.window_heigh);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
        setIconImage(settings.imageIconClient);


        jPanel = new JPanel(new BorderLayout());
        jToolBar = new JToolBar();
        jButton_send_to_server = new JButton(settings.image_icon_send);
        jButton_connect_to_server = new JButton(settings.image_icon_disconneted);
        jButton_select_color = new JButton(settings.image_icon_color_palette);

        sending_object_jTextField_mssg_input = new SendingObject();
        sending_object_jTextField_mssg_input.grabFocus();


        jButton_set_nickname = new JButton(settings.image_icon_set_nickname);
        jTextField_nickname_input = new JTextField();
        jTextField_nickname_input.setPreferredSize(new Dimension(80, 34));
        jTextField_nickname_input.setMaximumSize(jTextField_nickname_input.getPreferredSize());

        jTextArea_mssg_history = new JTextArea();
        jTextArea_mssg_history.setEditable(false);

        jToolBar.add(jButton_connect_to_server);

        jToolBar.add(sending_object_jTextField_mssg_input);
        jToolBar.add(jButton_send_to_server);
        jToolBar.add(jButton_select_color);
        jToolBar.addSeparator();
        jToolBar.add(jTextField_nickname_input);
        jToolBar.add(jButton_set_nickname);


        jPanel.add(jToolBar, BorderLayout.SOUTH);
        jPanel.add(jTextArea_mssg_history, BorderLayout.CENTER);

        jMenuBar = new JMenuBar();
        jMenu_file = new JMenu("FILE");
        jMenu_Connection = new JMenu("CONNECTION");

        jMenuItem_file_exit = new JMenuItem("EXIT");
        jMenuItem_file_save = new JMenuItem("Save chat history to the file");

        jMenuItem_connection_connect_disconnect = new JMenuItem("Connect/Disconnect");

        jMenu_file.add(jMenuItem_file_save);
        jMenu_file.add(jMenuItem_file_exit);

        jMenu_Connection.add(jMenuItem_connection_connect_disconnect);

        jMenuBar.add(jMenu_file);
        jMenuBar.add(jMenu_Connection);

        jMenuItem_file_save.addActionListener(this);
        jMenuItem_file_exit.addActionListener(this);

        jMenuItem_connection_connect_disconnect.addActionListener(this);


        jPanel.add(jMenuBar, BorderLayout.NORTH);

        setContentPane(jPanel);
        setVisible(true);

        jButton_send_to_server.addActionListener(this);
        jButton_connect_to_server.addActionListener(this);
        jButton_select_color.addActionListener(this);
        jButton_set_nickname.addActionListener(this);

    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();

    }


    @Override
    public void run() {
//noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                System.out.println("Thread sleep problem: ");
                ex.printStackTrace();
            }
            if (!(socket == null)) {
                if (socket.isConnected()) {
                    try {
                        objectInputStream = new ObjectInputStream(socket.getInputStream());
                        SendingObject jTextField_received_message = (SendingObject) objectInputStream.readObject();
                        jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\n" + jTextField_received_message.get_nickname() + "   " + jTextField_received_message.get_date().substring(0, 8) + ":  " +
                                jTextField_received_message.getText());
                    } catch (Exception ex) {
                        System.out.println("Disconnected");

                    }
                } else {
                    System.out.println("Socket is null");
                }
            } else {
                System.out.println("There's no connection with server");
            }
        }//while
    }//run


    @Override
    public void actionPerformed(ActionEvent e) {
        Object referer = e.getSource();

        if (referer == jButton_connect_to_server || referer == jMenuItem_connection_connect_disconnect) {
            System.out.println("jbutton_connect");
            if (socket == null) { //not connected
                try {
                    socket = new Socket(settings.host, settings.port);
                } catch (IOException exception) {
                    System.out.println("CLIENT -> CONNECTION PROBLEM");
                    exception.printStackTrace();
                    jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nSYSTEM: Connection problem! Try again later (or just run the server)");

                }
                if (socket.isConnected()) { //check if socket is connected
                    jButton_connect_to_server.setIcon(settings.image_icon_connected);//change image (connected image)
                    jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nSYSTEM: You are connected successfully!");
                }
            } else {//Already connected
                try {
                    socket.close();
                    jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nConnection closed!");
                    jButton_connect_to_server.setIcon(settings.image_icon_disconneted);
                    socket = null;
                } catch (Exception ex) {
                    jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nClosing problem! Try again later!");
                    ex.printStackTrace();
                }
            }//else

        } else if (referer == jButton_send_to_server) {
            if (!nickname.equals("")) {
                System.out.println("jbutton_send");

                try {
                    sending_object_jTextField_mssg_input.set_date(new Date(System.currentTimeMillis()));
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(sending_object_jTextField_mssg_input);
                    objectOutputStream.flush();
                    sending_object_jTextField_mssg_input.setText("");
                } catch (Exception ex) {
                    System.out.println("Sending file problem!");
                    ex.printStackTrace();
                }
            } else {
                jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nYou have to set up your nickname!"); //jbutton_set_nickname below!
            }
        } else if (referer == jButton_select_color) {
            color = JColorChooser.showDialog(this, "Choose JTextField color", color);
            if (color != null) {
                sending_object_jTextField_mssg_input.setForeground(color);
            }
        } else if (referer == jButton_set_nickname) {
            nickname = jTextField_nickname_input.getText();
            if (!nickname.equals("")) {
                jButton_set_nickname.setEnabled(false);
                jTextField_nickname_input.setEnabled(false);
                jTextField_nickname_input.setEditable(false);
                sending_object_jTextField_mssg_input.set_nickname(nickname);

            } else {
                jTextArea_mssg_history.setText(jTextArea_mssg_history.getText() + "\nNickname can't be empty! Try again"); //jbutton_set_nickname below!
            }

        } else if (referer == jMenuItem_file_exit) {
            System.exit(1);
        } else if (referer == jMenuItem_file_save) {
            try {
                String filename = "client" + Thread.currentThread().getId() + ".txt";
                File file = new File(filename);
                FileWriter fileWriter = new FileWriter(file);
                System.out.println("Saved to the file!");

                fileWriter.write(jTextArea_mssg_history.getText());
                fileWriter.close();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("Client-> actionPerformed else");
        }
    }//actionPerformed
}//class


