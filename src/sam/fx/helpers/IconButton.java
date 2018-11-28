package sam.fx.helpers;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
	public void setIcon(String url) {
		setGraphic(new ImageView(url));
		getStyleClass().setAll("icon-button");
		icon().getStyleClass().add("icon");
	}
	public String getIcon() {
		return null;
	}
	public final Image getImage() {
		return icon().getImage();
	}
	public final void setFitWidth(double value) {
		icon().setFitWidth(value);
	}
	public final double getFitWidth() {
		return icon().getFitWidth();
	}
	public final DoubleProperty fitWidthProperty() {
		return icon().fitWidthProperty();
	}
	public final void setFitHeight(double value) {
		icon().setFitHeight(value);
	}
	public final double getFitHeight() {
		return icon().getFitHeight();
	}
	public final DoubleProperty fitHeightProperty() {
		return icon().fitHeightProperty();
	}
	public final void setPreserveRatio(boolean value) {
		icon().setPreserveRatio(value);
	}
	public final boolean isPreserveRatio() {
		return icon().isPreserveRatio();
	}
	public final void setSmooth(boolean value) {
		icon().setSmooth(value);
	}
	public final boolean isSmooth() {
		return icon().isSmooth();
	}
	private ImageView icon() {
		return (ImageView)getGraphic();
	}
}
