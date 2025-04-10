use godot::classes::{Button, Control, IControl};
use godot::prelude::*;

#[derive(GodotClass)]
#[class(base=Control)]
struct MainMenu {
    #[base]
    base: Base<Control>,
}

#[godot_api]
impl IControl for MainMenu {
    fn init(base: Base<Control>) -> Self {
        Self { base }
    }

    fn physics_process(&mut self, delta: f64) {}
}

#[godot_api]
impl MainMenu {
    #[func]
    fn _on_start_button_pressed(&self) {
        godot_print!("Start button pressed");
        match self.base().get_tree().as_mut() {
            Some(tree) => {
                // Here you would typically change the scene to the game scene
                if let Ok(scene) = try_load::<PackedScene>("res://main_scene.tscn") {
                    tree.change_scene_to_packed(&scene);
                } else {
                    godot_print!("Failed to load game scene");
                }
            }
            None => godot_print!("No tree found"),
        }
    }

    #[func]
    fn _on_options_button_pressed(&self) {
        godot_print!("Options button pressed");
        // Here you would typically open the options menu
    }

    #[func]
    fn _on_quit_button_pressed(&self) {
        godot_print!("Exit button pressed");
        // Here you would typically exit the game
        self.base().get_tree().unwrap().quit();
    }
}
