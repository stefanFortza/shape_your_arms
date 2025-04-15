package multiplayer.networking.messages;

import org.dyn4j.geometry.Vector2;

public class MoveMessageFromClient extends GameMessage {
    private Vector2 direction;

    protected MoveMessageFromClient(Vector2 direction) {
        super(MessageType.PLAYER_MOVE_FROM_CLIENT);
        this.direction = direction;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

}