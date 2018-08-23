package sam.fx.clipboard;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

public interface FxClipboard {
    public static Clipboard clipboard() {
        return Clipboard.getSystemClipboard();
    }
   public static boolean setContent(Map<DataFormat, Object> map) {
       return clipboard().setContent(map);
   }
	public static boolean copyToClipboard(DataFormat format, Object data) {
        Map<DataFormat, Object> map = new HashMap<>();
        map.put(format, data);
        return setContent(map);
    }
    public static boolean copyToClipboard(String data) {
    	ClipboardContent cc = new ClipboardContent();
    	cc.putString(data);
    	return setContent(cc);
    }
    public static String getString() {
        return clipboard().getString();
    }
    public static List<File> getFiles() {
        return clipboard().getFiles();
    }
}
