use godot::classes::class_macros::registry::signal;
use godot::classes::{INode, Node};
use godot::meta::GodotType;
use godot::prelude::*;

use crate::entities::player::Player;

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

    #[func]
    fn on_message_received(&self, message: GString) {
        // Handle incoming messages
        godot_print!("Message received: {}", message);
    }

    // #[func]
    // fn send_message(&self, message: MessageType) {
    //     // Serialize the message to JSON
    //     let json_message = serde_json::to_string(&message).unwrap();
    //     godot_print!("Sending message: {}", json_message);

    //     // Send the message over the network
    //     // self.socket.send_text(&json_message);
    // }

    fn on_player_moved(&mut self, direction: Vector2) {
        // Handle player movement
        godot_print!("Player moved: {:?}", direction);
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
