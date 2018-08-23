package sam.fx.helpers;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import sam.weak.LazyAndWeak;

public class FxFxml {
	private String fxmlDir;
	
	public FxFxml() {
		this.fxmlDir ="";
	}

	public FxFxml(String fxmlDir) {
		this.fxmlDir =fxmlDir;
	}
	public void setFxmlDir(String fxmlDir) {
		this.fxmlDir = fxmlDir;
	}

	private static final LazyAndWeak<FXMLLoader> fxkeep = new LazyAndWeak<>(FXMLLoader::new);
	
	public static void fxml(URL url, Object root, Object controller) throws IOException {
		FXMLLoader fx = fxkeep.get();
		fx.setLocation(url);
		fx.setController(controller);
		fx.setRoot(root);
		fx.load();
	}
	public void fxml(Object parentclass, Object root, Object controller) throws IOException {
		fxml(ClassLoader.getSystemResource(fxmlDir+parentclass.getClass().getSimpleName()+".fxml"), root, controller);
	}
	public void fxml(Object obj) throws IOException {
		fxml(obj, obj, obj);
	}

}
