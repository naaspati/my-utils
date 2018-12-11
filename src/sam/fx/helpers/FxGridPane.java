package sam.fx.helpers;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

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
    public static void setColumnConstraint(GridPane gridPane, int index, ColumnConstraints c) {
    	for (int i = gridPane.getColumnConstraints().size(); i < index; i++) 
    		gridPane.getColumnConstraints().add(new ColumnConstraints());
    	gridPane.getColumnConstraints().add(c);
    }
    public static void setRowConstraint(GridPane gridPane, int index, RowConstraints c) {
    	for (int i = gridPane.getRowConstraints().size(); i < index; i++) 
    		gridPane.getRowConstraints().add(new RowConstraints());
    	gridPane.getRowConstraints().add(c);
    }
    
}
