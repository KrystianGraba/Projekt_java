import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Date;

public class Client extends JFrame implements Runnable, ActionListener {
    JPanel jPanel_message_history;

    JToolBar jToolBar;
    JButton jButton_send_to_server, jButton_connect_to_server, jButton_select_color;

    SendingObject sending_object_message_input;

    Settings settings = new Settings(); //settings class

    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    //
    Color color = Color.black;

    String nickname;

    JTextField jTextField_nickname_input;
    JButton jButton_set_nickname;

    JMenuBar jMenuBar;
    JMenu jMenu_file, jMenu_Connection, jMenu_Encryption;

    JMenuItem jMenuItem_file_save, jMenuItem_file_exit;
    JMenuItem jMenuItem_connection_connect_disconnect;
    JMenuItem jmenuItem_encrypt_messages_on, jmenuItem_encrypt_messages_off, jMenuItem_decrypt_messages;

    JTextField jTextField_decrypt_password;
    String encryption_password;
    List<SendingObject> list_sending_object_chat_history = new ArrayList<>();
    boolean isEncrypted;

    public Client() { //konstruktor
        super("Client - Chat");
        setLayout(new BorderLayout());
        setSize(settings.window_width, settings.window_heigh);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
        setIconImage(settings.imageIconClient);


        jToolBar = new JToolBar();
        jButton_send_to_server = new JButton(settings.image_icon_send);
        jButton_connect_to_server = new JButton(settings.image_icon_disconneted);
        jButton_select_color = new JButton(settings.image_icon_color_palette);

        sending_object_message_input = new SendingObject();
        sending_object_message_input.grabFocus();


        jButton_set_nickname = new JButton(settings.image_icon_set_nickname);
        jTextField_nickname_input = new JTextField();
        jTextField_nickname_input.setPreferredSize(new Dimension(80, 34));
        jTextField_nickname_input.setMaximumSize(jTextField_nickname_input.getPreferredSize());

        jToolBar.add(jButton_connect_to_server);

        jToolBar.add(sending_object_message_input);
        jToolBar.add(jButton_send_to_server);
        jToolBar.add(jButton_select_color);
        jToolBar.addSeparator();
        jToolBar.add(jTextField_nickname_input);
        jToolBar.add(jButton_set_nickname);



        jMenuBar = new JMenuBar();
        jMenu_file = new JMenu("FILE");
        jMenu_Connection = new JMenu("CONNECTION");
        jMenu_Encryption = new JMenu("ENCRYPTION");

        jMenuItem_file_exit = new JMenuItem("EXIT");
        jMenuItem_file_save = new JMenuItem("Save chat history to the file");

        jMenuItem_connection_connect_disconnect = new JMenuItem("Connect/Disconnect");

        jmenuItem_encrypt_messages_on = new JMenuItem("Encrypt messages ON");
        jmenuItem_encrypt_messages_off = new JMenuItem("Encrypt messages OFF");
        jTextField_decrypt_password = new JTextField("tajneHaslo");
        jMenuItem_decrypt_messages = new JMenuItem("Decrypt all messages");

        jMenu_Encryption.add(jmenuItem_encrypt_messages_on);
        jMenu_Encryption.add(jmenuItem_encrypt_messages_off);
        jMenu_Encryption.add(jTextField_decrypt_password);
        jMenu_Encryption.add(jMenuItem_decrypt_messages);

        jMenu_file.add(jMenuItem_file_save);
        jMenu_file.add(jMenuItem_file_exit);

        jMenu_Connection.add(jMenuItem_connection_connect_disconnect);

        jMenuBar.add(jMenu_file);
        jMenuBar.add(jMenu_Connection);
        jMenuBar.add(jMenu_Encryption);

        jMenuItem_file_save.addActionListener(this);
        jMenuItem_file_exit.addActionListener(this);

        jMenuItem_connection_connect_disconnect.addActionListener(this);
        isEncrypted=false;

        jPanel_message_history = new JPanel(new GridLayout(100,0));



        add(BorderLayout.NORTH,jToolBar);
        setJMenuBar(jMenuBar);
        add(BorderLayout.CENTER,new JScrollPane(jPanel_message_history));



        setVisible(true);

        jButton_send_to_server.addActionListener(this);
        jButton_connect_to_server.addActionListener(this);
        jButton_select_color.addActionListener(this);
        jButton_set_nickname.addActionListener(this);

        jmenuItem_encrypt_messages_on.addActionListener(this);
        jmenuItem_encrypt_messages_off.addActionListener(this);
        jMenuItem_decrypt_messages.addActionListener(this);


    }

    public void write(String text, Color color){
        SendingObject sending_object_message = new SendingObject();
        sending_object_message.setText(text);
        sending_object_message.setForeground(Color.red);
        sending_object_message.set_message(text);
        jPanel_message_history.add(sending_object_message);
        jPanel_message_history.updateUI();

    }
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();

    }

    public void encrypt_decrypt_password(){
        encryption_password = jTextField_decrypt_password.getText();
        if (encryption_password != null){

            encryption_password=jTextField_decrypt_password.getText();
            if (encryption_password.length()>16){ //check key lenght
                encryption_password = encryption_password.substring(0,16);
            }else if(encryption_password.length() <16){
                int x = 16- encryption_password.length();
                for(int i = 0 ; i <x; i ++) {
                    encryption_password = encryption_password + "0";
                }
            }
        }else{
            System.out.println("pass null");
        }
    }

    public String encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher;
        cipher = Cipher.getInstance("AES");
        SecretKey secretKey = new SecretKeySpec(encryption_password.getBytes(), "AES");
        byte[] plainTextByte =        sending_object_message_input.getText().getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        return (encoder.encodeToString(encryptedByte));

    }

    public String decrypt(String to_decrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher;
        cipher = Cipher.getInstance("AES");
        SecretKey secretKey = new SecretKeySpec(encryption_password.getBytes(), "AES");
        byte[] plainTextByte = sending_object_message_input.getText().getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        sending_object_message_input.setText(encoder.encodeToString(encryptedByte));



        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(to_decrypt);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        return new String(decryptedByte);

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
                        SendingObject sending_object_received_message = (SendingObject) objectInputStream.readObject();
                        if (sending_object_received_message.is_encrypted()){
                            write("$$" + sending_object_received_message.get_nickname() + "   " + sending_object_received_message.get_date().substring(0, 8) + ":  " +
                                    sending_object_received_message.get_message(),sending_object_received_message.get_color());
                            list_sending_object_chat_history.add(sending_object_received_message);

                        }else{
                            write( sending_object_received_message.get_nickname() + "   " + sending_object_received_message.get_date().substring(0, 8) + ":  " +
                                    sending_object_received_message.getText(),sending_object_received_message.get_color());
                            list_sending_object_chat_history.add(sending_object_received_message);

                        }
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
            if (socket == null) { //not connected
                try {
                    socket = new Socket(settings.host, settings.port);
                } catch (IOException exception) {
                    System.out.println("CLIENT -> CONNECTION PROBLEM");
                    exception.printStackTrace();
                    write("SYSTEM: Connection problem! Try again later (or just run the server)",Color.RED);

                }
                if (socket.isConnected()) { //check if socket is connected
                    jButton_connect_to_server.setIcon(settings.image_icon_connected);//change image (connected image)
                    write("SYSTEM: You are connected successfully!",Color.GREEN);
                }
            } else {//Already connected
                try {
                    socket.close();
                    write("SYSTEM: Connection closed!",Color.RED);
                    jButton_connect_to_server.setIcon(settings.image_icon_disconneted);
                    socket = null;
                } catch (Exception ex) {
                    write("SYSTEM: Closing problem! Try again later!",Color.RED);
                    ex.printStackTrace();
                }
            }//else

        } else if (referer == jButton_send_to_server) {
            if (!nickname.equals("")) {
                try {
                    sending_object_message_input.set_date(new Date(System.currentTimeMillis()));
                    sending_object_message_input.set_color(color);
                    if (isEncrypted){
                        String encrypted_message = encrypt();
                        sending_object_message_input.set_message(encrypted_message);
                        sending_object_message_input.setText(encrypted_message);
                        sending_object_message_input.set_encryption(true);
                    }else{
                        sending_object_message_input.set_encryption(false);
                        sending_object_message_input.set_message(sending_object_message_input.getText());
                    }


                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(sending_object_message_input);
                    objectOutputStream.flush();
                    sending_object_message_input.setText("");
                } catch (Exception ex) {
                    System.out.println("Sending file problem!");
                    ex.printStackTrace();
                }
            } else {
                write("SYSTEM: You have to set up your nickname!",Color.RED); //jbutton_set_nickname below!
            }
        } else if (referer == jButton_select_color) {
            color = JColorChooser.showDialog(this, "Choose JTextField color", color);
            if (color != null) {
                sending_object_message_input.setForeground(color);
            }
        } else if (referer == jButton_set_nickname) {
            nickname = jTextField_nickname_input.getText();
            if (!nickname.equals("")) {
                jButton_set_nickname.setEnabled(false);
                jTextField_nickname_input.setEnabled(false);
                jTextField_nickname_input.setEditable(false);
                sending_object_message_input.set_nickname(nickname);

            } else {
                write("SYSTEM: Nickname can't be empty! Try again",Color.RED); //jbutton_set_nickname below!
            }

        } else if (referer == jMenuItem_file_exit) {
            System.exit(1);
        } else if (referer == jMenuItem_file_save) {
            try {
                String filename = "client" + Thread.currentThread().getId() + ".txt";
                File file = new File(filename);
                FileWriter fileWriter = new FileWriter(file + "\n");
                write("Saved to the file!",Color.GREEN);

                for (int i = 0; i < list_sending_object_chat_history.size(); i++){
                    fileWriter.write(list_sending_object_chat_history.get(i).getText());
                }
                fileWriter.close();

            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }else if(referer==jmenuItem_encrypt_messages_off){
            if (isEncrypted){
                isEncrypted = false;
                write("SYSTEM: Encryption turned off",Color.BLACK);
            }else{
                write("SYSTEM: Encryption is already turned off",Color.BLACK);
            }

        }else if (referer==jmenuItem_encrypt_messages_on){
            if (!isEncrypted){
                if (jTextField_decrypt_password.getText().isEmpty()){
                    write("SYSTEM: Firstly you have to enter you encryption password",Color.RED);

                }else{
                    write("SYSTEM: Encryption turned on",Color.BLACK);
                    isEncrypted=true;
                    encryption_password=jTextField_decrypt_password.getText();
                    encrypt_decrypt_password();
                }
            }

        }else if (referer == jMenuItem_decrypt_messages){
            if (jTextField_decrypt_password.getText().isEmpty()){
                write("SYSTEM: Firstly you have to enter decrypt password",Color.RED);

            }else{
                write("SYSTEM: Decrypting starting!",Color.BLACK);

                encrypt_decrypt_password();


                for(int i = 0; i < list_sending_object_chat_history.size(); i++){
                    System.out.println(list_sending_object_chat_history.get(i).get_message());
                    String check_message = list_sending_object_chat_history.get(i).getText();
                    System.out.println(check_message);

                    if (list_sending_object_chat_history.get(i).is_encrypted()){
                        try{
                            //@TODO
                            encrypt_decrypt_password();
                            String decrypted_message = decrypt(list_sending_object_chat_history.get(i).get_message());
                            //  sending_objectlist_message_history.get(i).setText("$$" + sending_objectlist_message_history.get(i).get_nickname() + "   " + sending_objectlist_message_history.get(i).get_date().substring(0, 8) + ":  " +
                            //        decrypted_message,sending_objectlist_message_history.get(i).get_color());

                //ODszyfrouje ale nie ustawia jako teks
                            list_sending_object_chat_history.get(i).setText(decrypted_message);
                            list_sending_object_chat_history.get(i).set_message(decrypted_message);

                            System.out.println(decrypted_message);
                            System.out.println(decrypted_message);
                            System.out.println(decrypted_message);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }else{
                        System.out.println("2. NIE jest");
                    }
                }
            }
        }

        else {
            System.out.println("Client-> actionPerformed else");
        }
    }//actionPerformed
}//class


//ODszyfrouje ale nie ustawia jako teks






//---decrypt------
//     for(int i = 0; i < list_sending_object_chat_history.size(); i++){
//@TODO to lepszy if  jest od tego na dole ale nie dziala, zrobic ten usunac na dole
//                    if (list_sending_object_chat_history.get(i).is_encrypted()){
//                        try{
//                            String decrypted_message = decrypt(list_sending_object_chat_history.get(i).get_message());
//                            //  sending_objectlist_message_history.get(i).setText("$$" + sending_objectlist_message_history.get(i).get_nickname() + "   " + sending_objectlist_message_history.get(i).get_date().substring(0, 8) + ":  " +
//                            //        decrypted_message,sending_objectlist_message_history.get(i).get_color());
//                            list_sending_object_chat_history.get(i).setText(decrypted_message);
//                            System.out.println(decrypted_message);
//                            System.out.println(decrypted_message);
//                            System.out.println(decrypted_message);
//                        }catch(Exception ex){
//                            ex.printStackTrace();
//                        }
//                    }else{
//                        System.out.println("not enc");
//                    }