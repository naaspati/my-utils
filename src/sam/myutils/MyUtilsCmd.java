package sam.myutils;

import java.util.Arrays;

public interface MyUtilsCmd {
	public static void beep(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, '\007');
        System.out.println(new String(chars));
    }

}
