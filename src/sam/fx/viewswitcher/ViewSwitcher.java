package sam.fx.viewswitcher;

import javafx.scene.Parent;

public interface ViewSwitcher {
	void set(Parent parent);
	Parent current();
	void unset();
}
