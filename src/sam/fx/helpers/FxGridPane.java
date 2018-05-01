package sam.fx.helpers;

import javafx.scene.layout.GridPane;

public interface FxGridPane {
    public static GridPane gridPane(int spacing) {
        return gridPane(spacing, spacing);
    }
    public static GridPane gridPane(int hgap, int vgap) {
        GridPane g = new GridPane();
        g.setHgap(hgap);
        g.setVgap(vgap);
        return g;
    }
    
}
