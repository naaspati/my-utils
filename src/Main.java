import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import sam.collection.OneOrMany;
import sam.io.IOUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		OneOrMany<Integer> inte = new OneOrMany<>();
		System.out.println(inte+"  "+inte.size());
		
		int k = 0;
		inte.add(k++);
		System.out.println(inte+"  "+inte.size());

		inte.add(k++);
		System.out.println(inte+"  "+inte.size());

		inte.add(k++);
		System.out.println(inte+"  "+inte.size());

		inte.add(k++);
		System.out.println(inte+"  "+inte.size());

		inte.add(k++);
		System.out.println(inte+"  "+inte.size());

		inte.add(k++);
		System.out.println(inte+"  "+inte.size());
		
		
	}
}
