package sam.fx.dialog;

import javafx.scene.Node;

public interface DialogViewer {
	/**
	 * dialog will be closeable
	 * @param title
	 * @param node
	 * @return a runnable which when called, closes the dialog
	 */
	Runnable viewDialog(String title, Node node, Runnable onClose);
	Runnable viewDialog(Node node);
}
