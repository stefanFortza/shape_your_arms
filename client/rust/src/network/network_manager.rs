use godot::classes::class_macros::sys::known_virtual_hashes::EditorResourceConversionPlugin::convert;
use godot::classes::{INode, Input, Node};
use godot::prelude::*;

use crate::entities::player::Player;
use crate::main_game::input_controller::{self, InputController};
use crate::main_game::main_game_ui::MainGameUI;
use crate::utils::serializable_vector2::SerializableVector2;

use super::messages::message_type::MessageType;
use super::meters_pixels_converter::MetersPixelsConverter;

#[derive(GodotClass)]
#[class(base=Node)]
pub struct NetwornkManager {
    player_id: Option<GString>,
    input_controller: OnReady<Gd<InputController>>,
    main_game_ui: OnReady<Gd<MainGameUI>>,

    #[base]
    base: Base<Node>,
}

#[godot_api]
impl INode for NetwornkManager {
    fn init(base: Base<Node>) -> Self {
        Self {
            input_controller: OnReady::from_node("../../InputController"),
            main_game_ui: OnReady::from_node("../../../UI/MainGameUI"),
            player_id: None,
            base,
        }
    }

    fn ready(&mut self) {
        godot_print!("NetworkManager is ready");

        let this = self.to_gd();

        self.input_controller
            .signals()
            .move_input_direction_changed()
            .connect_obj(&this, Self::on_move_input_direction_changed);

        self.input_controller
            .signals()
            .mouse_input_direction_changed()
            .connect_obj(&this, Self::on_mouse_input_direction_changed);
        self.input_controller
            .signals()
            .mouse_clicked()
            .connect_obj(&this, Self::on_mouse_clicked);

        self.main_game_ui
            .signals()
            .save_button_pressed()
            .connect_obj(&this, Self::_on_save_button_pressed);

        self.main_game_ui
            .signals()
            .load_button_pressed()
            .connect_obj(&this, Self::_on_load_button_pressed);
    }
}

#[godot_api]
pub impl NetwornkManager {
    #[signal]
    pub fn message_serialized(message: GString);

    #[signal]
    pub fn welcome_message_received(welcome_message: GString);

    #[signal]
    pub fn player_joined_message_received(player_joined_message: GString);

    #[signal]
    pub fn player_left_message_received(player_joined_message: GString);

    #[signal]
    pub fn game_state_sync_received(game_state_sync: GString);

    #[func]
    fn on_message_received(&mut self, message: GString) {
        // godot_print!("Message received: {:?}", message);
        let meters_pixels_converter = MetersPixelsConverter::default_converter();
        let parsed_message = MessageType::from_json(message.to_string().as_str());
        if let Ok(parsed_message) = parsed_message {
            match parsed_message {
                MessageType::Welcome {
                    ref player_id, // Changed: use `ref` to borrow player_id
                    player_data: _,
                } => {
                    godot_print!("Welcome message received with player ID: {:?}", player_id);

                    let converted_message = meters_pixels_converter
                        .convert_message_coordinates_to_pixels(&parsed_message); // Changed: pass reference to parsed_message

                    let player_id = player_id.to_godot();
                    self.player_id = Some(player_id.clone());
                    self.signals()
                        .welcome_message_received()
                        .emit(MessageType::to_json(&converted_message).unwrap().to_godot());
                }
                MessageType::GameStateSync {
                    players: _,
                    bullets: _,
                } => {
                    let converted_message = meters_pixels_converter
                        .convert_message_coordinates_to_pixels(&parsed_message);
                    let message = MessageType::to_json(&converted_message).unwrap().to_godot();
                    self.signals().game_state_sync_received().emit(message);
                }
                MessageType::PlayerJoined { player_data: _ } => {
                    self.signals()
                        .player_joined_message_received()
                        .emit(message);
                }
                MessageType::PlayerLeft { player_data: _ } => {
                    self.signals().player_left_message_received().emit(message);
                }

                // Handle other message types here
                _ => {
                    godot_print!("Other message type received : {:?}", parsed_message);
                }
            }
        } else {
            godot_error!("Failed to deserialize message : {:?}", message);
        }
        // Handle incoming messages
        // godot_print!("Message received: {}", message);
    }

    #[func]
    fn on_move_input_direction_changed(&mut self, direction: Vector2) {
        // Handle player movement

        let move_message_type = if direction != Vector2::ZERO {
            "movementStarted"
        } else {
            "movementStopped"
        };

        let message = MessageType::PlayerMoveFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
            direction: SerializableVector2::new_from_vector2(&direction),
            move_message_type: move_message_type.to_string(),
        };

        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize player movement");
        }
    }

    #[func]
    fn on_mouse_input_direction_changed(&mut self, direction: Vector2) {
        // Handle mouse input
        let message = MessageType::PlayerMouseDirectionFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
            mouse_direction: SerializableVector2::new_from_vector2(&direction),
        };

        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize mouse input");
        }
    }

    #[func]
    fn on_mouse_clicked(&mut self) {
        godot_print!("Mouse clicked");
        // Handle mouse click
        let message = MessageType::PlayerShootMessageFromClient {
            player_id: self.player_id.clone().unwrap_or_default().to_string(),
        };

        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize mouse input");
        }
    }

    #[func]
    fn _on_save_button_pressed(&mut self) {
        // Handle save button pressed
        godot_print!("Save button pressed");
        let message = MessageType::SaveGameState;
        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize save game state");
        }
        // Emit a signal or perform an action
    }

    #[func]
    fn _on_load_button_pressed(&mut self) {
        // Handle load button pressed
        godot_print!("Load button pressed");
        let message = MessageType::LoadGameState;
        if let Ok(json) = MessageType::to_json(&message) {
            self.signals().message_serialized().emit(json.to_godot());
        } else {
            godot_print!("Failed to serialize load game state");
        }
    }
}
