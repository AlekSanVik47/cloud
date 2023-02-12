package main;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button send;
    public ListView<String> listView;
    public ListView<String> listViewServer;
    public TextField text;
    public String enteredText;
    private List<File> clientFileList;
    private List<File> serverFileList;
    public static Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public void sendCommand(ActionEvent actionEvent) {
        System.out.println("SEND!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: init connect to server
        try{
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1000);
            clientFileList = new ArrayList<>();
            serverFileList = new ArrayList<>();
            String clientPath = "./client/src/main/resources/";
            String serverPath ="./server/src/main/resources/";
            File dir = new File(clientPath);
            File serverDir = new File(serverPath);
            if (!dir.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                clientFileList.add(file);
                listView.getItems().add(file.getName() + ": " + file.length());
            }
            listView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = listView.getSelectionModel().getSelectedItem();
                    File currentFile = findFileByName(fileName);
                    if (currentFile !=null) {
                        try {
                            os.writeUTF("./upload");
                            writeFile(fileName, currentFile);
                            //socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            filesOnServer(serverDir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filesOnServer(File serverDir) {
        if (!serverDir.exists()) {
            throw new RuntimeException("directory resource not exists on server");
        }
        for (File file : Objects.requireNonNull(serverDir.listFiles())) {
            serverFileList.add(file);
            listViewServer.getItems().add(file.getName() + ": " + file.length());
        }


//        text.setOnAction(b -> {
//            if (b.equals(serverFileList.contains(
//
//                    findFileByName(getEnteredText(enteredText))))) {
//                if (send.isPressed()) {
//                    String fileName = listViewServer.getSelectionModel().getSelectedItem();
//                    File currentFile = findFileByName(fileName);
//                    if (currentFile != null) {
//                        try {
//                            os.writeUTF("./download");
//                            writeFile(fileName, currentFile);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//        });
    }

    private String getEnteredText(String str) {
       text.setText(str);
        return text.getText();
    }

    private void writeFile(String fileName, File currentFile) throws IOException {
        os.writeUTF(fileName);
        os.writeLong(currentFile.length());
        FileInputStream fis = new FileInputStream(currentFile);
        byte[] buffer = new byte[1024];
        while (fis.available() > 0) {
            int bytesRead = fis.read(buffer);
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        String response = is.readUTF();
        System.out.println(response);
    }

    private File findFileByName(String fileName) {
        int endIndex = fileName.indexOf(':');
        String tempFileName = fileName.substring(0,endIndex);
        for (File file : clientFileList) {
            if (file.getName().equals(tempFileName)){
                return file;
            }
        }
        return null;
    }
    //downloadFromServer

}
