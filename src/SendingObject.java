import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendingObject extends JTextField {
    public String nickname;
    Date date;
    boolean isEncrypted;
Color color;
    SendingObject() {
        super();
        isEncrypted = false;
    }

    SendingObject(String nickname) {
        this.nickname = nickname;
    }

    public String get_nickname() {
        return this.nickname;
    }

    public void set_nickname(String nickname) {
        this.nickname = nickname;
    }

    public String get_date() {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        return dateTimeFormatter.format(this.date);
    }
public Color get_color(){
        return this.color;
}
public void set_color(Color color){
        this.color=color;
}
    public void set_date(Date date) {
        this.date = date;
    }

    public void set_encryption(boolean encryption){
        this.isEncrypted = encryption;
    }

}
