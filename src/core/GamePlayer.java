package core;

import org.fp.dam.naipes.blackjack.Blackjack;
import org.fp.dam.naipes.blackjack.BlackjackPedirException;
import org.fp.dam.naipes.blackjack.BlackjackPlantarseException;
import org.fp.dam.naipes.blackjack.BlackjackRepartirException;
import utils.MessageDigest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GamePlayer implements Runnable {

    private final Socket SOCKET;
    private final Blackjack BLACKJACK;

    public GamePlayer(Socket clientSocket) {
        this.SOCKET = clientSocket;
        this.BLACKJACK = new Blackjack();
    }


    @Override
    public void run() {
        String cmd;
        boolean isPlaying = true;

        try (DataInputStream dis = new DataInputStream(SOCKET.getInputStream()); DataOutputStream dos = new DataOutputStream(SOCKET.getOutputStream())) {
            try {
                while (isPlaying) {
                    cmd = MessageDigest.decryptMessage(dis.readUTF());
                    switch (cmd) {
                        case "N": // New game
                            BLACKJACK.repartir();
                            dos.writeUTF(MessageDigest.encryptMessage(BLACKJACK.toString()));
                            break;
                        case "H": // Hit
                            BLACKJACK.pedir();
                            dos.writeUTF(MessageDigest.encryptMessage(BLACKJACK.toString()));
                        case "S": // Stand
                            BLACKJACK.plantarse();
                            dos.writeUTF(MessageDigest.encryptMessage(BLACKJACK.toString()));
                        case "F": // Finish game
                            isPlaying = false;
                    }
                }
            } catch (BlackjackRepartirException | BlackjackPedirException | BlackjackPlantarseException e) {
                dos.writeUTF(MessageDigest.encryptMessage(e.getMessage()));
            }
        } catch (IOException e) {
            // TODO: Send exception to client
        }
    }
}
