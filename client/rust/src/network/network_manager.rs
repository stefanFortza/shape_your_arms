use godot::classes::{INode, Node};
use godot::prelude::*;

use crate::player::Player;

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

    fn ready(&mut self) {
        godot_print!("NetworkManager is ready");

        let player = self.find_player_node();
        if let Some(player) = player {
            godot_print!("Player node found: {:?}", player);
        } else {
            godot_print!("Player node not found");
        }
    }
}

#[godot_api]
impl NetwornkManager {
    #[func]
    fn on_message_received(&self, message: GString) {
        // Handle incoming messages
        godot_print!("Message received: {}", message);
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
