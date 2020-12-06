package sam.functions;

@FunctionalInterface
public interface TriConsumerWithException<T, U, V, E extends Exception> {
    void accept(T t, U u, V v) throws E;
}
