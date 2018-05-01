package sam.fx.popup;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public final class FxPopupShop {
    public static final double VERSION = 1.2;
    
	private static Window parent;
	private static String style = "-fx-background-color:#1B1B1B;-fx-background-radius:10;-fx-padding:10 20 10 20;-fx-text-fill:white;-fx-border-width:0.3;-fx-border-color:white"; 
	
	public static void setStyle(String background, String textFill) {
		style = String.format("-fx-background-color:%s;-fx-background-radius:10;-fx-padding:10 20 10 20;-fx-text-fill:%s", background == null ? "#1B1B1B" : background, textFill == null ? "white" : textFill);
	}
	
	public static void setParent(Window parent) {
		FxPopupShop.parent = parent;
	}
	public static Window getParent() {
		return parent;
	}
	public static void showHidePopup(String msg, int delayInMills) {
		if(parent == null)
			throw new IllegalStateException("parent is not set");
		
		        Popup popup = new Popup();

        Label text = new Label(msg);
        text.getStyleClass().clear();
        
        text.setStyle(style);

        popup.getContent().add(text);
        popup.setAutoHide(true);
        popup.show(parent);
        popup.getScene().getRoot().setStyle("-fx-background-color:transparent");

        if(parent.isShowing()){
            popup.setX(parent.getX() + parent.getWidth()/2 - popup.getWidth()/2);
            popup.setY(parent.getY() + parent.getHeight()/2 - popup.getHeight()/2);
        }
        
        PauseTransition pause = new PauseTransition(Duration.millis(delayInMills/2));
        FadeTransition transition = new FadeTransition(Duration.millis(delayInMills/2));
        transition.setToValue(0.1);
        
        SequentialTransition st = new SequentialTransition(text, pause, transition);
        st.setOnFinished(e -> {
            popup.hide();
            st.stop();  
        });
        
        popup.setOnAutoHide(e -> st.stop());
        st.play();
	}
}
