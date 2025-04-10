package multiplayer.entities.game_world_signal_data;

import multiplayer.entities.GameState;
import multiplayer.entities.Player;

public record OnPlayerJoinedGameWorldData(Player newPlayer, GameState gameState) {
}