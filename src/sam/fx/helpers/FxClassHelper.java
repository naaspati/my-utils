package sam.fx.helpers;

import javafx.scene.Node;

public interface FxClassHelper {

	@SafeVarargs
	public static <E extends Node> E[] addClass(String clazz, E...nodes) {
		for (Node n : nodes) 
			n.getStyleClass().add(clazz);

		return nodes;

	}
	@SafeVarargs
	public static <E extends Node> E[] removeClass(String clazz, E...nodes) {
		for (Node n : nodes) 
			n.getStyleClass().remove(clazz);
		return nodes;
	}
	@SafeVarargs
	public static <E extends Node> E[] setClass(String clazz, E... nodes) {
		for (Node n : nodes) 
			n.getStyleClass().setAll(clazz);
		return nodes;
	}
	public static <E extends Node> E removeClass(E node, String...clazz) {
		node.getStyleClass().removeAll(clazz);
		return node;
	}
	public static <E extends Node> E addClass(E node, String...classes) {
		node.getStyleClass().addAll(classes);
		return node;
	}

	public static <E extends Node> E setClass(E n, String...className) {
		n.getStyleClass().setAll(className);
		return n;
	}
	public static <E extends Node> E clearClasses(E n) {
		n.getStyleClass().clear();
		return n;
	}
	public static <E extends Node> E toggleClass(E node, String clazz, boolean add) {
		if(add) {
			if(!hasClass(node, clazz))
				addClass(node, clazz);
		}
		else
			removeClass(node, clazz);

		return node;
	}
	public static boolean hasClass(Node node, String clazz) {
		return node.getStyleClass().contains(clazz);
	}
	/**
	 * if node contains the clazz, then remove else add
	 * @param node
	 * @param clazz
	 */
	public static Node toggleClass(Node node, String clazz) {
		if(hasClass(node,clazz))
			removeClass(node, clazz);
		else
			addClass(node,clazz);
		return node;
	}
	
	public static <E extends Node> E addClass(E node, String clazz) {
		node.getStyleClass().add(clazz);
		return node;
	}
	public static <E extends Node> E removeClass(E node, String clazz) {
		node.getStyleClass().removeAll(clazz);
		return node;
	}

	public static <E extends Node> E setClass(E n, String className) {
		n.getStyleClass().clear();
		n.getStyleClass().add(className);
		return n;
	}

	
}
