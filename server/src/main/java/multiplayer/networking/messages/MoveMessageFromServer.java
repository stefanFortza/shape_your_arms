package multiplayer.networking.messages;

import org.dyn4j.geometry.Vector2;

public class MoveMessageFromServer extends GameMessage {

    private Vector2 position;

    protected MoveMessageFromServer(Vector2 position) {
        super(MessageType.PLAYER_MOVE_FROM_SERVER);
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 direction) {
        this.position = direction;
    }

}