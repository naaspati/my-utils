package sam.fx.helpers;

import javafx.scene.control.Label;

public interface FxLabel {
    public static Label label(String string) {
        return new Label(string);
    }
    public static Label label(String string, String clss) {
        return FxClassHelper.addClass(new Label(string), clss);
    }
}

