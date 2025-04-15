package multiplayer.networking.messages.move_messages;

import com.google.gson.*;
import java.lang.reflect.Type;

public class MoveMessageTypeAdapter implements JsonDeserializer<MoveMessageType>, JsonSerializer<MoveMessageType> {

    @Override
    public MoveMessageType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // The JSON contains the string value (e.g., "MovementStarted" or
        // "MovementStopped")
        String rawValue = json.getAsString();
        return MoveMessageType.fromString(rawValue);
        // Assumes your enum has a custom fromString method that matches these exact
        // strings
    }

    @Override
    public JsonElement serialize(MoveMessageType moveType, Type typeOfSrc, JsonSerializationContext context) {
        // Serialize enum back to its string value
        return new JsonPrimitive(moveType.getStringValue());
    }
}