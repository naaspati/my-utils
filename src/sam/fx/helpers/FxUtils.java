package sam.fx.helpers;

import java.io.File;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sam.myutils.Checker;

public interface FxUtils {
	// VERSION = 1.02;
	public static void makeStageDraggable(Stage stage, Node node) {
		double[] start = {0,0};

		node.addEventFilter(MouseEvent.ANY, e -> {
			if(e.getEventType() == MouseEvent.MOUSE_PRESSED) {
				start[0] = e.getScreenX() - stage.getX();
				start[1] = e.getScreenY() - stage.getY();
			} else if(e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				stage.setX(e.getScreenX() - start[0]);
				stage.setY(e.getScreenY() - start[1]);
			}
		});
	}

	@SafeVarargs
	public static <N> void each(Consumer<N> consumer, N...ns) {
		for (N n : ns) consumer.accept(n);
	}

	/**
	 * find object of class E in Object(a Node) parent
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E find(Object node, Class<E> cls) {
		if(node == null || cls.isInstance(node))
			return (E) node;

		if(!(node instanceof Node))
			throw new IllegalArgumentException("node is not an Node instance. found class: "+node.getClass());

		Node n = ((Node)node).getParent();

		while(n != null && !cls.isInstance(n)) 
			n = n.getParent();	

		return (E) n;

	}
	public static InvalidationListener invalidationListener(InvalidationListener invalidationListener) {
		return invalidationListener;
	}
	public static <E> E edit(E e, Consumer<E> edit) {
		edit.accept(e);
		return e;
	}
	public static FileChooser fileChooser(File expectedDir, String expectedName, String title, Consumer<FileChooser> editor) {
		FileChooser fc = new FileChooser();

		if (Checker.exists(expectedDir))
			fc.setInitialDirectory(expectedDir);
		if(expectedName != null)
			fc.setInitialFileName(expectedName);
		
		fc.setTitle(title);
		if(editor != null)
			editor.accept(fc);
		return fc;
	}
}
