package sam.fx.helpers;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

public final class FxFxml {
	private static final Logger LOGGER = Logger.getLogger(FxFxml.class.getSimpleName());
	
	private static URL FXML_DIR;
	public static void setFxmlDir(URL fxml_dir) {
		FXML_DIR = fxml_dir;
	}

	public final FXMLLoader loader;

	public <E> E load() throws IOException {
		LOGGER.fine(() -> "Loading fxml: "+loader.getLocation());
		return loader.load();
	}
	public FxFxml() {
		loader = new FXMLLoader();
	}
	public FxFxml(URL url, Object root, Object controller) {
		loader = new FXMLLoader(url);
		loader.setController(controller);
		loader.setRoot(root);
	}
	public FxFxml(Object parentclass, Object root, Object controller) throws IOException {
		String name = parentclass.getClass().getSimpleName()+".fxml";
		URL url = FXML_DIR != null ? new URL(FXML_DIR, name) : ClassLoader.getSystemResource(name);
		loader = new FXMLLoader(url);
		loader.setController(controller);
		loader.setRoot(root);
	}
	public FxFxml(Object obj, boolean isDynamicRoot) throws IOException {
		this(obj, isDynamicRoot ? obj : null, obj);
	}
	public static <E> E fxml(URL url, Object root, Object controller) throws IOException {
		return new FxFxml(url, root, controller).load();
	}
	public static <E> E fxml(Object parentclass, Object root, Object controller) throws IOException {
		return new FxFxml(parentclass, root, controller).load();
	}
	public static <E> E fxml(Object obj, boolean isDynamicRoot) throws IOException {
		return new FxFxml(obj, isDynamicRoot ? obj : null, obj).load();
	}
	
	public FxFxml location(URL location) {
		loader.setLocation(location);
		return this;
	}
	public FxFxml resources(ResourceBundle resources) {
		loader.setResources(resources);
		return this;
	}
	public FxFxml root(Object root) {
		loader.setRoot(root);
		return this;
	}
	public FxFxml controller(Object controller) {
		loader.setController(controller);
		return this;
	}
	public FxFxml builderFactory(BuilderFactory builderFactory) {
		loader.setBuilderFactory(builderFactory);
		return this;
	}
	public FxFxml builderFactory(Map<Class<?>, ?> map) {
		loader.setBuilderFactory(cls -> () -> map.get(cls));
		return this;
	}
	public FxFxml controllerFactory(Callback<Class<?>, Object> controllerFactory) {
		loader.setControllerFactory(controllerFactory);
		return this;
	}
	public FxFxml charset(Charset charset) {
		loader.setCharset(charset);
		return this;
	}
	public FxFxml classLoader(ClassLoader classLoader) {
		loader.setClassLoader(classLoader);
		return this;
	}
}
