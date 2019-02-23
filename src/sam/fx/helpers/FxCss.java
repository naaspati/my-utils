package sam.fx.helpers;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

public interface FxCss {
	
	public static Border border(Paint stroke) {
		return new Border(borderStroke(stroke));
	}
	public static BorderStroke borderStroke(Paint stroke) {
		return new BorderStroke(stroke, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT, Insets.EMPTY);
	}
	public static Border border(Paint stroke, BorderStrokeStyle style, BorderWidths widths) {
		return new Border(borderStroke(stroke, style, widths));
	}
	public static BorderStroke borderStroke(Paint stroke, BorderStrokeStyle style, BorderWidths widths) {
		return new BorderStroke(stroke, style, CornerRadii.EMPTY, widths, Insets.EMPTY);
	}
	public static Border border(Paint stroke, BorderStrokeStyle style, CornerRadii radii, BorderWidths widths, Insets insets) {
		return new Border(new BorderStroke(stroke, style, radii, widths, insets));
	}
	
	public static Background background(Paint fill) {
		return background(fill, CornerRadii.EMPTY, Insets.EMPTY);
	}
	public static Background background(Paint fill, CornerRadii radii, Insets insets) {
		return new Background(new BackgroundFill(fill, radii, insets));
	}
	
	
}
