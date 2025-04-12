use godot::classes::{Control, IControl};
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Control)]
struct MainGameUI {
    #[base]
    base: Base<Control>,
}

#[godot_api]
impl IControl for MainGameUI {
    fn init(base: Base<Control>) -> Self {
        Self { base }
    }

    fn physics_process(&mut self, delta: f64) {}
}

#[godot_api]
impl MainGameUI {
    #[func]
    fn _on_back_button_pressed(&self) {
        godot_print!("Start button pressed");
        match self.base().get_tree().as_mut() {
            Some(tree) => {
                // Here you would typically change the scene to the game scene
                if let Ok(scene) = try_load::<PackedScene>("res://scenes/main_menu/main_menu.tscn")
                {
                    tree.change_scene_to_packed(&scene);
                } else {
                    godot_print!("Failed to load game scene");
                }
            }
            None => godot_print!("No tree found"),
        }
    }
}
