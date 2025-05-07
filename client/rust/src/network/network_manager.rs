use std::str::FromStr;

use godot::classes::class_macros::registry::signal;
use godot::classes::class_macros::sys::known_virtual_hashes::VideoStreamPlayback::play;
use godot::classes::{INode, Node};
use godot::meta::GodotType;
use godot::prelude::*;

use crate::entities::player::Player;
use crate::utils::serializable_vector2::SerializableVector2;

use super::messages::message_type::MessageType;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct NetwornkManager {
    player_id: Option<GString>,

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for NetwornkManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            player_id: None,
            base,
        }
    }

    fn ready(&mut self) {
        godot_print!("NetworkManager is ready");

        let player = self.find_player_node();
        if let Some(mut player) = player {
            godot_print!("Player node found: {:?}", player);

            player
                .signals()
                .player_moved()
                .connect_obj(&self.to_gd(), Self::on_player_moved);
        } else {
            godot_print!("Player node not found");
        }
    }
}

#[godot_api]
pub impl NetwornkManager {
    #[signal]
    pub fn message_serialized(message: GString);

    #[signal]
    fn welcome_message_received(player_id: GString);

    #[func]
    fn on_message_received(&mut self, message: GString) {
        let message = MessageType::from_json(message.to_string().as_str());
        if let Ok(message) = message {
            godot_print!("Message received: {:?}", message);

            match message {
                MessageType::Welcome { player_id } => {
                    godot_print!("Welcome message received with player ID: {}", player_id);
                    let player_id = player_id.to_godot();
                    self.player_id = Some(player_id.clone());
                    self.signals().welcome_message_received().emit(player_id);
                }
                // Handle other message types here
                _ => {
                    godot_print!("Other message type received");
                }
            }
        } else {
            godot_error!("Failed to deserialize message");
        }
        // Handle incoming messages
        // godot_print!("Message received: {}", message);
    }

    // #[func]
    // fn send_message(&self, message: MessageType) {
    //     // Serialize the message to JSON
    //     let json_message = serde_json::to_string(&message).unwrap();
    //     godot_print!("Sending message: {}", json_message);

    //     // Send the message over the network
    //     // self.socket.send_text(&json_message);
    // }

    #[func]
    fn on_player_moved(&mut self, direction: Vector2) {
        // Handle player movement

        let message = MessageType::PlayerMoveFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
            direction: SerializableVector2::new_from_vector2(&direction),
            move_message_type: "movementStarted".to_string(),
        };

        if let Ok(json) = serde_json::to_string(&message) {
            godot_print!("Player moved: {}", json);

            // Emit the signal with the serialized message
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize player movement");
        }
        // let player_moved_message:MessageType = MessageType::PlayerMoveFromClient { player_id: (), direction: () }
    }

    #[func]
    fn find_player_node(&self) -> Option<Gd<Player>> {
        let tree = self.base().get_tree();
        if let Some(mut tree) = tree {
            let player = tree.get_first_node_in_group("player");

            if let Some(player) = player {
                let player = player.cast::<Player>();

                return Some(player);
            }
        }
        None
    }
}
