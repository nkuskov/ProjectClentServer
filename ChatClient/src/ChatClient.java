import java.io.*;
import java.net.Socket;

/**
 * Created by Nikolai on 12.08.2016.
 */
public class ChatClient implements Runnable {
    Socket socket = null;
    BufferedReader streamIn = null;
    DataOutputStream streamOut = null;
    Thread thread = null;
    ChatClientThread client = null;
    FileTransfer fileTransfer = null;

    ChatClient(String serverName, int serverPort) throws IOException { //Create new Socket and start Thread
        socket = new Socket(serverName, serverPort);
        System.out.println("Connected: " + socket);
        using();
        System.out.print("Write your nickname: ");

    }

    public void using(){
        System.out.println("Using of chat: \n " +
                            "\"- nickname message\" (after that you will send message to this client) \n " +
                            "\"- file filepath\" (you will send file to chose client)\n " +
                            "\"- clients\" (will show you list of clients)\n ");
    }

    public void open(){
        streamIn = new BufferedReader(new InputStreamReader(System.in));
        try {
            streamOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't open streams");
        }
    }

    public void start() throws IOException {  //start Thread, open IO streams, create ChatClientThread and start his Thread
        open();
        if(thread==null){
            client = new ChatClientThread(this,socket);
            thread = new Thread(this);
            thread.start();
            client.start();
        }
    }

    public void systemMessage(String msg){
        System.out.println(msg);
    }


    public void handle(String msg) throws IOException { //split msg and start file transfer by creating FileTransfer
        System.out.println("Handling[" + msg + "]");
        String[] splited = msg.split(" ");
        if(splited[0].equalsIgnoreCase("-") && splited[1].equalsIgnoreCase("getfile")) {
            client.file = true;
            fileTransfer = new FileTransfer(this, client, socket, Integer.parseInt(splited[2]));
            fileTransfer.start();

        }
        else {
            System.out.println(msg);
        }
    }



    @Override
    public void run() { // write to streamOut from console
        while (true){
            try {
                streamOut.writeUTF(streamIn.readLine());
                streamOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can't writeUTF");
            }
        }

    }

    public static void main(String[] args) throws IOException {
        ChatClient client = null;
        if(args.length<2){
            System.out.println("Usage: java ChatClient server port");
        }
        else {
            client =new ChatClient(args[0],Integer.parseInt(args[1]));
            client.start();
        }
    }
}
