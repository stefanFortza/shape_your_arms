use std::collections::HashMap;

use godot::classes::INode;
use godot::prelude::*;

use crate::entities::player::Player;
use crate::network::messages::message_type::MessageType;
use crate::network::network_manager::NetwornkManager;

// GameManager.rs
#[derive(GodotClass)]
#[class(base=Node)]
pub struct GameWorldManager {
    local_player_id: Option<GString>,

    players: HashMap<String, Gd<Player>>,
    player_scene: OnReady<Gd<PackedScene>>,

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for GameWorldManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            local_player_id: None,
            players: HashMap::new(),
            player_scene: OnReady::from_loaded("res://scenes/main_game/player_scene.tscn"),
            base,
        }
    }
    fn ready(&mut self) {
        // Connect to NetworkManager signals
        let network_manager = self
            .base()
            .try_get_node_as::<NetwornkManager>("./Network/NetworkManager");

        if let Some(mut network_manager) = network_manager {
            let this = self.to_gd();

            network_manager
                .signals()
                .welcome_message_received()
                .connect_obj(&this, Self::on_welcome_message_received);

            network_manager
                .signals()
                .game_state_sync_received()
                .connect_obj(&this, Self::on_game_state_sync_received);
        } else {
            godot_print!("NetworkManager not found");
        }
    }
}

#[godot_api]
impl GameWorldManager {
    #[func]
    fn on_welcome_message_received(&mut self, player_id: GString) {
        self.local_player_id = Some(player_id.clone());
        godot_print!("Welcome message received with player ID: {:?}", player_id);
    }

    #[func]
    fn on_game_state_sync_received(&mut self, message: GString) {
        // Deserialize and update players/game state
        let parsed_message = MessageType::from_json(message.to_string().as_str()).unwrap();
        if let MessageType::GameStateSync { players, .. } = parsed_message {
            for (id, player_data) in players {
                if let Some(player) = self.players.get_mut(&id) {
                    // Update existing player
                    player.bind_mut().apply_network_state(player_data);
                } else {
                    // Create new player
                    let mut new_player = self.player_scene.instantiate_as::<Player>();
                    new_player.bind_mut().apply_network_state(player_data);
                    self.base_mut().add_child(&new_player);
                    self.players.insert(id.clone(), new_player);
                }
            }
        }
    }
}
