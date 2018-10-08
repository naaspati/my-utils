package sam.fx.helpers;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import sam.reference.WeakAndLazy;

public final class FxFxml {
	private static final WeakAndLazy<FXMLLoader> fxkeep = new WeakAndLazy<>(FXMLLoader::new);
	
	private static URL FXML_DIR;
	
	public static void setFxmlDir(URL fxml_dir) {
		FXML_DIR = fxml_dir;
	}
	
	public static <E> E fxml(URL url, Object root, Object controller) throws IOException {
		FXMLLoader fx = fxkeep.get();
		fx.setLocation(url);
		fx.setController(controller);
		fx.setRoot(root);
		return fx.load();
	}
	public static <E> E fxml(Object parentclass, Object root, Object controller) throws IOException {
		String name = parentclass.getClass().getSimpleName()+".fxml";
		URL url = FXML_DIR != null ? new URL(FXML_DIR, name) : ClassLoader.getSystemResource(name); 
		return fxml(url, root, controller);
	}
	public static <E> E fxml(Object obj, boolean isDynamicRoot) throws IOException {
		return fxml(obj, isDynamicRoot ? obj : null, obj);
	}

}
