use std::collections::HashMap;

use godot::prelude::GodotConvert;
use serde::{Deserialize, Serialize};

use crate::utils::serializable_vector2::SerializableVector2;

use super::{bullet_parser::Bullet, player_parser::Player};

// #[derive(Deserialize, Debug)]
// pub struct GameStateData {
//     pub players: HashMap<String, Player>,
//     pub bullets: Vec<Bullet>,
// }

#[derive(Deserialize, Serialize, Debug)]
#[serde(rename_all = "camelCase")]
#[serde(tag = "type")] // Use the "type" field in JSON to determine the variant.
pub enum MessageType {
    // GameState {
    //     #[serde(flatten)]
    //     data: GameStateData,
    // },
    // Hit {
    //     damage: u32,
    // },
    // PlayerDeath {
    //     player_id: u32,
    //     reason: String,
    // },
    #[serde(rename_all = "camelCase")]
    Welcome { player_id: String },
    // PlayerLeft {
    //     player_id: u32,
    // },
    // PlayerJoined {
    //     player_id: u32,
    //     name: String,
    // },
    // InitialGameState {
    //     #[serde(flatten)]
    //     data: GameStateData,
    // },
    // PlayerMoveFromServer {
    //     player_id: u32,
    //     x: f32,
    //     y: f32,
    // },
    // PlayerShoot {
    //     player_id: u32,
    //     direction: (f32, f32),
    // },
    PlayerMoveFromClient {
        player_id: String,
        direction: SerializableVector2,
    },
}

impl MessageType {
    pub fn from_json(json: &str) -> Result<Self, serde_json::Error> {
        serde_json::from_str(json)
    }

    pub fn to_json(message: &MessageType) -> Result<String, serde_json::Error> {
        serde_json::to_string(message)
    }
}
