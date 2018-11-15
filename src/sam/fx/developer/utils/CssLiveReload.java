package sam.fx.developer.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import sam.io.fileutils.DirWatcher;

public class CssLiveReload extends DirWatcher {
	private final Collection<Path> filesToWatch;
	private List<String> styleSheets;
	private final String cssDir;

	public CssLiveReload(List<String> stylesheets, Path cssDir, Collection<Path> cssFilesFileNames) throws MalformedURLException {
		super(cssDir, StandardWatchEventKinds.ENTRY_MODIFY);
		
		this.styleSheets = stylesheets;
		this.filesToWatch = cssFilesFileNames;
		String s = cssDir.toUri().toURL().toString();
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
