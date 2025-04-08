package multiplayer;

import javax.websocket.server.ServerEndpointConfig;

public class GameEndpointConfigurator extends ServerEndpointConfig.Configurator {
    private static GameServer gameServer;

    public static void setGameServer(GameServer server) {
        gameServer = server;
    }

    public static GameServer getGameServer() {
        return gameServer;
    }
}