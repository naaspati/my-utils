package sam.fx.developer.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import sam.fileutils.FilesUtils;
import sam.myutils.MyUtils;

public interface FxDeveloperUtils {
	public static final double VERSION = 1.2;

	public static void setEscapeExit(Scene scene) {
		scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> System.exit(0));
	}

	public static void liveReloadCss(HostServices hs, String fileToWatch, Scene scene) throws IOException, URISyntaxException {
		Objects.requireNonNull(hs, "HostServices cannot be null");
		Objects.requireNonNull(fileToWatch, "fileToWatch cannot be null");
		Objects.requireNonNull(scene, "stylesheets cannot be null");

		Path cssPath = Paths.get(new URI(hs.getDocumentBase())).resolve(fileToWatch);

		if(!Files.isRegularFile(cssPath))
			throw new IOException(cssPath+" "+(Files.notExists(cssPath) ? "not found" : "not a file"));

		String cssFileString = hs.resolveURI(hs.getDocumentBase(), fileToWatch);

		Consumer<Path> setCss = p -> {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(cssFileString);
			System.out.println("css set: "+fileToWatch);
		};

		setCss.accept(null);
		
		MyUtils.runOnDeamonThread(() -> FilesUtils.watchDir(cssPath.getParent(), setCss, e -> {e.printStackTrace(); return false;}, StandardWatchEventKinds.ENTRY_MODIFY));
	}
}
