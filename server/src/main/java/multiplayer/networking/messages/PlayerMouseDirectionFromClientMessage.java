package multiplayer.networking.messages;

import org.dyn4j.geometry.Vector2;

public class PlayerMouseDirectionFromClientMessage extends GameMessage {
    private String playerId;
    private Vector2 mouseDirection;

    public PlayerMouseDirectionFromClientMessage(String playerId, Vector2 mouseDirection) {
        super(MessageType.PLAYER_MOUSE_DIRECTION_FROM_CLIENT);
        this.playerId = playerId;
        this.mouseDirection = mouseDirection;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Vector2 getMouseDirection() {
        return mouseDirection;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setMouseDirection(Vector2 mouseDirection) {
        this.mouseDirection = mouseDirection;
    }

}