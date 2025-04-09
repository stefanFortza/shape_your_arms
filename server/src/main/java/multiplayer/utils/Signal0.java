package multiplayer.utils;

/**
 * A specialized Signal class that doesn't pass any data when emitted.
 * Useful for simple notifications where only the event occurrence matters.
 * Extends Signal<Void> for code reusability.
 */
public class Signal0 extends Signal<Void> {

    /**
     * Creates a new signal with the given name
     * 
     * @param name A descriptive name for the signal
     */
    public Signal0(String name) {
        super(name);
    }

    /**
     * Connect a callback to this signal
     * 
     * @param callback The callback to be executed when the signal is emitted
     * @return true if the connection was successful
     */
    public boolean connect(Runnable callback) {
        return super.connect(data -> callback.run());
    }

    /**
     * Emit the signal with no data
     */
    public void emit() {
        super.emit(null);
    }

    // All other methods like disconnect, getConnectionCount, etc. are inherited
    // from Signal<Void>
}
