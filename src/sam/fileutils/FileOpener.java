package sam.fileutils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class FileOpener {
	private static final Logger LOGGER = Logger.getLogger(FileOpener.class.getSimpleName());
	
	public static void openFile(File file) throws IOException{
		LOGGER.fine(() -> "Open File: "+file);
		Runtime.getRuntime().exec("explorer \""+file.getName()+"\"", null, file.getParentFile());
	}
	public static void openFileLocationInExplorer(File file) throws IOException {
		if(file == null)
			throw new NullPointerException("path is null");

		LOGGER.fine(() -> "Open File Location: "+file);
		Runtime.getRuntime().exec("explorer /select, \""+file.getName()+"\"", null, file.getParentFile());
	}


}
