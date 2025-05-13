use godot::prelude::*;

use crate::network::messages::bullet_data::{self, BulletData};

#[derive(GodotClass)]
#[class(base=Node2D)]
pub struct Bullet {
    bullet_id: GString,
    owner_id: GString,
    #[base]
    base: Base<Node2D>,
}

#[godot_api]
pub impl Bullet {
    pub fn apply_network_state(&mut self, bullet_data: &BulletData) {
        // godot_print!("Applying network state: {:?}", player_data);

        self.owner_id = bullet_data.owner_id.to_godot();

        let transform = bullet_data.transform;
        self.base_mut()
            .set_position(Vector2::new(transform.x, transform.y));

        let rotation = transform.sint.atan2(transform.cost);

        self.base_mut().set_rotation(rotation);
    }

    #[func]
    pub fn applu_local_input(&mut self) {}
}

#[godot_api]
impl INode2D for Bullet {
    fn init(base: Base<Node2D>) -> Self {
        Self {
            owner_id: "".to_godot(),
            bullet_id: "".to_godot(),
            base,
        }
    }
}
