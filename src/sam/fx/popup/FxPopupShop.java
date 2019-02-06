package sam.fx.popup;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public final class FxPopupShop {
	// VERSION = 1.2;

	private static Window parent;
	private static String style = "-fx-background-color:#1B1B1B;-fx-background-radius:10;-fx-padding:10 20 10 20;-fx-text-fill:white;-fx-border-width:0.3;-fx-border-color:white"; 

	public static void setStyle(String background, String textFill) {
		style = String.format("-fx-background-color:%s;-fx-background-radius:10;-fx-padding:10 20 10 20;-fx-text-fill:%s", background == null ? "#1B1B1B" : background, textFill == null ? "white" : textFill);
	}

	public static class PopupWrap {
		public final Popup popup;

		public PopupWrap(Popup p) {
			this.popup = p;
		}
		public void hide(int delayInMills) {
			FxPopupShop.hide(this, delayInMills);
		}
	}

	public static void setParent(Window parent) {
		FxPopupShop.parent = parent;
	}
	public static Window getParent() {
		return parent;
	}

	public static PopupWrap show(String msg) {
		Label text = new Label(msg);
		text.getStyleClass().clear();

		text.setStyle(style);
		return show(text);
	}

	public static PopupWrap show(Node node) {
		if(parent == null)
			throw new IllegalStateException("parent is not set");

		Popup popup = new Popup();

		popup.getContent().add(node);
		popup.setAutoHide(true);
		popup.show(parent);
		popup.getScene().getRoot().setStyle("-fx-background-color:transparent");
		

		if(parent.isShowing()){
			popup.setX(parent.getX() + parent.getWidth()/2 - popup.getWidth()/2);
			popup.setY(parent.getY() + parent.getHeight()/2 - popup.getHeight()/2);
		}
		return new PopupWrap(popup);
	} 

	public static void hide(PopupWrap popup, int delayInMills) {
		if(popup == null || !popup.popup.isShowing())
			return;

		if(delayInMills < 100) {
			popup.popup.hide();
			return;
		}

		PauseTransition pause = new PauseTransition(Duration.millis(delayInMills/2));
		FadeTransition transition = new FadeTransition(Duration.millis(delayInMills/2));
		transition.setToValue(0.1);

		SequentialTransition st = new SequentialTransition(popup.popup.getContent().get(0), pause, transition);
		st.setOnFinished(e -> {
			popup.popup.hide();
			st.stop();  
		});

		popup.popup.setOnAutoHide(e -> st.stop());
		st.play();
	}
	public static void showHidePopup(String msg, int delayInMills) {
		PopupWrap p = show(msg);
		hide(p, delayInMills);
	}
	public static PopupWrap showLoadingPopup() {
		ProgressIndicator p = new ProgressIndicator();
		p.setStyle("-fx-background-color:white");
		return show(p);
	}
}
