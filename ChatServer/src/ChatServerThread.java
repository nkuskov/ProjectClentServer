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
        server.systemMessage("spliting message \"" + msg + "\" from <" +name + ">");
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
                server.handle(name,"File " + split[2] + " start sending...");
                server.fileTransfer(clientName, split[2]);
            }
        }
    }

    public void fileTransfer(String fileName){  // Sending file
        try{

            File myFile = new File(fileName);  // create new File
            server.handle(name,"Start \"" + fileName + "\" file sending...");
            byte[] byteArray = new byte[(int)myFile.length()]; // create byte[]
            fileInStream = new BufferedInputStream(new FileInputStream(myFile)); // open IO streams
            fileInStream.read(byteArray,0,byteArray.length); // write file to byte[]
            server.handle(name,"Sending " + myFile.getAbsolutePath());
            streamOut.writeUTF("- getfile " + byteArray.length);
            fileOutStream = socket.getOutputStream();
            fileOutStream.write(byteArray,0,byteArray.length);//send file to streamOut from byte[]
            fileOutStream.flush();
            open();
            server.handle(name,"File " + myFile.getAbsolutePath()+" transfered with " + byteArray.length + " bytes size");
            if(socket == null) server.systemMessage("Socket is closed");
        }
        catch (Exception e){
            try {
                splitMsg("Write correct file path");
            }
            catch (IOException ex){

            }
            server.systemMessage("FileTransfer failed");
        }

    }




    public void send(String msg) throws IOException { // send msg ot OutputStream
        streamOut.writeUTF(msg);
        streamOut.flush();
    }

    public void run(){  // if you don't choose to whom you will send message,
        while(true) {   // you should write "- nickname message"
            if (this.name == null) {
                try {
                    this.name = streamIn.readUTF();
                    System.out.println(socket + " nickname is: " + this.name);
                } catch (IOException e) {
                    server.systemMessage("readUTF for [tacking name] in ChatServerThread doesn't work");
                }
            }
            try{
                splitMsg(streamIn.readUTF());
            }
            catch (IOException e){
                if(socket == null){
                    server.systemMessage("socket is closed");
                }
                server.systemMessage("readUTF for [split msg] in ChatServerThread doesn't work");


            }
        }

    }

    public void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
            streamOut = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e){
            server.systemMessage("Can't open streams in ChatServerThread");
        }

    }


}
