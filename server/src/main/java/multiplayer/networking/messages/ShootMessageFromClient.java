package multiplayer.networking.messages;

public class ShootMessageFromClient extends GameMessage {

    public ShootMessageFromClient() {
        super(MessageType.PLAYER_SHOOT);
    }
}