package core;

import org.fp.dam.naipes.blackjack.Blackjack;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    ExecutorService cachedPool = Executors.newCachedThreadPool();

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on <host>:<port> " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
            listen(serverSocket);
        } catch (IOException e) {
            System.out.println("Error initializing the server");
        }
    }

    @SuppressWarnings("all")
    private void listen(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                cachedPool.submit(new GamePlayer(clientSocket));
                System.out.printf("(%s): Game started%n", clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            System.out.println("Error while waiting for clients: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server(9999);
    }
}
