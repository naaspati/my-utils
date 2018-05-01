package sam.fx.clipboard;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

public interface FxClipboard {
	public static boolean copyToClipboard(DataFormat format, Object data) {
        Map<DataFormat, Object> map = new HashMap<>();
        map.put(format, data);
        return Clipboard.getSystemClipboard().setContent(map);
    }
    public static boolean copyToClipboard(String data) {
    	ClipboardContent cc = new ClipboardContent();
    	cc.putString(data);
        return Clipboard.getSystemClipboard().setContent(cc);
    }
}
