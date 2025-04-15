package multiplayer.networking.messages.move_messages;

import org.dyn4j.geometry.Vector2;

import multiplayer.networking.messages.GameMessage;
import multiplayer.networking.messages.MessageType;

public class MoveMessageFromClient extends GameMessage {
    private String playerId;

    private Vector2 direction;
    private MoveMessageType moveMessageType;

    protected MoveMessageFromClient(String playerId, Vector2 direction, MoveMessageType moveMessageType) {
        super(MessageType.PLAYER_MOVE_FROM_CLIENT);
        this.direction = direction;
        this.moveMessageType = moveMessageType;
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public MoveMessageType getMoveMessageType() {
        return moveMessageType;
    }

    public void setMoveMessageType(MoveMessageType moveMessageType) {
        this.moveMessageType = moveMessageType;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

}