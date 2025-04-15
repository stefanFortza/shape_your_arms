package multiplayer.entities;

import java.util.List;
import java.util.Map;

import multiplayer.entities.entities_data.PlayerData;

public record GameState(
                Map<String, PlayerData> players,
                List<Bullet> bullets) {

}
