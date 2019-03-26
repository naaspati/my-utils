import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.function.IntSupplier;

import javax.swing.ImageIcon;

import sam.collection.OneOrMany;
import sam.io.IOUtils;
import sam.nopkg.EnsureSingleton;

public class Main {
	private static final EnsureSingleton singleton = new EnsureSingleton();
	{
		singleton.init();
	}

	public static void main(String[] args) throws Exception {
		new Main();
		new Main();
		new Main();
	}

	static int n = 0;
	int a = (new IntSupplier() {
		@Override
		public int getAsInt() {
			System.out.println(n);
			return n++;
		}
	}).getAsInt();
	
	
}
