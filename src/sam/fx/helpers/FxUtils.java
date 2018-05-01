package sam.fx.helpers;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public interface FxUtils {
	public static final double VERSION = 1.02;
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
	
}
