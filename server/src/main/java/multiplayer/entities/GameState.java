package multiplayer.entities;

import java.util.Map;

public class GameState {
    private Map<String, Player> players;
    private Map<String, Bullet> bullets;

    public GameState(Map<String, Player> players, Map<String, Bullet> bullets) {
        this.players = players;
        this.bullets = bullets;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Map<String, Bullet> getBullets() {
        return bullets;
    }

}
