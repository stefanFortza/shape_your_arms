package multiplayer.networking.web_socket_signal_data;

public record OnErrorOccurredData(String errorMessage, Exception exception) {
}
