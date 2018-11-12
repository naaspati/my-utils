package sam.fx.developer.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import sam.io.fileutils.DirWatcher;

public class CssLiveReload implements Runnable {
	private final Collection<Path> filesToWatch;
	private List<String> styleSheets;
	private final DirWatcher watcher;
	private final String cssDir;

	public CssLiveReload(List<String> stylesheets, Path cssDir, Collection<Path> cssFilesFileNames) throws MalformedURLException {
		this.styleSheets = stylesheets;
		this.filesToWatch = cssFilesFileNames;
		String s = cssDir.toUri().toURL().toString();
		if(s.charAt(s.length() - 1) != '/')
			s = s.concat("/");
		
		this.cssDir = s;
		
		watcher = new DirWatcher(cssDir, this::onupdate, this::onError, StandardWatchEventKinds.ENTRY_MODIFY);
	}
	
	private boolean onError(Throwable t) {
		t.printStackTrace();
		return false;
	}
	private void onupdate(Path path){
		if(!filesToWatch.contains(path))
			return;
		String url = cssDir+path;
		
		Platform.runLater(() -> styleSheets.remove(url));
		Platform.runLater(() -> styleSheets.add(url));
		
		System.out.println("stylesheet modified: "+path.getFileName());
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
		watcher.run();
	}

}
