extends Button


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
	

func _on_button_down() -> void:
	var scene = load("res://networking/network_client.tscn")
	if scene:
		var instance = scene.instantiate()
		get_tree().root.add_child(instance)
