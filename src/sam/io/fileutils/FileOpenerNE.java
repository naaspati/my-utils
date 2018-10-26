package sam.io.fileutils;

import java.io.File;
import java.util.function.BiConsumer;

/**
 * FileOpener with self error handler
 * @author Sameer
 *
 */
public final class FileOpenerNE {
	private static BiConsumer<File, Exception> errorHandler = (file, error) -> {};

	public static void setErrorHandler(BiConsumer<File, Exception> errorHandler) {
		FileOpenerNE.errorHandler = errorHandler;
	}

	public static void openFile(File file){
		try {
			FileOpener.openFile(file);
		} catch (Exception e) {
			errorHandler.accept(file, e);
		}
	}
	public static void openFileLocationInExplorer(File file) {
		try {
			FileOpener.openFileLocationInExplorer(file);
		} catch (Exception e) {
			errorHandler.accept(file, e);
		}
	}



}
