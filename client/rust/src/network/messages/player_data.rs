use serde::{Deserialize, Serialize};

use crate::utils::transform::Transform;

// Message received: "{"players":{"0a09c426-44e0-45bb-a316-f37deafd6d68":{"playerId":"0a09c426-44e0-45bb-a316-f37deafd6d68",
// "transform":{"cost":1.0,"sint":0.0,"x":0.0,"y":0.0},"health":100,"score":0}}
// ,"bullets":[],"type":"gameStateSync"}"

#[derive(Deserialize, Serialize, Debug, Clone)]
#[serde(rename_all = "camelCase")]
pub struct PlayerData {
    pub player_id: String,
    pub transform: Transform,
}
