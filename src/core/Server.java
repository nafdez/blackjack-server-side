package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    ExecutorService cachedPool = Executors.newCachedThreadPool();

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on <host>:<port> " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
            listen(serverSocket);
        } catch (IOException e) {
            System.out.println("Error initializing the server");
        }
    }

    @SuppressWarnings("all")
    private void listen(ServerSocket serverSocket) {
        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                clientSocket.setSoTimeout(3000);
                cachedPool.submit(new GamePlayer(clientSocket));
                System.out.printf("(%s): Game started%n", clientSocket.getInetAddress());
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout" + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error while waiting for clients: " + e.getMessage());
            }
        }
    }
}
