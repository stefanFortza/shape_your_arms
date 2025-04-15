package multiplayer.entities;

import java.util.List;
import java.util.Map;

public record GameState(
        Map<String, Player> players,
        List<Bullet> bullets) {

}
