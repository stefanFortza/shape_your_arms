package multiplayer.networking;

public enum MessageType {
    GAME_STATE("GameState"),
    HIT("Hit"),
    PLAYER_DEATH("PlayerDeath"),
    WELCOME("Welcome"),
    PLAYER_LEFT("PlayerLeft");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MessageType fromString(String type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.type.equals(type)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + type);
    }
}