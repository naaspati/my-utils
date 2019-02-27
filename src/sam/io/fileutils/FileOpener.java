package sam.io.fileutils;

import java.io.File;
import java.io.IOException;
import sam.logging.Logger;

public final class FileOpener {
	private static final Logger LOGGER = Logger.getLogger(FileOpener.class);
	
	public static void openFile(File file) throws IOException{
		LOGGER.debug("Open File: {}",file);
		Runtime.getRuntime().exec("explorer \""+file.getName()+"\"", null, file.getParentFile());
	}
	public static void openFileLocationInExplorer(File file) throws IOException {
		if(file == null)
			throw new NullPointerException("path is null");

		LOGGER.debug("Open File Location: {}",file);
		Runtime.getRuntime().exec("explorer /select, \""+file.getName()+"\"", null, file.getParentFile());
	}
}
