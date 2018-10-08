package sam.swing;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public interface SwingClipboard {
	Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard(); 
	
	ClipboardOwner DEFAULT_CLIPBOARD_OWNER = new ClipboardOwner() {
		@Override public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	};   
	/**
	 * copy to clipboard
	 * @param string
	 */
	public static  void setString(String string){
		EventQueue.invokeLater(() -> CLIPBOARD.setContents(new StringSelection(string), DEFAULT_CLIPBOARD_OWNER));
	}
	public static String getString() throws UnsupportedFlavorException, IOException {
		return (String) CLIPBOARD.getData(DataFlavor.stringFlavor);
	}
}
