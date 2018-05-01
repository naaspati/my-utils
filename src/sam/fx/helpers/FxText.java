package sam.fx.helpers;

import javafx.scene.text.Text;

public interface FxText {
    public static Text ofString(String string) {
        return new Text(string);
    }
    public static Text ofClass(String clss) {
        return FxClassHelper.addClass(new Text(), clss);
    }
    public static Text of(String string, String clss) {
        return FxClassHelper.addClass(new Text(string), clss);
    }
}
