package sam.fx.helpers;

import java.nio.file.Path;

import javafx.stage.Stage;

public interface StageAttr {
	default Path saveDir() {
		return null;
	}
	Stage stage();
	default void setStageAttrs(int defaultWidth, int defaultHeight) {
		new StageAttrImpl(saveDir(), this).set(defaultWidth, defaultHeight);
	}
	default void updateStageAttrs() {
		new StageAttrImpl(saveDir(), this).update();
	}
	
	
}
