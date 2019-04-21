package sam.fx.dialog;

import javafx.scene.Node;

public interface DialogViewer {
	/**
	 * @param title
	 * @param node
	 * @return a runnable which when called, closes the dialog
	 */
	public Runnable viewDialog(String title, Node node, Runnable onClose);

}
