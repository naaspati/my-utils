package sam.fx.developer.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

public class FxDeveloperUtils {
	public static final double VERSION = 1.2;

	public static void setEscapeExit(Scene scene) {
		scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> System.exit(0));
	}

	public static void liveReloadCss(Path fileToWatch, Scene scene) throws IOException, URISyntaxException {
		liveReloadCss(fileToWatch, scene.getStylesheets());
	}
	public static void liveReloadCss(Path fileToWatch, List<String> stylesheets) throws IOException, URISyntaxException {
		Objects.requireNonNull(stylesheets, "scene cannot be null");
		
		if(!Files.isRegularFile(fileToWatch))
			throw new IOException(fileToWatch+" "+(Files.notExists(fileToWatch) ? "not found" : "not a file"));
		
		fileToWatch = fileToWatch.normalize().toAbsolutePath();
		newLoader(stylesheets, fileToWatch.getParent(), Collections.singleton(fileToWatch.getFileName()));
	}

	/**
	 * do not add stylesheets which are being watched
	 * 
	 * @param fileToWatch
	 * @param scene
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void liveReloadCss(Collection<Path> fileToWatch, Scene scene) throws IOException, URISyntaxException {
		Objects.requireNonNull(scene, "scene cannot be null");
		liveReloadCss(fileToWatch, scene.getStylesheets());
	}

	private static void liveReloadCss(Collection<Path> fileToWatch, ObservableList<String> stylesheets) throws IOException, URISyntaxException {
		Objects.requireNonNull(fileToWatch, "fileToWatch cannot be null");
		Objects.requireNonNull(stylesheets, "stylesheets cannot be null");

		if(fileToWatch.isEmpty()) 
			throw new IllegalArgumentException("no fileToWatch specified");

		for (Path p : fileToWatch) {
			if(!Files.isRegularFile(p))
				throw new IOException(p+" "+(Files.notExists(p) ? "not found" : "not a file"));	
		}

		if(fileToWatch.size() == 1){
			Path p = fileToWatch.iterator().next();
			liveReloadCss(p, stylesheets);
			return;
		}

		Set<Entry<Path, Set<Path>>> set = fileToWatch.stream()
		.map(p -> p.normalize().toAbsolutePath())
		.distinct()
		.collect(Collectors.groupingBy(Path::getParent, Collectors.mapping(Path::getFileName, Collectors.toSet())))
		.entrySet();
		
		for (Entry<Path, Set<Path>> e : set) 
			newLoader(stylesheets, e.getKey(), e.getValue());
		
	}

	private static void newLoader(List<String> stylesheets, Path parent, Collection<Path> children) throws MalformedURLException {
		CssLiveReload c = new CssLiveReload(stylesheets, parent, children);
		Thread t = new Thread(c);
		t.setDaemon(true);
		t.start();
	}
}
