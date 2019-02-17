import java.io.IOException;
import java.nio.file.Paths;

import sam.nopkg.AutoCloseableWrapper;
import sam.nopkg.FileLinesSet;
import sam.reference.ReferencePool;
import sam.reference.WeakPool;

public class Main {

	public static void main(String[] args) throws Exception {
		FileLinesSet set = new FileLinesSet(Paths.get("temp"), false);
		set.add(String.valueOf(System.currentTimeMillis()));
		 set.remove("1550399529629");
		set.close();
	}

}
