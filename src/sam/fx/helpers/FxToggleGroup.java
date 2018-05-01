package sam.fx.helpers;

import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class FxToggleGroup {
    public static ToggleGroup toggleGroup(Toggle selected, Toggle...buttons) {
        ToggleGroup grp = new ToggleGroup();
        grp.getToggles().addAll(buttons);
        if(selected != null)
            grp.selectToggle(selected);
        return grp;
    }
}
