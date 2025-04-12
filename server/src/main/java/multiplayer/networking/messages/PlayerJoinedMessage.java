package multiplayer.networking.messages;

import multiplayer.entities.Vector2;

public class PlayerJoinedMessage extends GameMessage {
    private String id;
    private Vector2 position;

    public PlayerJoinedMessage(String id, Vector2 position) {
        super(MessageType.PLAYER_JOINED);
        this.id = id;
        this.position = position;
    }

}
