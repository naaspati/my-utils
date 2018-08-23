package sam.fx.helpers;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.Hyperlink;
import sam.fileutils.FileOpener;
import sam.fx.popup.FxPopupShop;

public interface FxHyperlink {

	public static Hyperlink of(Path path) {
		Hyperlink link = new Hyperlink(path == null ? "--" : path.toString());
		return configure(link, path);
	}

	public static Hyperlink configure(Hyperlink link, Path path) {
		if(path == null) {
			link.setDisable(true);
			return link;
		}

		link.setOnAction(e -> {
			if(Files.notExists(path))
				FxPopupShop.showHidePopup("file not found: \n"+path, 2000);
			else if(Files.isDirectory(path))
				FileOpener.getInstance().openFileNoError(path.toFile());
			else
				FileOpener.getInstance().openFileLocationInExplorerNoError(path.toFile());
		});
		return link;
	}


}
