package UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import static UDP.Services.convertObjectFromBytes;
import static UDP.UDPServer.BUFFER_SIZE;
import static UDP.UDPServer.SERVER_PORT;

public class UDPClient implements  AutoCloseable{
    private DatagramSocket socket;
    private DatagramPacket packet;
    GUI gui;

    public UDPClient() throws SocketException {
        socket = new DatagramSocket();
        packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        gui = new GUI();
    }

    public static void main(String[] args){

        try(UDPClient client = new UDPClient()) {
            client.sendMessage(InetAddress.getLocalHost(), SERVER_PORT, new Message(null, null));
            while (true){
                ;
            }
        }
        catch(Exception e){
            System.err.println("Error: "+e.getMessage());
        }
    }



    @Override
    public void close() throws Exception {
        if (socket !=null && !socket.isClosed()) socket.close();
    }

    public   boolean sendMessage(InetAddress ip, int port, Message message) {
        try {
            byte[] buf = Services.convertObjectToBytes(message);
            packet = new DatagramPacket(buf, buf.length, ip, port);
            socket.send(packet);
            packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            socket.receive(packet);
            Message answer;
            ArrayList<Message> list;
            Object answerObj = (Object) convertObjectFromBytes(packet.getData());
            if( answerObj instanceof Message) {
                answer = (Message) answerObj;
                gui.textArea.append(answer.getMessage());
            }
            else {
                list = (ArrayList<Message>) convertObjectFromBytes(packet.getData());
                if(message.getCommand()==Command.TOP_MESSAGE) gui.textArea.append("top 10 messages:\n");
                if(message.getCommand()==Command.CLIENT_MESSAGE) gui.textArea.append("client last 10 messages:\n");
                if (!list.isEmpty())
                    for (var x:list)
                        gui.textArea.append(x.toString()+"\n");
                    else
                    gui.textArea.append("Empty list\n");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error from client sendMessage: " + e.getMessage());
            return false;
        }
    }

    public class GUI extends JFrame {
        boolean editable=false;
        JFrame jf;
        Container contents;
        JButton ping;
        JButton add;
        JButton top10;
        JButton my10;
        JButton clear;
        JTextArea textArea;

        GUI()
        {
            jf=new JFrame();
            jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
            jf.setSize(600,600);

            contents=jf.getContentPane();

            JToolBar toolbar;
            textArea = new JTextArea();
            userInstruction();
            textArea.setEditable(editable);
            JScrollPane scrollPane = new JScrollPane(textArea);
            contents.add(scrollPane, BorderLayout.CENTER);

            //tollbar section:
            toolbar=new JToolBar("Toolbar");
            contents.add(toolbar, BorderLayout.SOUTH);

            ping = new JButton("Ping");
            clear = new JButton("Clear Screen");

            add = new JButton("Add");
            top10 = new JButton("Top10");
            my10 = new JButton("My10");

            toolbar.add(ping);
            toolbar.add(add);
            toolbar.add(top10);
            toolbar.add(my10);
            toolbar.add(clear);

            //Action listeners
            clear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.setText("");
                    editable = true;
                    textArea.setEditable(editable);
                }
            });
            ping.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.setText("");
                    ping();
                    userInstruction();
                    editable = false;
                    textArea.setEditable(editable);
                }
            });
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(editable)
                        add();
                    userInstruction();
                    editable = false;
                    textArea.setEditable(editable);
                }
            });
            top10.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textArea.setText("");
                    top10();
                    userInstruction();
                    editable = false;
                    textArea.setEditable(editable);
                }
            });
            my10.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                my10();
                userInstruction();
                editable = false;
                textArea.setEditable(editable);
                }
            });
            jf.setVisible(true);
        }
        public void ping() {
            try {
                sendMessage(InetAddress.getLocalHost(), SERVER_PORT, new Message("", Command.PING));
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

        }

        public void add() {
            try {
                sendMessage(InetAddress.getLocalHost(), SERVER_PORT, new Message(textArea.getText(), Command.ADD));
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

        public void top10() {
            try {
                sendMessage(InetAddress.getLocalHost(), SERVER_PORT, new Message("" ,Command.TOP_MESSAGE));
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

        public void my10() {
            try {
                sendMessage(InetAddress.getLocalHost(), SERVER_PORT, new Message("" ,Command.CLIENT_MESSAGE));
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        void userInstruction(){
            textArea.append("\nIn order to send text message to server\n" +
                    "press clear screen button then type your message\n" +
                    "and press add button");
        }
    }
}
