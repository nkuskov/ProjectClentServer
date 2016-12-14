//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.Socket;
//
///**
// * Created by Nikolai on 22.08.2016.
// */
//public class FileTransfer implements Runnable {
//    Socket socket = null;
//    Thread thread = null;
//    BufferedOutputStream fileOut = null;
//    InputStream fileIn = null;
//    byte[] byteArray = null;
//    Integer fileLength = 0;
//    ChatClient client = null;
//    String filePath = "d:/Education/Java/SuperServer/World.txt";
//    ChatClientThread chatClientThread = null;
//
//
//
//    FileTransfer(ChatClient _client, ChatClientThread _chatClientThread, Socket _socket, Integer _fileLength){
//        client = _client;
//        chatClientThread = _chatClientThread;
//        socket = _socket;
//        fileLength = _fileLength;
//    }
//
//
//
//    public void start(){
//        thread = new Thread(this);
//        thread.start();
//
//    }
//
//
//
//    @Override
//    public void run() {
//        try {
//
//            client.systemMessage("Start file sending");
//            fileIn = socket.getInputStream();
//            byteArray = new byte[fileLength];// taking fileLength from splited msg
//            fileIn.read(byteArray,0,byteArray.length); // write to bytearray from StreamIn
//            fileOut = new BufferedOutputStream(new FileOutputStream(filePath));
//            fileOut.write(byteArray,0,byteArray.length);// write from byteArray to new file
//            fileOut.flush();
//            client.handle("File " + filePath + " writed with " + byteArray.length + " bytes size.");
//            client.systemMessage("File sending done.");
//            fileOut.close();
//            socket.shutdownInput();
//
//            client.open();
//            if(socket == null){
//                client.systemMessage("socket = null!!");
//            }
//            chatClientThread.open();
//            chatClientThread.file = false;
//
//
//
//
//        }
//        catch (IOException e){
//
//        }
//
//    }
//}
