import java.util.Arrays;
import java.util.LinkedList;

public class Main {

	public static void main(String[] args) throws Exception {
		LinkedList<Integer> list = new LinkedList<>(Arrays.asList(0,1,2,3,4,5,6));
		System.out.println(list.listIterator(2).next());
	}

    
}
