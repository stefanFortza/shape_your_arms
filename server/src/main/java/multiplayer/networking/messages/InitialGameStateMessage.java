package multiplayer.networking.messages;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import multiplayer.entities.Bullet;
import multiplayer.entities.GameState;
import multiplayer.entities.entities_data.PlayerData;

public class InitialGameStateMessage extends GameMessage {
    private Map<String, PlayerData> players;
    private List<Bullet> bullets;

    public InitialGameStateMessage(GameState gameState) {
        super(MessageType.INITIAL_GAME_STATE);

        this.players = gameState.getPlayers().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPlayerData()));

    }

    public Map<String, PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerData> players) {
        this.players = players;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }
}