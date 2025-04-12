use godot::classes::{INode, Node};
use godot::prelude::*;

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
    fn on_message_received(&self, message: GString) {
        // Handle incoming messages
        godot_print!("Message received: {}", message);
    }
}
