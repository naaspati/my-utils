package sam.fx.developer.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javafx.application.Platform;
import sam.io.fileutils.DirWatcher;
import sam.myutils.Checker;
import sam.thread.MyUtilsThread;

public class CssLiveReload extends DirWatcher {
	public static void start(Collection<String> stylesheets, Path cssDir, String...cssFileNames) {
		if(Checker.isEmpty(cssFileNames))
			throw new RuntimeException("empty cssFileNames");
		
		MyUtilsThread.runOnDeamonThread(new CssLiveReload(stylesheets, cssDir, Arrays.stream(cssFileNames).map(Paths::get).collect(Collectors.toSet())));
	} 
	
	private final Collection<Path> filesToWatch;
	private Collection<String> styleSheets;
	private final String cssDir;

	public CssLiveReload(Collection<String> stylesheets, Path cssDir, Collection<Path> cssFilesFileNames) {
		super(cssDir, StandardWatchEventKinds.ENTRY_MODIFY);
		
		this.styleSheets = stylesheets;
		this.filesToWatch = cssFilesFileNames;
		String s;
		try {
			s = cssDir.toUri().toURL().toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		if(s.charAt(s.length() - 1) != '/')
			s = s.concat("/");
		
		this.cssDir = s;
	}

	@Override
	public void run() {
		Platform.runLater(() -> {
			filesToWatch.forEach(s -> {
				String url = cssDir+s;
				if(!styleSheets.contains(url))
					styleSheets.add(url);
			});
		});
		
		System.out.println("watching: \n   dir:"+cssDir);
		System.out.println("   files: "+filesToWatch.stream().map(Path::toString).collect(Collectors.joining(", ")));
		super.run();
	}

	@Override
	protected void failed(Exception e) {
		e.printStackTrace();
	}
	@Override
	protected boolean onErrorContinue(Exception e) {
		e.printStackTrace();
		return true;
	}
	@Override
	protected void onEvent(Path path, WatchEvent<?> we) {
		if(!filesToWatch.contains(path))
			return;
		String url = cssDir+path;
		
		Platform.runLater(() -> styleSheets.remove(url));
		Platform.runLater(() -> styleSheets.add(url));
		
		System.out.println("stylesheet modified: "+path.getFileName());
	}

}
