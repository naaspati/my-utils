package sam.fx.helpers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public interface FxButton {
    public static Button button(String tooltip, String iconName, EventHandler<ActionEvent> action) {
        Button b = new Button(null, iconName == null ? null : new ImageView(iconName));

        b.getStyleClass().clear();
        b.setTooltip(new Tooltip(tooltip));

        if(action != null)
            b.setOnAction(action);

        return b;
    }
    
}
