package multiplayer.networking.messages;

public class WelcomeMessage extends GameMessage {
    private String playerId;

    public WelcomeMessage(String playerId) {
        super(MessageType.WELCOME);
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
