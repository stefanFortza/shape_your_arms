package multiplayer.networking.messages;

public class ShootMessage extends GameMessage {
    protected ShootMessage(MessageType messageType) {
        super(messageType);
        // TODO Auto-generated constructor stub
    }

    private double rotation;

    public double getRotation() {
        return rotation;
    }
}