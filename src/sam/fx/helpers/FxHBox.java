package sam.fx.helpers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public interface FxHBox {
	public static HBox buttonBox(Node...nodes){
		HBox box = new HBox(10, nodes);
		box.setPadding(new Insets(5, 10, 5, 10));
		box.setAlignment(Pos.CENTER_RIGHT);
		return box;
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
	public static Pane maxPane() {
		Pane p = new Pane();
		p.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(p, Priority.ALWAYS);
		return p;	
	}

}
