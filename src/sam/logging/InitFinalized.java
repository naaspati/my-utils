package sam.logging;

public interface InitFinalized {

	default void init() {
		MyLoggerFactory.logger(getClass().getSimpleName()).fine(() -> "INIT "+getClass());
	}
	default void finalized() {
		MyLoggerFactory.logger(getClass().getSimpleName()).fine(() -> "FINALIZED "+getClass());
	}

}
