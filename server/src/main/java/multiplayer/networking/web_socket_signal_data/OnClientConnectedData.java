package multiplayer.networking.web_socket_signal_data;

import org.java_websocket.WebSocket;

public record OnClientConnectedData(String playerId, WebSocket connection) {
}
