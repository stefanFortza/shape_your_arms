package multiplayer.networking.messages;

import org.dyn4j.geometry.Vector2;

import multiplayer.entities.entities_data.PlayerData;

public class PlayerJoinedMessage extends GameMessage {
    private PlayerData playerData;

    public PlayerJoinedMessage(PlayerData playerData) {
        super(MessageType.PLAYER_JOINED);
        this.playerData = playerData;
    }

}
