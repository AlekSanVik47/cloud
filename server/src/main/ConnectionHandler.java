package main;


import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    public static final String UPLOAD = "./upload";
    public static final String END = "./end";
    public static final String DOWNLOAD = "./download";
    public static final String FILES_SERVER = "./filesServer";

    public TextField text;
    String serverPath = "./server/src/main/resources/";
    File serverFileDir = new File(serverPath);

    private DataInputStream is;
    private DataOutputStream os;
    private ObjectInputStream ois; // file -> ois ->


    public ConnectionHandler(Socket socket) throws IOException, InterruptedException {
        System.out.println("Connection accepted");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        Thread.sleep(2000);
//        Object msg = ois.readObject();
//        if (msg instanceof File) {
//
//        }
    }


    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                String command = is.readUTF();
                if (END.equals(command)) {
                    System.out.println("Client disconnected");
                    break;
                }
                if (UPLOAD.equals(command)) {
                    String fileName = is.readUTF();
                    System.out.println("fileName: " + fileName);
                    long fileLength = is.readLong();
                    System.out.println("fileLength: " + fileLength);
                    File file = new File(main.Server.serverPath + "/" + fileName);
                    if (!file.exists()) file.createNewFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for (long i = 0; i < (fileLength / 1024 == 0 ? 1 : fileLength / 1024); i++) {
                            int bytesRead = is.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            fos.flush();
                        }
                    }
                    os.writeUTF("OK. File uploaded successfully");
                }
                if (FILES_SERVER.equals(command)) {
                    new Controller().filesOnServer(serverFileDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private String readCommands() throws IOException {
//        return text.getText();
//    }
}

