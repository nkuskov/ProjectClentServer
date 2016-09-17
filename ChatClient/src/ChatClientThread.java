import java.io.*;
import java.net.Socket;

/**
 * Created by Nikolai on 12.08.2016.
 */
public class ChatClientThread extends Thread {
    ChatClient client = null;
    Socket socket = null;
    DataInputStream streamIn = null;
    boolean file = false;
    InputStream fileIn = null;

    ChatClientThread(ChatClient _client, Socket _socket) throws IOException {
        socket = _socket;
        client = _client;
        open();
    }



    public void open(){
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e){
            client.systemMessage("Can't open stream in ChatClientThread");
        }
         // only for filetransfering

    }

    public void run(){
        while (true){
            try {
                if (!file) { //check filetransfer
                    client.handle(streamIn.readUTF());
                }

            } catch (IOException e) {
                e.printStackTrace();
                client.systemMessage("Can't readUTF in ChatClient");
            }
        }

    }


}
