import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendingObject extends JTextField {
    public String nickname;
    Date date;

    SendingObject() {
        super();
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

    public void set_date(Date date) {
        this.date = date;
    }

}
