use std::collections::HashMap;

use godot::classes::class_macros::registry::signal;
use godot::classes::{Camera2D, INode};
use godot::prelude::*;

use crate::entities::bullet::Bullet;
use crate::entities::player::Player;
use crate::network::messages::bullet_data::BulletData;
use crate::network::messages::message_type::MessageType;
use crate::network::messages::player_data::PlayerData;
use crate::network::network_manager::NetwornkManager;

// GameManager.rs
#[derive(GodotClass)]
#[class(base=Node)]
pub struct GameWorldManager {
    local_player_id: Option<GString>,

    players: HashMap<String, Gd<Player>>,
    bullets: HashMap<String, Gd<Bullet>>,
    player_scene: OnReady<Gd<PackedScene>>,
    bullet_scene: OnReady<Gd<PackedScene>>,
    network_manager: OnReady<Gd<NetwornkManager>>,

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for GameWorldManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            local_player_id: None,
            players: HashMap::new(),
            bullets: HashMap::new(),
            player_scene: OnReady::from_loaded("res://scenes/main_game/player_scene.tscn"),
            bullet_scene: OnReady::from_loaded("res://scenes/main_game/bullet_scene.tscn"),
            network_manager: OnReady::from_node("./Network/NetworkManager"),
            base,
        }
    }
    fn ready(&mut self) {
        // Connect to NetworkManager signals
        let this = self.to_gd();

        self.network_manager
            .signals()
            .welcome_message_received()
            .connect_obj(&this, Self::on_welcome_message_received);

        self.network_manager
            .signals()
            .game_state_sync_received()
            .connect_obj(&this, Self::on_game_state_sync_received);

        self.network_manager
            .signals()
            .player_joined_message_received()
            .connect_obj(&this, Self::on_player_joined_message_received);

        self.network_manager
            .signals()
            .player_left_message_received()
            .connect_obj(&this, Self::on_player_left_message_received);
    }
}

#[godot_api]
impl GameWorldManager {
    #[signal]
    pub fn local_player_instantiated(player: Gd<Player>);

    #[func]
    fn on_welcome_message_received(&mut self, message: GString) {
        // Deserialize and add player to the game world
        let parsed_message = MessageType::from_json(message.to_string().as_str()).unwrap();
        if let MessageType::Welcome {
            player_id,
            player_data,
        } = parsed_message
        {
            self.local_player_id = Some(player_id.clone().to_godot());
            let mut player = self.instantiate_player_scene(&player_data);

            // let camera_node = self.base().get_node_as::<Camera2D>("../Camera2D");
            // self.base_mut().remove_child(&camera_node);
            // camera_node.get_parent().unwrap().remove_child(&camera_node);
            let camera_node = Camera2D::new_alloc();
            player.add_child(&camera_node);

            self.signals().local_player_instantiated().emit(player);
        }
    }

    #[func]
    fn on_player_joined_message_received(&mut self, message: GString) {
        // Deserialize and add player to the game world
        let parsed_message = MessageType::from_json(message.to_string().as_str()).unwrap();

        if let MessageType::PlayerJoined { player_data } = parsed_message {
            self.instantiate_player_scene(&player_data);
        }
    }

    #[func]
    fn on_player_left_message_received(&mut self, message: GString) {
        let parsed_message = MessageType::from_json(message.to_string().as_str()).unwrap();

        if let MessageType::PlayerLeft { player_data } = parsed_message {
            self.remove_player_from_scene(&player_data);
        }
    }

    #[func]
    fn on_game_state_sync_received(&mut self, message: GString) {
        // Deserialize and update players/game state
        let parsed_message = MessageType::from_json(message.to_string().as_str()).unwrap();

        if let MessageType::GameStateSync { players, bullets } = parsed_message {
            for (id, player_data) in players {
                if let Some(player) = self.players.get_mut(&id) {
                    // Update existing player
                    player.bind_mut().apply_network_state(&player_data);
                } else {
                    // Create new player
                    self.instantiate_player_scene(&player_data);
                }
            }

            for (id, bullet_data) in bullets {
                if let Some(bullet) = self.bullets.get_mut(&id) {
                    // Update existing bullet
                    bullet.bind_mut().apply_network_state(&bullet_data);
                } else {
                    // Create new bullet
                    self.instantiate_bullet_scene(&bullet_data);
                }
            }
        }
    }

    fn instantiate_player_scene(&mut self, player_data: &PlayerData) -> Gd<Player> {
        let mut new_player = self.player_scene.instantiate_as::<Player>();
        new_player.bind_mut().apply_network_state(&player_data);
        self.base_mut().add_child(&new_player);
        self.players
            .insert(player_data.player_id.clone(), new_player.clone());

        new_player
    }

    fn instantiate_bullet_scene(&mut self, bullet_data: &BulletData) -> Gd<Bullet> {
        let mut new_bullet = self.bullet_scene.instantiate_as::<Bullet>();
        new_bullet.bind_mut().apply_network_state(&bullet_data);
        self.base_mut().add_child(&new_bullet);
        self.bullets
            .insert(bullet_data.bullet_id.clone(), new_bullet.clone());

        new_bullet
    }

    fn remove_player_from_scene(&mut self, player_data: &PlayerData) {
        let player_removed = self.players.remove(&player_data.player_id);
        if let Some(mut player_removed) = player_removed {
            player_removed.queue_free();
        }
    }
}
