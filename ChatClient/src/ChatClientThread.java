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
        start();
    }



    public void open() throws IOException {
        streamIn = new DataInputStream(socket.getInputStream());
        fileIn = socket.getInputStream(); // only for filetransfering
    }

    public void run(){
        while (true){
            try {
                if (!file) { //check filetransfer
                    client.handle(streamIn.readUTF());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
