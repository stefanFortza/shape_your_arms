use godot::prelude::*;

mod main_game;
mod main_menu;
mod network;
mod player;

struct MyExtension;

#[gdextension]
unsafe impl ExtensionLibrary for MyExtension {}
