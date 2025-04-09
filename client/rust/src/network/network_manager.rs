use godot::classes::{INode, Node};
use godot::prelude::*;

use crate::network::server_data_parse::server_data_enum::ServerDataEnum;

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
    #[func]
    fn _on_network_client_on_message_received(server_data: String) {
        // godot_print!("Connected to server: {}", server_data);
        let data = ServerDataEnum::from_json(&server_data);
        match data {
            Ok(data) => match data {
                ServerDataEnum::GameState { data } => {
                    godot_print!("Game state: {:?}", data);
                }
                ServerDataEnum::Hit { damage } => {
                    godot_print!("Hit with damage: {}", damage);
                }
                ServerDataEnum::PlayerDeath { player_id, reason } => {
                    godot_print!("Player {} died due to {}", player_id, reason);
                }
                ServerDataEnum::Welcome { id, x, y } => {
                    godot_print!("Welcome! ID: {}, Position: ({}, {})", id, x, y);
                }
                ServerDataEnum::PlayerLeft { player_id } => {
                    godot_print!("Player {} left the game", player_id);
                }
            },
            Err(e) => godot_print!("Failed to parse server data: {}", e),
        }

        // match data {
        //     ServerDataEnum::GameState { state } => godot_print!("Game state: {}", state),
        //     ServerDataEnum::Hit { damage } => todo!(),
        //     ServerDataEnum::PlayerDeath { player_id, reason } => todo!(),
        //     ServerDataEnum::Welcome { message } => todo!(),
        //     ServerDataEnum::PlayerLeft { player_id } => todo!(),
        // }
    }
}
