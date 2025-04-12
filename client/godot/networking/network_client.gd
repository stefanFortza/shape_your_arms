extends Node

signal on_message_received(String)

# The URL we will connect to.
@export var websocket_url = "ws://localhost:8887"


# Our WebSocketClient instance.
var socket = WebSocketPeer.new()
var player_id = -1

func _ready():
	# Initiate connection to the given URL.
	var err = socket.connect_to_url(websocket_url)
	if err != OK:
		print("Unable to connect")
		set_process(false)
	else:
		# Wait for the socket to connect.
		await get_tree().create_timer(2).timeout

func _process(_delta: float):
	# Poll the socket for incoming data.
	socket.poll()

	# get_ready_state() tells you what state the socket is in.
	var state = socket.get_ready_state()


	# WebSocketPeer.STATE_OPEN means the socket is connected and ready
	# to send and receive data.
	if state == WebSocketPeer.STATE_OPEN:
		while socket.get_available_packet_count():
			var string_message = socket.get_packet().get_string_from_utf8()
			# print("Got data from server: ", string_message)
			on_message_received.emit(string_message)
			# print("Got data from server: ", socket.get_packet().get_string_from_utf8())


	# WebSocketPeer.STATE_CLOSING means the socket is closing.
	# It is important to keep polling for a clean close.
	elif state == WebSocketPeer.STATE_CLOSING:
		pass

	# WebSocketPeer.STATE_CLOSED means the connection has fully closed.
	# It is now safe to stop polling.
	elif state == WebSocketPeer.STATE_CLOSED:
		# The code will be -1 if the disconnection was not properly notified by the remote peer.
		var code = socket.get_close_code()
		print("WebSocket closed with code: %d. Clean: %s" % [code, code != -1])
		set_process(false) # Stop processing.

func _on_player_moved(direction: Vector2):
	var message = MessageTypes.PlayerMoveMessage.new(direction)
	send_message(message)


# Send a strongly typed message
func send_message(message: MessageTypes.Message) -> void:
	if socket.get_ready_state() != WebSocketPeer.STATE_OPEN:
		print("Socket not connected, cannot send message")
		return
		
	var dict = message.to_dict()
	var json_string = JSON.stringify(dict)
	socket.send_text(json_string)

# # Process incoming message and emit appropriate signals
# func process_message(json_string: String) -> void:
# 	var json = JSON.new()
# 	var error = json.parse(json_string)

# 	if error != OK:
# 		print("JSON Parse Error: ", json.get_error_message())
# 		return
		
# 	var message = json.get_data()

# 	# Ensure the message has a type field
# 	if not message.has("type"):
# 		print("Received message without type field")
# 		return
		
# 	# Process message based on type
# 	match message["type"]:
# 		MessageTypes.MessageType.PLAYER_JOIN:
# 			if message.has("player_name") and message.has("player_id"):
# 				player_joined.emit(message["player_name"], message["player_id"])
				
# 		MessageTypes.MessageType.PLAYER_MOVE:
# 			if message.has("player_id") and message.has("position") and message.has("velocity"):
# 				var position = Vector2(message["position"]["x"], message["position"]["y"])
# 				var velocity = Vector2(message["velocity"]["x"], message["velocity"]["y"])
# 				player_moved.emit(message["player_id"], position, velocity)
				
# 		MessageTypes.MessageType.CHAT_MESSAGE:
# 			if message.has("player_id") and message.has("content"):
# 				chat_message_received.emit(message["player_id"], message["content"])
				
# 		MessageTypes.MessageType.GAME_STATE:
# 			if message.has("state"):
# 				game_state_updated.emit(message["state"])
				
# 		_:
# 			print("Unknown message type: ", message["type"])
