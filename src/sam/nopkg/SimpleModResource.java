package sam.nopkg;

import java.io.IOException;
import java.util.function.Supplier;

import sam.io.serilizers.IOExceptionConsumer;

public class SimpleModResource<E> extends ModResource<E> {
	private final Supplier<E> reader;
	private final IOExceptionConsumer<E> writer;

	public SimpleModResource(Supplier<E> reader, IOExceptionConsumer<E> writer) {
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	protected E read() {
		return reader.get();
	}

	@Override
	protected void write(E e) throws IOException {
		writer.accept(e);
	}

}
