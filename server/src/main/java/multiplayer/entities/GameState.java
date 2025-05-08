package multiplayer.entities;

import java.util.List;
import java.util.Map;

public class GameState {
    private Map<String, Player> players;
    private List<Bullet> bullets;

    public GameState(Map<String, Player> players, List<Bullet> bullets) {
        this.players = players;
        this.bullets = bullets;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
