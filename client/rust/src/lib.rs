use godot::prelude::*;

mod main_game;
mod main_menu;
mod network;
mod player;
mod utils;

struct MyExtension;

#[gdextension]
unsafe impl ExtensionLibrary for MyExtension {}
