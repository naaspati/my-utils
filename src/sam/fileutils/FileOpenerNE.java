package sam.fileutils;

import java.io.File;
import java.util.function.Consumer;

/**
 * FileOpener with self error handler
 * @author Sameer
 *
 */
public final class FileOpenerNE {
	private static Consumer<Exception> errorHandler = e -> {};

	public static void setErrorHandler(Consumer<Exception> errorHandler) {
		FileOpenerNE.errorHandler = errorHandler;
	}

	public static void openFile(File file){
		try {
			FileOpener.openFile(file);
		} catch (Exception e) {
			errorHandler.accept(e);
		}
	}
	public static void openFileLocationInExplorer(File file) {
		try {
			FileOpener.openFileLocationInExplorer(file);
		} catch (Exception e) {
			errorHandler.accept(e);
		}
	}



}
