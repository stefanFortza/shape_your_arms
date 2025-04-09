package multiplayer.networking;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnMessageReceivedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.networking.web_socket_signal_data.OnErrorOccurredData;
import multiplayer.networking.web_socket_signal_data.OnServerStartedData;
import multiplayer.utils.Signal;

import java.net.InetSocketAddress;
import java.util.UUID;

public class WebSocketHandler extends WebSocketServer {

    public Signal<OnClientConnectedData> clientConnectedSignal = new Signal<>();
    public Signal<OnClientDisconnectedData> clientDisconnectedSignal = new Signal<>();
    public Signal<OnMessageReceivedData> messageReceivedSignal = new Signal<>();
    public Signal<OnErrorOccurredData> errorOccurredSignal = new Signal<>();
    public Signal<OnServerStartedData> serverStartedSignal = new Signal<>();

    private final GameServerCoordinator gameServerInstance;
    private final int port;

    public WebSocketHandler(int port, GameServerCoordinator gameServer) {
        super(new InetSocketAddress(port));
        this.port = port;
        this.gameServerInstance = gameServer;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String playerId = UUID.randomUUID().toString();
        conn.setAttachment(playerId); // Store playerId with the connection

        clientConnectedSignal.emit(new OnClientConnectedData(playerId, conn));

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String playerId = conn.getAttachment();
        if (playerId != null) {
            clientDisconnectedSignal.emit(new OnClientDisconnectedData(playerId));
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String playerId = conn.getAttachment();
        if (playerId != null) {
            messageReceivedSignal.emit(new OnMessageReceivedData(playerId, conn, message));
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error:");
        ex.printStackTrace();

        if (conn != null) {
            String playerId = conn.getAttachment();
            if (playerId != null) {
                errorOccurredSignal.emit(new OnErrorOccurredData(ex.getMessage(), ex));
            }
        }
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully on port " + port);
        serverStartedSignal.emit(new OnServerStartedData(port));
        setConnectionLostTimeout(30);
    }
}
