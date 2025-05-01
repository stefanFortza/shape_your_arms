use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct BaseSignalMessage {
    #[base]
    base: Base<Node>,
}

#[godot_api]
pub impl INode for BaseSignalMessage {
    fn init(base: Base<Node>) -> Self {
        Self { base }
    }
}

#[godot_api]
pub impl BaseSignalMessage {}
