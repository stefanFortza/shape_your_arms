package multiplayer.networking.messages;

import multiplayer.entities.entities_data.PlayerData;

public class WelcomeMessage extends GameMessage {
    private String playerId;
    private PlayerData playerData;

    public WelcomeMessage(String playerId, PlayerData playerData) {
        super(MessageType.WELCOME);
        this.playerId = playerId;
        this.playerData = playerData;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

}
