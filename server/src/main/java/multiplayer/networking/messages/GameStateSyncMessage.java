package multiplayer.networking.messages;

import java.util.Map;
import java.util.stream.Collectors;

import multiplayer.entities.GameState;
import multiplayer.entities.entities_data.BulletData;
import multiplayer.entities.entities_data.PlayerData;

public class GameStateSyncMessage extends GameMessage {
    private Map<String, PlayerData> players;
    private Map<String, BulletData> bullets;

    public GameStateSyncMessage() {
        super(MessageType.GAME_STATE_SYNC);
    }

    public GameStateSyncMessage(GameState gameState) {
        super(MessageType.GAME_STATE_SYNC);

        this.players = gameState.getPlayers().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPlayerData()));
        this.bullets = gameState.getBullets().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBulletData()));
    }

    public Map<String, PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerData> players) {
        this.players = players;
    }

    public Map<String, BulletData> getBullets() {
        return bullets;
    }

    public void setBullets(Map<String, BulletData> bullets) {
        this.bullets = bullets;
    }

}
