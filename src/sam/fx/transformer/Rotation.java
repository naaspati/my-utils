package sam.fx.transformer;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;

/**
 * 
 * FIXME
 * 
 * needs more work
 * 
 * 1. removing effect of methods and applying effect of transform when switching from method to transform and Vice versa
 *    
 */
class Rotation extends VBox implements AbstractTransform {
	private final RotateUsingMethod rotationUsingMethod = new RotateUsingMethod();
	private RotateUsingTransform rotationWithTransform;
	private AbstractTransform current = rotationUsingMethod;

	public Rotation() {
		super(5);

		RadioButton rb = new RadioButton("use new Rotate(...) transform");
		getChildren().add(rb);
		getChildren().add((Node)current);

		rb.setOnAction(e -> {
			if(rotationWithTransform == null)
				rotationWithTransform  = new RotateUsingTransform();
			current = rb.isSelected() ? rotationWithTransform : rotationUsingMethod;
			getChildren().set(1, (Node)current);
		});
	}

	@Override
	public String title() {
		return current.title();
	}

	@Override
	public String toString() {
		return current.toString();
	}

	@Override
	public void change(Node old, Node _new) {
		current.change(old, _new);
	}
}
