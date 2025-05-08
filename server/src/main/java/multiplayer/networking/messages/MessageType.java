package multiplayer.networking.messages;

public enum MessageType {
    // Incoming messages
    PLAYER_SHOOT("playerShoot"),
    PLAYER_MOVE_FROM_CLIENT("playerMoveFromClient"),

    // Outgoing messages
    GAME_STATE("gameState"),
    HIT("hit"),
    PLAYER_DEATH("playerDeath"),
    WELCOME("welcome"),
    PLAYER_LEFT("playerLeft"),
    PLAYER_JOINED("playerJoined"),
    INITIAL_GAME_STATE("initialGameState"),
    GAME_STATE_SYNC("gameStateSync"),
    PLAYER_MOVE_FROM_SERVER("playerMoveFromServer");

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