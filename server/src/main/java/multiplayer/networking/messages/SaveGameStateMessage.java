package multiplayer.networking.messages;

public class SaveGameStateMessage extends GameMessage {

    public SaveGameStateMessage() {
        super(MessageType.SAVE_GAME_STATE);
    }

}
