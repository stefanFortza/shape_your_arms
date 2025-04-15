package multiplayer.networking.messages.move_messages;

public enum MoveMessageType {
    MOVEMENT_STARTED("MovementStarted"),
    MOVEMENT_STOPPED("MovementStopped");

    private final String stringValue; // Store the string value

    MoveMessageType(String stringValue) {
        this.stringValue = stringValue; // Use the string value
    }

    public static MoveMessageType fromString(String text) {
        for (MoveMessageType mt : MoveMessageType.values()) {
            if (mt.getStringValue().equalsIgnoreCase(text)) {
                return mt;
            }
        }
        throw new IllegalArgumentException("Unknown MoveMessageType: " + text);
    }

    // ...existing code...

    public String getStringValue() {
        return stringValue; // Add a getter method
    }
}