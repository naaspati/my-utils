package sam.fx.helpers;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
	private ImageView icon;
	
	public void setIcon(Image img) {
	    setIcon(new ImageView(img));
	}
	public void setIcon(ImageView img) {
	    setGraphic(icon = img);
        getStyleClass().setAll("icon-button");
        icon.getStyleClass().add("icon");
        icon.setSmooth(true);
	}
	public void setIcon(String url) {
		setIcon(new ImageView(url));
	}
	public String getIcon() {
		return null;
	}
	public final Image getImage() {
		return icon.getImage();
	}
	public final void setFitWidth(double value) {
		icon.setFitWidth(value);
	}
	public final double getFitWidth() {
		return icon.getFitWidth();
	}
	public final DoubleProperty fitWidthProperty() {
		return icon.fitWidthProperty();
	}
	public final void setFitHeight(double value) {
		icon.setFitHeight(value);
	}
	public final double getFitHeight() {
		return icon.getFitHeight();
	}
	public final DoubleProperty fitHeightProperty() {
		return icon.fitHeightProperty();
	}
	public final void setPreserveRatio(boolean value) {
		icon.setPreserveRatio(value);
	}
	public final boolean isPreserveRatio() {
		return icon.isPreserveRatio();
	}
}
