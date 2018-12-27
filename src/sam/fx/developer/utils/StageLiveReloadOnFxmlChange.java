package sam.fx.developer.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import javafx.application.Platform;
import javafx.stage.Stage;
import sam.fx.helpers.FxFxml;
import sam.io.fileutils.FileWatcher;

public class StageLiveReloadOnFxmlChange {
	private final FileWatcher fm;
	
	public StageLiveReloadOnFxmlChange(Path fxmlFile, Stage stage, Object controller) throws MalformedURLException {
		URL url = fxmlFile.toUri().toURL();
		this.fm = new FileWatcher(fxmlFile) {
			@Override
			protected void onModify() {
				Platform.runLater(() -> {
					stage.hide();
					try {
						FxFxml.load(url, stage, controller);
					} catch (IOException e) {
						e.printStackTrace();
					}
					stage.show();
				});
			}
		};
	}
	public void start() {
		Thread t = new Thread(fm);
		t.setDaemon(true);
		t.start();
	}
}
