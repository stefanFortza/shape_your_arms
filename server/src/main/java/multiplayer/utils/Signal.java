package multiplayer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A Signal class inspired by Godot's signal system.
 * Allows for event-driven programming with a familiar syntax.
 *
 * @param <T> The type of data that will be passed when the signal is emitted
 */
public class Signal<T> {
    private final List<Consumer<T>> connections = new ArrayList<>();
    private final String name;

    /**
     * Creates a new signal with the given name
     * 
     * @param name A descriptive name for the signal
     */
    public Signal(String name) {
        this.name = name;
    }

    /**
     * Creates a new signal with a default name
     */
    public Signal() {
        this("UnnamedSignal");
    }

    /**
     * Connect a callback to this signal
     * 
     * @param callback The callback to be executed when the signal is emitted
     * @return true if the connection was successful
     */
    public boolean connect(Consumer<T> callback) {
        if (!connections.contains(callback)) {
            connections.add(callback);
            return true;
        }
        return false;
    }

    /**
     * Disconnect a callback from this signal
     * 
     * @param callback The callback to disconnect
     * @return true if the callback was found and disconnected
     */
    public boolean disconnect(Consumer<T> callback) {
        return connections.remove(callback);
    }

    /**
     * Emit the signal with the given data
     * 
     * @param data The data to pass to all connected callbacks
     */
    public void emit(T data) {
        // Create a copy to avoid concurrent modification issues if callbacks modify
        // connections
        List<Consumer<T>> currentConnections = new ArrayList<>(connections);

        for (Consumer<T> callback : currentConnections) {
            try {
                callback.accept(data);
            } catch (Exception e) {
                System.err.println("Error in signal '" + name + "' callback: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * @return The number of connections to this signal
     */
    public int getConnectionCount() {
        return connections.size();
    }

    /**
     * Removes all connections from this signal
     */
    public void disconnectAll() {
        connections.clear();
    }

    /**
     * @return The name of this signal
     */
    public String getName() {
        return name;
    }
}
