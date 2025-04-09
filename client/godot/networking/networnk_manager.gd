extends Node


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_network_client_on_message_received(String: Variant) -> void:
	print("Message received: ", String)
	pass # Replace with function body.
