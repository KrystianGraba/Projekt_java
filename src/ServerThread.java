import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    Socket socket = null;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    List<JTextField> jTextFieldList = new ArrayList<>();

    boolean is_conected = true;


    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void send(SendingObject mssg_to_send) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(mssg_to_send);
            objectOutputStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//send

    public void run() {
        while (is_conected) {
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
                        System.out.println(jTextField_received_message);
                        jTextFieldList.add(0, jTextField_received_message);
                    } catch (Exception ex) {
                        if (socket.isConnected()) {
                            System.out.println("Reading data problem! Port was closed! Thread stopped");

                            is_conected = false;
                        }
                    }
                }

            } else {
                System.out.println("Socket is null");
            }
        }//while
    }//run
}//class
