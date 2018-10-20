package sam.myutils;

public interface MyUtilsArgs {
	public static boolean isHelp(String arg) {
		return arg != null && (arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help") || arg.equals("\\?")); 
	}
	public static boolean isVersion(String arg) {
		return arg != null && (arg.equalsIgnoreCase("-v") || arg.equalsIgnoreCase("--version")); 
	}

}
