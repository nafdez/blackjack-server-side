package core;

import org.fp.dam.naipes.blackjack.Blackjack;
import org.fp.dam.naipes.blackjack.BlackjackPedirException;
import org.fp.dam.naipes.blackjack.BlackjackPlantarseException;
import org.fp.dam.naipes.blackjack.BlackjackRepartirException;
import utils.MessageProcessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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

        try {
            DataInputStream dis = new DataInputStream(SOCKET.getInputStream());
            DataOutputStream dos = new DataOutputStream(SOCKET.getOutputStream());
            try {
                while (isPlaying) {
                    cmd = MessageProcessor.decodeMessage(dis.readUTF());
                    switch (cmd) {
                        case "N": // New game
                            logRequest("New game");
                            BLACKJACK.repartir();
                            dos.writeUTF(MessageProcessor.encodeMessage(BLACKJACK.toString()));
                            dos.flush();
                            break;
                        case "H": // Hit
                            logRequest("Hit");
                            BLACKJACK.pedir();
                            dos.writeUTF(MessageProcessor.encodeMessage(BLACKJACK.toString()));
                            break;
                        case "S": // Stand
                            logRequest("Stand");
                            BLACKJACK.plantarse();
                            dos.writeUTF(MessageProcessor.encodeMessage(BLACKJACK.toString()));
                            break;
                        case "F": // Finish game
                            logRequest("Finish");
                            isPlaying = false;
                            break;
                        default:
                            logRequest("Unknown command: " + cmd);
                            dos.writeUTF(MessageProcessor.encodeMessage(cmd));
                            break;
                    }
                }
            } catch (BlackjackRepartirException | BlackjackPedirException | BlackjackPlantarseException e) {
                logRequest("Game error: " + e.getClass());
                dos.writeUTF(MessageProcessor.encodeMessage(e.getMessage()));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logRequest(String message) {
        System.out.printf("(%s): %s%n", SOCKET.getInetAddress(), message);
    }
}
