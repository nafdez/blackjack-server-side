package core;

import utils.MessageDigest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on <host>:<port> " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
            new Thread(() -> {
                try {
                    listen(serverSocket);
                } catch (IOException e) {
                    System.out.println("IOException");
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Failed to initialize Server");
        }
    }

    private void listen(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            client.setSoTimeout(3000);

            try (DataInputStream dis = new DataInputStream(client.getInputStream()); DataOutputStream dos = new DataOutputStream(client.getOutputStream())) {
                String request = MessageDigest.decryptMessage(dis.readUTF());
                System.out.printf("(%s): %s%n", client.getInetAddress(), request);
                dos.writeUTF(MessageDigest.encryptMessage(request));
                dos.flush();
            } catch (SocketTimeoutException e) {
                System.out.printf("(%s): Timeout%n", client.getInetAddress());
            }
        }
    }
}
