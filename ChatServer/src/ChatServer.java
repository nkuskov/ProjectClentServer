import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Nikolai on 05.08.2016.
 * This class create ServerSocket and massive with Socket's. Also He has method to
 * find current Client by his name, send him a ClientsMap and TransferFile
 */
public class ChatServer implements Runnable{
    private ChatServerThread clients[] = new ChatServerThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;
    String chatUsing = "Using - nickname message";

    public ChatServer(int port){ //constructor create ServerSocket and start Thread for adding new Sockets
        try{
            System.out.println("Binding ro the port " + port + ", please wait");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
        }

        catch(Exception e){
            System.out.println(e);
        }
    }



    @Override
    public void run() {     //loop create new Socket and add it in massive
        while(thread!=null){
            try{
                System.out.println("Waiting for the client...");
                addThread(server.accept());
            }
            catch(Exception e){
                System.out.println(e);
                stop();
            }
        }
    }

    public synchronized Integer findClient(String name){ //method which find current Client by his name.
        int number = -1;                                   // return index of this client
        for (int i = 0; i < clientCount; i++) {
            if(name.equalsIgnoreCase(clients[i].name)){
                number = i;
            }

        }
        return number;

    }

    public synchronized void clientsMap(String name){ //get full clients map and send it to current client
        int j = findClient(name);
        for (int i = 0; i < clientCount; i++) {
            try {
                clients[j].send(clients[i].name);

            } catch (IOException e) {

            }
        }
    }

    public synchronized void handle(String name, String input){  //send message to current client
        try{
            clients[findClient(name)].send(input);
        }
        catch (IOException e){

        }

    }

    public synchronized void fileTransfer(String name, String file){ // transfer file to current client
        clients[findClient(name)].fileTransfer(file);
    }

//    public synchronized void remove(int ID){
//        int pos = findClient(ID);
//        if (pos>=0){
//            ChatServerThread toTerminate = clients[pos];
//            System.out.println("Removing client thread" + ID + " at" + pos);
//            if(pos<clientCount - 1){
//                for (int i = pos+1; i < clientCount; i++) {
//                    clients[i-1] = clients[i];
//
//                }
//            }
//            clientCount--;
//            try{
//                toTerminate.close();
//            }
//            catch (Exception e){
//                System.out.println(e);
//                toTerminate.stop();
//            }
//
//        }
//    }



    public void addThread(Socket socket){  // create massive with clients by adding new Sockets on it
        if(clientCount < clients.length){ //and start his Thread
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ChatServerThread(this,socket);
            try{
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;

            }
            catch(Exception e){
                System.out.println(e);

            }
        }
    }

    public void start(){    //create a Thread(ChatServer) and start() it
        if(thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop(){
        if(thread != null){
            thread.stop();
            thread = null;
        }
    }

    public static void main(String[] args) {    // Create new constructor with ServerSocket with current port from console
        ChatServer server = null;
        if(args.length!=1){
            System.out.println("Usage: java ChatServer4 port");
        }
        else {
            server = new ChatServer(Integer.parseInt(args[0]));
            server.start();
        }
    }


}




























