package sam.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Timer;
import javax.swing.border.Border;

//VERSION = 1.2;
public final class SwingPopupShop {

	private static PopupFactory POPUP_FACTORY;
	private static Dimension SCREEN_SIZE;
	private static Component parentComponent;
	
	public static Font popupFont;
	public static Color popupForeground;
	public static Color popupBackground;
	public static int popupLabelPadding = 10;
	public static Border popupborder;

	public static void setPopupsRelativeTo(Component c){
		parentComponent = c;
	}

	private static void init() {
		if(POPUP_FACTORY != null)
			return;
		POPUP_FACTORY = PopupFactory.getSharedInstance();
		SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
		
		 popupFont = popupFont != null ? popupFont :  new Font("Comic Sans MS", 1, 30);
		 popupForeground = popupForeground != null ? popupForeground :  Color.white;
		 popupBackground = popupBackground != null ? popupBackground :  Color.black;
		 popupborder = popupborder != null ? popupborder :  BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.white, 1, true), BorderFactory.createEmptyBorder(popupLabelPadding,popupLabelPadding,popupLabelPadding,popupLabelPadding));
	}

	private static JLabel getPopupLabel(String msg){
		JLabel popupLabel = new JLabel(msg, JLabel.CENTER);
		popupLabel.setFont(popupFont);
		popupLabel.setBackground(popupBackground);
		popupLabel.setForeground(popupForeground);
		popupLabel.setOpaque(true);
		popupLabel.setDoubleBuffered(false);
		popupLabel.setBorder(popupborder);
		return popupLabel;
	}

	public static SwingPopupWrapper showPopup(Component owner, String msg){
		return showPopup(owner, getPopupLabel(msg));
	}

	public static SwingPopupWrapper showPopup(Component owner, String msg, int x, int y){
		return showPopup(owner, getPopupLabel(msg), x , y);
	}

	public static SwingPopupWrapper showPopup(String msg, int x, int y){
		return showPopup(parentComponent, getPopupLabel(msg), x , y);
	}

	public static SwingPopupWrapper showPopup(String msg){
		return showPopup(parentComponent, msg);
	}

	public static SwingPopupWrapper showPopup(JComponent child, int x, int y){
		return showPopup(parentComponent, child, x, y);
	}

	public static class SwingPopupWrapper {
		private final Popup popup;

		private SwingPopupWrapper(Popup popup) {
			this.popup = popup;
		}
		
		public SwingPopupWrapper show() {
			popup.show();
			return this;
		}
		public SwingPopupWrapper hide(int delay) {
			hidePopup(this, delay);
			return this;
		}
	}

	public static SwingPopupWrapper showPopup(Component owner, JComponent child, int x, int y){
		init();
		return new SwingPopupWrapper(POPUP_FACTORY.getPopup(owner, child, x, y)).show();
	}
	public static SwingPopupWrapper showPopup(Component owner, JComponent child) {
		return showPopup(owner, 
				child, 
				((owner == null)?SCREEN_SIZE.width/2:(owner.getLocation().x + owner.getWidth()/2)) - child.getPreferredSize().width/2, 
				((owner == null)?SCREEN_SIZE.height/2:(owner.getLocation().y + owner.getHeight()/2)) - child.getPreferredSize().height/2);

	}
	public static void hidePopup(SwingPopupWrapper p, int delay){

		if(p == null)
			return;
		if(delay == 0)
			p.popup.hide();
		else{
			Timer t = new Timer(delay, e -> EventQueue.invokeLater(() -> p.popup.hide()));
			t.start();
			t.setRepeats(false);
			t = null;
		}
	}

	public static void showHidePopup(Component owner, JComponent child, int delay) { 
		hidePopup(showPopup(owner, child), delay);
	}

	public static void showHidePopup(Component owner, JComponent child, int x, int y, int delay) { 
		hidePopup(showPopup(owner, child, x, y), delay);
	}

	public static void showHidePopup(JComponent child, int x, int y, int delay) { 
		hidePopup(showPopup(child, x, y), delay);
	}

	public static void showHidePopup(String msg, int delay) { 
		hidePopup(showPopup(msg), delay);
	}

	public static void showHidePopup(String msg, int x, int y, int delay) { 
		hidePopup(showPopup(msg, x, y), delay);
	}

	public static void showHidePopup(Component owner, String msg, int x, int y, int delay) { 
		showPopup(owner, msg, x, y).hide(delay);
	}
	public static void showHidePopup(Component owner, String msg, int delay) { 
		showPopup(owner, msg).hide(delay);
	}
}
