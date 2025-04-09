package multiplayer.utils;

/**
 * Interface for classes that emit signals.
 * Implementing this interface helps standardize how signals are exposed.
 */
public interface SignalEmitter {
    /**
     * Get all available signals from this emitter
     * This method should be implemented to return all signals the class exposes
     */
    Signal<?>[] getSignals();
}
