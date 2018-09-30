package sam.fileutils;

import java.io.File;
import java.io.IOException;

public interface FileOpener {
	public static void openFile(File file) throws IOException{
		Runtime.getRuntime().exec("explorer \""+file.getName()+"\"", null, file.getParentFile());
	}
		public static void openFileLocationInExplorer(File file) throws IOException {
		if(file == null)
			throw new NullPointerException("path is null");

		Runtime.getRuntime().exec("explorer /select, \""+file.getName()+"\"", null, file.getParentFile());
	}

	
}
