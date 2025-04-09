use godot::classes::{INode, Node};
use godot::prelude::*;

use crate::network::server_data_parse::server_data_enum::{GameStateData, ServerDataEnum};

#[derive(GodotClass)]
#[class(base=Node)]
struct NetwornkManager {
    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for NetwornkManager {
    fn init(base: Base<Node>) -> Self {
        Self { base }
    }
}

#[godot_api]
impl NetwornkManager {
    // #[signal]
    // fn on_game_state_received(data: String);
    // #[signal]
    // fn on_hit_received(damage: u32);
    // #[signal]
    // fn on_player_death_received(player_id: u32, reason: String);
    // #[signal]
    // fn on_welcome_received(id: String, x: f32, y: f32);
    // #[signal]
    // fn on_player_left_received(player_id: u32);

    // #[func]
    // fn _on_network_client_on_message_received(&mut self, server_data: String) {
    //     // godot_print!("Connected to server: {}", server_data);
    //     let data = ServerDataEnum::from_json(&server_data);
    //     match data {
    //         Ok(data) => match data {
    //             ServerDataEnum::GameState { data } => {
    //                 godot_print!("Game state: {:?}", data);
    //                 // Serialize GameStateData to a JSON string before emitting the signal
    //             }
    //             ServerDataEnum::Hit { damage } => {
    //                 godot_print!("Hit with damage: {}", damage);
    //             }
    //             ServerDataEnum::PlayerDeath { player_id, reason } => {
    //                 godot_print!("Player {} died due to {}", player_id, reason);
    //             }
    //             ServerDataEnum::Welcome { id, x, y } => {
    //                 godot_print!("Welcome! ID: {}, Position: ({}, {})", id, x, y);
    //             }
    //             ServerDataEnum::PlayerLeft { player_id } => {
    //                 godot_print!("Player {} left the game", player_id);
    //             }
    //         },
    //         Err(e) => godot_print!("Failed to parse server data: {}", e),
    //     }
    // }
}
