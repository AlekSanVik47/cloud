package main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public final static String serverPath = "./server/src/main/resources";

    public static void main(String[] args) {
        Socket socket;
        try (ServerSocket server = new ServerSocket(8189)) {
            ExecutorService executor = Executors.newFixedThreadPool(4);
            System.out.println("server started");
            while (true) {
                socket = server.accept();
                executor.execute(new main.ConnectionHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
