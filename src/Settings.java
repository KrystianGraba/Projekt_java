import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Settings {
    public String host = "";
    public int port;
    public int window_heigh;
    public int window_width;
    Image imageIconClient;
    Image imageIconServer;

    ImageIcon image_icon_send;
    ImageIcon image_icon_disconneted;
    ImageIcon image_icon_connected;
    ImageIcon image_icon_color_palette;
    ImageIcon image_icon_set_nickname;
    ImageIcon image_icon_client_list;


    public Settings() {
        host = "localhost";
        port = 2222;
        window_heigh = 500;
        window_width = 500;
        imageIconClient = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("ikona.png"))).getImage();
        imageIconServer = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("database-storage.png"))).getImage();


        image_icon_send = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("send.png")));
        image_icon_disconneted = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("disconnected.png")));
        image_icon_connected = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("connected.png")));
        image_icon_color_palette = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("color-palette.png")));
        image_icon_set_nickname = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("setnickname.png")));
        image_icon_client_list = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("list.png")));

    }

    public static void main(String[] args) {
        new Settings();
    }

}
