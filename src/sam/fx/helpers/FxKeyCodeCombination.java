package sam.fx.helpers;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;

public interface FxKeyCodeCombination {
    public static KeyCodeCombination combination(KeyCode code, Modifier...modifiers) {
        return new KeyCodeCombination(code, modifiers);
    }
    
}
