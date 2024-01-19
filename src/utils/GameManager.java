package utils;

import org.fp.dam.naipes.blackjack.Blackjack;

import java.util.HashMap;

public class GameManager {

    private static GameManager instance;
    private final HashMap<String, Blackjack> GAMES = new HashMap<String, Blackjack>();

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    public String newGame(Blackjack game) {
        String gameId = String.valueOf(game.hashCode());
        GAMES.put(gameId, game);
        return gameId;
    }

    public Blackjack getGame(String id) {
        return GAMES.get(id);
    }

    public void removeGame(String id) {
        GAMES.remove(id);
    }
}
