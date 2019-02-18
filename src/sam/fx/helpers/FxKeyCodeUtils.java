package sam.fx.helpers;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;

public interface FxKeyCodeUtils {
	public static KeyCodeCombination combination(KeyCode code, Modifier...modifiers) {
		return new KeyCodeCombination(code, modifiers);
	}
	public static void putAccelerator(Scene scene, KeyCode code, KeyCombination.Modifier modifier, Runnable action) {
		putAccelerator(scene, code, action, modifier);
	}
	public static void putAccelerator(Scene scene, KeyCode code, Runnable action, KeyCombination.Modifier...modifiers) {
		scene.getAccelerators().put(combination(code, modifiers), action);
	}
}
