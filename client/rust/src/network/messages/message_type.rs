use std::collections::HashMap;

use godot::prelude::GodotClass;
use serde::{Deserialize, Serialize};

use crate::utils::serializable_vector2::SerializableVector2;

use super::{bullet_data::BulletData, player_data::PlayerData};

#[derive(Deserialize, Serialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
#[serde(tag = "type")] // Use the "type" field in JSON to determine the variant.
pub enum MessageType {
    #[serde(rename_all = "camelCase")]
    GameStateSync {
        players: HashMap<String, PlayerData>,
        bullets: HashMap<String, BulletData>,
    },
    #[serde(rename_all = "camelCase")]
    Welcome {
        player_id: String,
        player_data: PlayerData,
    },

    #[serde(rename_all = "camelCase")]
    PlayerLeft { player_data: PlayerData },
    #[serde(rename_all = "camelCase")]
    PlayerJoined { player_data: PlayerData },
    #[serde(rename_all = "camelCase")]
    PlayerShootMessageFromClient { player_id: String },
    #[serde(rename_all = "camelCase")]
    PlayerMoveFromClient {
        player_id: String,
        direction: SerializableVector2,
        move_message_type: String,
    },
    #[serde(rename_all = "camelCase")]
    PlayerMouseDirectionFromClient {
        player_id: String,
        mouse_direction: SerializableVector2,
    },
    #[serde(rename_all = "camelCase")]
    SaveGameState,
    #[serde(rename_all = "camelCase")]
    LoadGameState,
}

impl MessageType {
    pub fn from_json(json: &str) -> Result<Self, serde_json::Error> {
        serde_json::from_str(json)
    }

    pub fn to_json(message: &MessageType) -> Result<String, serde_json::Error> {
        serde_json::to_string(message)
    }
}
