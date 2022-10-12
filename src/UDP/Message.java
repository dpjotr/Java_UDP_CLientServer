package UDP;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Externalizable {
    //message counter
    public static int last;
    public int getId() {
        return id;
    }
    public Command getCommand() {
        return command;
    }
    public String getMessage() {
        return message;
    }
    public Date getDate() {
        return date;
    }
    public InetAddress getAddress() {
        return address;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setCommand(Command command) {
        this.command = command;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setAddress(InetAddress address) {
        this.address = address;
    }
    public void setPort(int port) {
        this.port = port;
    }
    //this massage id
    private int id;
    private Command command;
    private String message;
    //message creation date
    private Date date;
    //sender ip adress
    private InetAddress address;
    //sender port
    private transient int port;
    public Message(int id, Command command, String message, Date date, InetAddress address, int port){
        this.id = id;
        this.command = command;
        this.message = message;
        this.date = date;
        this.address = address;
        this.port = port;
    }

    public  Message(){this(0, Command.PING, "", new Date(System.currentTimeMillis()), InetAddress.getLoopbackAddress(),9999);}

    public Message(InetAddress address, int port, String msg, Date date){
        id = ++last;
        message = msg;
        this.date = date;
        this.address = address;
        this.port = port;
        command = Command.PING;
    }

    public Message(String message, Command command ) {
        this.message = message;
        this.command = command;
        date = new Date(System.currentTimeMillis());
        port = 0;
        address = InetAddress.getLoopbackAddress();
        id= 0 ;
    }

    private String dateToString(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }

    @Override
    public String toString() {
        //a. <address:port:id>-<message>-<date>
        return address.getHostAddress() + ":" + port + ":" + id + "-" + message + "-" + dateToString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(command);
        out.writeObject(message);
        out.writeObject(date);
        out.writeObject(address);
        out.writeInt(port);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        command = (Command) in.readObject();
        message = (String) in.readObject();
        date = (Date) in.readObject();
        address = (InetAddress)in.readObject();
        port = in.readInt();

    }
}
