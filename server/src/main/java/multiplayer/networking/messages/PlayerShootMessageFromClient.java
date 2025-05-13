package multiplayer.networking.messages;

public class PlayerShootMessageFromClient extends GameMessage {
    String playerId;

    public PlayerShootMessageFromClient() {
        super(MessageType.PLAYER_SHOOT_MESSAGE_FROM_CLIENT);
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

}