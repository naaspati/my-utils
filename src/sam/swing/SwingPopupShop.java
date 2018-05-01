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

public final class SwingPopupShop {
    public static final double VERSION = 1.2;
    
	private static PopupFactory POPUP_FACTORY;
	private static Dimension SCREEN_SIZE;
	private static Popup[] popups; 
	private static int currentIndex = 0;
	private static Component parentComponent;

	public static void setPopupsRelativeTo(Component c){
		parentComponent = c;
	}
	
	private static void init() {
	    POPUP_FACTORY = PopupFactory.getSharedInstance();
	    SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	    popups = new Popup[10];
	}

	private static JLabel getPopupLabel(String msg){
		JLabel label = new JLabel(msg, JLabel.CENTER);
		label.setFont(new Font("Comic Sans MS", 1, 30));
		int popupLabelPadding = 10;
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		label.setOpaque(true);
		label.setDoubleBuffered(false);
		label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.white, 1, true), BorderFactory.createEmptyBorder(popupLabelPadding,popupLabelPadding,popupLabelPadding,popupLabelPadding)));
		return  label;
	}

	public static int showPopup(Component owner, String msg){
		return showPopup(owner, getPopupLabel(msg));
	}

	public static int showPopup(Component owner, String msg, int x, int y){
		return showPopup(owner, getPopupLabel(msg), x , y);
	}

	public static int showPopup(String msg, int x, int y){
		return showPopup(parentComponent, getPopupLabel(msg), x , y);
	}

	public static int showPopup(String msg){
		return showPopup(parentComponent, msg);
	}

	public static int showPopup(JComponent child, int x, int y){
		return showPopup(parentComponent, child, x, y);
	}

	public static int showPopup(Component owner, JComponent child, int x, int y){
	    init();

		currentIndex++;
		if(currentIndex >= popups.length)
			currentIndex = 0;

		if(popups[currentIndex] != null)
			hidePopup(currentIndex, 0);

		(popups[currentIndex] = POPUP_FACTORY.getPopup(owner, child, x, y)).show();

		return currentIndex;
	}

	public static int showPopup(Component owner, JComponent child) {
		return showPopup(owner, 
				child, 
				((owner == null)?SCREEN_SIZE.width/2:(owner.getLocation().x + owner.getWidth()/2)) - child.getPreferredSize().width/2, 
				((owner == null)?SCREEN_SIZE.height/2:(owner.getLocation().y + owner.getHeight()/2)) - child.getPreferredSize().height/2);

	}

	public static void hidePopup(int popupId, int delay){
		if(popups[popupId] == null)
			return;
		if(delay == 0)
			popups[popupId].hide();
		else{
			final Popup p = popups[popupId];

			Timer t = new Timer(delay, e -> EventQueue.invokeLater(() -> p.hide()));
			t.start();
			t.setRepeats(false);
			t = null;
		}

		popups[popupId] = null;
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
		hidePopup(showPopup(owner, msg, x, y), delay);
	}

	public static void showHidePopup(Component owner, String msg, int delay) { 
		hidePopup(showPopup(owner, msg), delay);
	}


}
