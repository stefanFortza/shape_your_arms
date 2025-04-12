package multiplayer.networking.messages;

public enum MessageType {
    // Incoming messages
    PLAYER_SHOOT("PlayerShoot"),
    PLAYER_MOVE_FROM_CLIENT("PlayerMoveFromClient"),

    // Outgoing messages
    GAME_STATE("GameState"),
    HIT("Hit"),
    PLAYER_DEATH("PlayerDeath"),
    WELCOME("Welcome"),
    PLAYER_LEFT("PlayerLeft"),
    PLAYER_JOINED("PlayerJoined"),
    INITIAL_GAME_STATE("InitialGameState"),
    PLAYER_MOVE_FROM_SERVER("PlayerMoveFromServer");

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