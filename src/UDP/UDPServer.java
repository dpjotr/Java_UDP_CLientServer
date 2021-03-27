package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPServer implements AutoCloseable{


    public static final int SERVER_PORT = 9999;
    public static final int BUFFER_SIZE = 2048;
    private ArrayList<Message> list;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean isWorking;

    public UDPServer() throws SocketException {
        socket = new DatagramSocket(SERVER_PORT);
        list = new ArrayList<Message>();
        packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        isWorking = true;
    }
    public  static void main(String[] args){
        try (UDPServer server = new UDPServer();){
            server.run();
            server.printList();
        }
            catch(Exception e){
            System.err.println("Error-" + e.getMessage());
            }

    }

    private void run()  {
        System.out.println("Server started");
        while(isWorking){
            receiveMessage();
            if(list.size()>500) isWorking = false;

        }
        System.out.println("Server closed");
    }

    private void printList() {
        StringBuilder sb = new StringBuilder();
        sb.append("message list\n=========================\n");
        for (var m: list){
            sb.append(m.toString()+"\n");
        }
        System.out.println(new String(sb));

    }

    private void receiveMessage()  {
        try{
            socket.receive(packet);
            Message received = (Message) Services.convertObjectFromBytes(packet.getData());
            if (received.getMessage()==null||received.getCommand()==null||received.getDate()==null){
                Message answer = new Message("Message format is wrong\n",Command.ADD);
                byte[] buf = Services.convertObjectToBytes(answer);
                packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                socket.send(packet);

            }
            else {
                received.setAddress(packet.getAddress());
                received.setPort(packet.getPort());
                switch (received.getCommand()) {
                    case ADD: {
                        list.add(new Message(packet.getAddress(), packet.getPort(), received.getMessage(), received.getDate()));
                        Message answer = new Message("Server: message received\n", Command.ADD);
                        byte[] buf = Services.convertObjectToBytes(answer);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }
                    case PING: {
                        Message answer = new Message("Server got ready!\n", Command.PING);
                        byte[] buf = Services.convertObjectToBytes(answer);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }
                    case TOP_MESSAGE: {
                        ArrayList<Message> listToSend = new ArrayList<>();
                        for (int i = list.size() - 1; i > list.size() - 11 && i >= 0; i--)
                            listToSend.add(list.get(i));
                        if(listToSend.size()==0)
                            listToSend.add(new Message("List is empty", received.getCommand()));
                        byte[] buf = Services.convertObjectToBytes(listToSend);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }
                    case CLIENT_MESSAGE: {
                        ArrayList<Message> onlyThisClientMessages = new ArrayList<Message>();
                        for (var x : list)
                            if (x.getAddress() == received.getAddress())
                                onlyThisClientMessages.add(x);
                        ArrayList<Message> listToSend = new ArrayList<>();
                        for (int i = onlyThisClientMessages.size() - 1; i > onlyThisClientMessages.size() - 11 && i >= 0; i--)
                            listToSend.add(onlyThisClientMessages.get(i));
                        if(listToSend.size()==0)
                            listToSend.add(new Message("List is empty", received.getCommand()));
                        byte[] buf = Services.convertObjectToBytes(listToSend);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }
                }
            }

        }
        catch(Exception e){
            System.out.println("Error serverReceiveMessage: ");
        }

    }

    @Override
    public void close() throws Exception {
        if (socket !=null && !socket.isClosed()) socket.close();
    }




}
