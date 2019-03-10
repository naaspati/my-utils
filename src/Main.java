import java.nio.file.Paths;

import sam.io.serilizers.StringReader2;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println(StringReader2.getText0(Paths.get("D:\\Downloads\\10.txt")));
	}
}
