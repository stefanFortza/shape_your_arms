package multiplayer.networking.messages;

import multiplayer.entities.entities_data.PlayerData;

public class PlayerLeftMessage extends GameMessage {
    private PlayerData playerData;

    public PlayerLeftMessage(PlayerData playerData) {
        super(MessageType.PLAYER_LEFT);
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

}
