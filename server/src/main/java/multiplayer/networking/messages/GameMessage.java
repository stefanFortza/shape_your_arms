package multiplayer.networking.messages;

public abstract class GameMessage {
    private String type;

    protected GameMessage(String type) {
        this.type = type;
    }

    // Alternative constructor using enum
    protected GameMessage(MessageType messageType) {
        this.type = messageType.getType();
    }

    public String getType() {
        return type;
    }
}