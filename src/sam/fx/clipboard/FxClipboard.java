package sam.fx.clipboard;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import sam.reference.WeakAndLazy;

public interface FxClipboard {
    static final WeakAndLazy<Clipboard> CLIPBOARD = new WeakAndLazy<>(Clipboard::getSystemClipboard);
    
   public static boolean setContent(Map<DataFormat, Object> map) {
       return CLIPBOARD.get().setContent(map);
   }
	public static boolean setContent(DataFormat format, Object data) {
        return setContent(Collections.singletonMap(format, data));
    }
    public static boolean setString(String data) {
    	return setContent(DataFormat.PLAIN_TEXT, data);
    }
    public static String getString() {
        return CLIPBOARD.get().getString();
    }
    public static List<File> getFiles() {
        return CLIPBOARD.get().getFiles();
    }
}
