package sam.fx.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.IdentityHashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import sam.logging.Logger;

public final class FxFxml implements BuilderFactory  {
	private static final Logger LOGGER = Logger.getLogger(FxFxml.class);

	private static String FXML_DIR;
	public static void setFxmlDir(URL fxml_dir) {
		FXML_DIR = fxml_dir.toString();
		LOGGER.debug("FXML_DIR: {}", FXML_DIR);
	}

	public final FXMLLoader loader;
	private IdentityHashMap<Class<?>, Object> builds;

	private BuilderFactory builderFactory;

	@SuppressWarnings("rawtypes")
	@Override
	public Builder<?> getBuilder(Class<?> type) {
		Object o = builds.get(type);
		if(o == null)
			return null;
		if(o instanceof Builder)
			return (Builder)o;
			 
		return () -> o;
	}  

	public <E> E load() throws IOException {
		LOGGER.debug("Loading fxml: {}", loader.getLocation());
		
		if(builderFactory != null)
			loader.setBuilderFactory(builderFactory);
		if(builds != null)
			loader.setBuilderFactory(this);	
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
		loader = new FXMLLoader(url(parentclass, ".fxml"));
		loader.setController(controller);
		loader.setRoot(root);
	}
	public static URL url(Object parentclass, String ext) throws MalformedURLException {
		String name = parentclass.getClass().getSimpleName()+ext;
		return FXML_DIR != null ? new URL(FXML_DIR+"/"+name) : ClassLoader.getSystemResource(name);
	}

	public URL location() {
		return loader.getLocation();
	}
	public FxFxml(Object obj, boolean isDynamicRoot) throws IOException {
		this(obj, isDynamicRoot ? obj : null, obj);
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
		this.builderFactory = builderFactory;
		return this;
	}
	public <E> FxFxml putBuilder(Class<E> cls, E value) {
		if(builds == null)
			builds = new IdentityHashMap<>();
		builds.put(cls, value);
		return this;
	}
	public <E> FxFxml putBuilder(Class<E> cls, Builder<E> builder) {
		if(builds == null)
			builds = new IdentityHashMap<>();
		builds.put(cls, builder);
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
	public static void setCss(Parent parent) throws MalformedURLException {
		parent.getStylesheets().add(url(parent, ".css").toString());
	}
	public static <E> E load(URL url, Object root, Object controller) throws IOException {
		return new FxFxml(url, root, controller).load();
	}
	public static <E> E load(Object parentclass, Object root, Object controller) throws IOException {
		return new FxFxml(parentclass, root, controller).load();
	}
	public static <E> E load(Object obj, boolean isDynamicRoot) throws IOException {
		return new FxFxml(obj, isDynamicRoot ? obj : null, obj).load();
	}
}
