import java.io.IOException;

import sam.reference.ReferencePool;
import sam.reference.WeakPool;

public class Main {

	public static void main(String[] args) throws IOException {
		System.setProperty("DUMP_POOL_GENERATED_COUNT", "true");
		ReferencePool<String> s = new WeakPool<>(() -> String.valueOf(System.currentTimeMillis()));
		s.poll();
		s.add(s.poll());
		s.poll();
	}

}
