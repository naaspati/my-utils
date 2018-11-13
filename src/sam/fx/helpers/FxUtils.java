package sam.fx.helpers;

import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

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

	/**
	 * 
	 * <pre>
	 *   Pane p = new Pane();
	 *   p.setMaxWidth(Double.MAX_VALUE);
	 *   HBox.setHgrow(p, Priority.ALWAYS);
	 *   return p;
	 * </pre>
	 * 
	 * @return
	 */
	public static Pane longPaneHbox() {
		Pane p = new Pane();
		p.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(p, Priority.ALWAYS);
		return p;	
	}


}
