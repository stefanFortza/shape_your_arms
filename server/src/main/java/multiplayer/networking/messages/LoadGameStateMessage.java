package multiplayer.networking.messages;

public class LoadGameStateMessage extends GameMessage {

    public LoadGameStateMessage(String gameStateId) {
        super(MessageType.LOAD_GAME_STATE);
    }

}
