import java.io.*;
import java.net.Socket;

/**
 * Created by Nikolai on 12.08.2016.
 */
public class ChatClient implements Runnable {
    Socket socket = null;
    BufferedReader consoleIn = null;
    DataOutputStream streamOut = null;
    Thread thread = null;
    ChatClientThread clientThread = null;
    Boolean running = true;

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
                            "\"- clients\" (will show you list of clients)\n " +
                            "\"- bye \" (will exit from the chat)\n");
    }

    public void open(){
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
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
            clientThread = new ChatClientThread(this,socket);
            thread = new Thread(this);
            thread.start();
            clientThread.start();
        }
    }

    public void systemMessage(String msg){
        System.out.println(msg);
    }


    public void handle(String msg) throws IOException { //split msg and start file transfer by creating FileTransfer
        System.out.println("Handling[" + msg + "]");
        String[] splited = msg.split(" ");
        if(splited[0].equalsIgnoreCase("-") && splited[1].equalsIgnoreCase("bye")){
            System.out.println("Good bye. Press RETURN to exit...");
            stop();
        }
        if(splited[0].equalsIgnoreCase("-") && splited[1].equalsIgnoreCase("getfile")) {
           clientThread.fileTransfer(Integer.parseInt(splited[2]));

        }
        else {
            System.out.println(msg);
        }
    }

    public String getFileName() throws IOException {
        System.out.println("Write file path: ");
        running = false;
        String fileName = consoleIn.readLine();
        System.out.println("File path: " + fileName);
        running = true;
        return fileName;
    }


    public void stop(){
        if(thread != null){
            thread.stop();
            thread = null;
        }

        try{
            if(consoleIn != null) consoleIn.close();
            if(streamOut != null) streamOut.close();
            if(socket != null) socket.close();
        }
        catch (IOException ioe){
            System.out.println("Error closing...");
            clientThread.close();
            clientThread.stop();

        }
    }

    @Override
    public void run() { // write to streamOut from console
        while (true){
            while (!running){
                thread.yield();
            }
            try {
                streamOut.writeUTF(consoleIn.readLine());
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
            client = new ChatClient(args[0],Integer.parseInt(args[1]));
            client.start();
        }
    }
}
