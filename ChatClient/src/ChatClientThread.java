import java.io.*;
import java.net.Socket;

/**
 * Created by Nikolai on 12.08.2016.
 */
public class ChatClientThread extends Thread {
    ChatClient client = null;
    Socket socket = null;
    DataInputStream streamIn = null;
    BufferedInputStream buffIn = null;
    BufferedOutputStream fileWrite = null;


    ChatClientThread(ChatClient _client, Socket _socket) throws IOException {
        socket = _socket;
        client = _client;
        open();
    }


    void fileTransfer(int byteLength) throws IOException {
        byte[] byteArray = new byte[byteLength];


        fileWrite = new BufferedOutputStream(new FileOutputStream(client.getFileName()));
        buffIn.read(byteArray,0,byteLength);
        fileWrite.write(byteArray,0,byteLength);
        fileWrite.flush();
        fileWrite.close();
        client.handle("File ./world.txt writed with " + byteArray.length + " bytes size.");
    }

    public void open(){
        try {
            streamIn = new DataInputStream(socket.getInputStream());
            buffIn = new BufferedInputStream(socket.getInputStream());
        }
        catch (IOException e){
            client.systemMessage("Can't open stream in ChatClientThread");
        }
         // only for filetransfering

    }

    public void close(){
        try{
            if(streamIn != null) streamIn.close();
            if(buffIn != null) buffIn.close();
        }
        catch (IOException ioe){
            System.out.println("Error in closing streams" + ioe);
        }
    }

    public void run(){
        while (true){
            try {
                    client.handle(streamIn.readUTF());


            } catch (IOException e) {
                e.printStackTrace();
                client.systemMessage("Can't readUTF in ChatClient");
            }
        }

    }


}
