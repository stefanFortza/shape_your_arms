package multiplayer.networking.messages;

public class ShootMessage extends GameMessage {
    protected ShootMessage(MessageType messageType) {
        super(messageType);
    }

    private double rotation;

    public double getRotation() {
        return rotation;
    }
}