// Rename to GameStateManager
package multiplayer.entities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import multiplayer.entities.entities_data.PlayerData;
import multiplayer.entities.game_world_signal_data.OnPlayerJoinedGameWorldData;
import multiplayer.entities.game_world_signal_data.OnPlayerLeftGameWorldData;
import multiplayer.gui.GameWorldGUI;
import multiplayer.gui.framework.SimulationBody;
import multiplayer.networking.NetworkManager;
import multiplayer.networking.messages.MessageType;
import multiplayer.networking.GameServerCoordinator;
import multiplayer.networking.web_socket_signal_data.OnClientConnectedData;
import multiplayer.networking.web_socket_signal_data.OnClientDisconnectedData;
import multiplayer.utils.Signal;

/**
 * Handles all game logic and state
 */
public class GameWorld {
    public Signal<OnPlayerJoinedGameWorldData> playerJoinedGameWorldSignal = new Signal<>(
            "playerJoinedGameWorldSignal");
    public Signal<OnPlayerLeftGameWorldData> playerLeftGameWorldSignal = new Signal<>("playerLeftGameWorldSignal");

    private final World<SimulationBody> world = new World<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final NetworkManager messageBroker;
    private final GameServerCoordinator gameServerCoordinator;
    private final GameWorldGUI gameWorldGUI;
    private float timer = 0;

    public GameWorld(NetworkManager messageBroker, GameServerCoordinator gameServerCoordinator) {
        this.messageBroker = messageBroker;
        this.gameServerCoordinator = gameServerCoordinator;

        gameWorldGUI = new GameWorldGUI(world);
        gameWorldGUI.run();
        System.out.println("GameWorld initialized");
    }

    public void initSignalHandlers() {
        // Initialize signal handlers if needed
        gameServerCoordinator.clientConnectedSignal.connect(this::onClientConnected);
        gameServerCoordinator.clientDisconnectedSignal.connect(this::onClientDisconnected);
    }

    public void onClientConnected(OnClientConnectedData data) {

        Player newPlayer = new Player(new Vector2().zero(), data.playerId());
        // Create a new player at a random position
        players.put(data.playerId(), newPlayer);
        this.world.addBody(newPlayer);

        playerJoinedGameWorldSignal.emit(new OnPlayerJoinedGameWorldData(newPlayer, getGameState()));
    }

    public void onClientDisconnected(OnClientDisconnectedData data) {
        Player player = players.remove(data.playerId());

        playerLeftGameWorldSignal.emit(new OnPlayerLeftGameWorldData(player));
    }

    public GameState getGameState() {

        Map<String, PlayerData> playersCopy = new HashMap<>();

        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            playersCopy.put(entry.getKey(), player.getPlayerData());
        }

        // Return a copy of the game state
        // Map<String, Player> playersCopy = new HashMap<>(players);
        List<Bullet> bulletsCopy = new ArrayList<>(bullets);
        return new GameState(playersCopy, bulletsCopy);
    }

    public void handlePlayerMove(String playerId, JsonObject message) {
        // TODO: Handle player movement
        // Player player = players.get(playerId);
        // if (player != null) {
        // double x = message.get("x").getAsDouble();
        // double y = message.get("y").getAsDouble();

        // double rotation = message.has("rotation") ?
        // message.get("rotation").getAsDouble() : player.getRotation();

        // player.setPosition(new Vector2(x, y));
        // player.setRotation(rotation);

        // }
    }

    public void handlePlayerShoot(String playerId, JsonObject message) {
        Player player = players.get(playerId);
        if (player != null) {
            double rotation = message.get("rotation").getAsDouble();

            // Calculate direction vector from rotation
            double dirX = Math.cos(rotation);
            double dirY = Math.sin(rotation);

            // TODO
            // Create bullet at player's position
            // Bullet bullet = new Bullet(
            // playerId,
            // player.getX(),
            // player.getY(),
            // dirX,
            // dirY);

            // bullets.add(bullet);

        }
    }

    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer > 1) {
            timer = 0;
            JsonObject initialGameState = new JsonObject();
            Gson gson = new Gson();
            initialGameState.addProperty("type", MessageType.INITIAL_GAME_STATE.getType());
            initialGameState.add("players", gson.toJsonTree(getGameState().players()));
            initialGameState.add("bullets", gson.toJsonTree(getGameState().bullets()));
            System.out.println(initialGameState.toString());

            // Broadcast the game state to all clients
            // gameServerCoordinator.broadcastGameState(players, bullets);

        }
        this.gameWorldGUI.gameLoop();
        // this.world.update(deltaTime);
        updateBullets(deltaTime);
    }

    private void updateBullets(float deltaTime) {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(deltaTime);

            // Remove bullets that are out of bounds or expired
            if (bullet.isExpired()) {
                bulletIterator.remove();
            }
        }
    }

}