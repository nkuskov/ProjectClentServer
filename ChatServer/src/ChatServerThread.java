import java.io.*;
import java.net.Socket;

/**
 * Created by Nikolai on 12.08.2016.
 *
 */

public class ChatServerThread extends Thread {
    Socket socket = null;
    DataInputStream streamIn = null;
    DataOutputStream streamOut = null;
    ChatServer server = null;
    String name = null;
    String clientName = null;
    String fileName = null;
    BufferedInputStream fileInStream = null;
    OutputStream fileOutStream = null;



    ChatServerThread(ChatServer _server, Socket _socket){ //
        super();
        socket = _socket;
        server = _server;


    }



    public void splitMsg(String msg) throws IOException {  //split income message for send correct command
        String[] split = msg.split(" ");
        if(split[0].equalsIgnoreCase("-") && split[1].equalsIgnoreCase("clients")){ // send ClientsMap
            server.clientsMap(name);
            System.out.println("command " + split[1]);

        }else {
            if (split[0].equalsIgnoreCase("-") && server.findClient(split[1]) != -1){ // send message to current Client
            clientName = split[1];                                                    // by using his clientName
            String s = "";
            for (int i = 2; i < split.length; i++) {
                s += split[i];

                }
            server.handle(clientName, name + ": " + s);
            }
            else {
                if (clientName != null) {
                    server.handle(clientName, name + ": " + msg);
                }
            }

        }
        if(split[0].equalsIgnoreCase("-") && split[1].equalsIgnoreCase("file")){ // for sending file.
            if(clientName != null){
                server.fileTransfer(clientName, split[2]);
            }
        }
    }

    public void fileTransfer(String fileName){  // Sending file
        try{
            File myFile = new File(fileName);  // create new File
            byte[] byteArray = new byte[(int)myFile.length()]; // create byte[]
            fileInStream = new BufferedInputStream(new FileInputStream(myFile)); // open IO streams
            fileInStream.read(byteArray,0,byteArray.length); // write file to byte[]
            send("Sending " + myFile.getAbsolutePath());
            streamOut.writeUTF("- getfile " + byteArray.length);
            fileOutStream = socket.getOutputStream();
            fileOutStream.write(byteArray,0,byteArray.length);//send file to streamOut from byte[]
            fileOutStream.flush();
            send("File " + myFile.getAbsolutePath()+" transfered with " + byteArray.length + " bytes size");
            if(fileInStream != null) fileInStream.close();
            if(fileOutStream != null) fileOutStream.close();
        }
        catch (Exception e){
            System.out.println("Write correct filepath");
        }
    }




    public void send(String msg) throws IOException { // send msg ot OutputStream
        streamOut.writeUTF(msg);
        streamOut.flush();
    }

    public void run(){  // if you dont choose to whome you will send message,
        while(true) {   // you shoud write "- nickname message"
            if (this.name == null) {
                try {
                    this.name = streamIn.readUTF();
                    System.out.println(socket + " nickname is: " + this.name);
                } catch (IOException e) {

                }
            }
            try{
                splitMsg(streamIn.readUTF());
            }
            catch (IOException e){

            }
        }

    }

    public void open() throws IOException {
        streamIn = new DataInputStream(socket.getInputStream());
        streamOut = new DataOutputStream(socket.getOutputStream());

    }


}
