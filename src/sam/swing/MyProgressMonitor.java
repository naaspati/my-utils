package sam.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class MyProgressMonitor {
    // VERSION = 1.2;
    
	private JFrame dialog;
	private JProgressBar progressBar;
	private int max;
	private int min;
	private int currentProgress = 0;
	private boolean showRemainingAsTitle;

	public MyProgressMonitor(String title, int min, int max) {
		this.max = max;
		this.min = min;
		
		progressBar = new JProgressBar(min, max);
		progressBar.setStringPainted(true);
		progressBar.setBackground(Color.white);
		progressBar.setForeground(Color.black);
		progressBar.setFont(new Font(null, 0, 20));
		progressBar.setStringPainted(true);
		progressBar.setBorderPainted(false);
		progressBar.setMaximumSize(new Dimension(600, 50));
		progressBar.setPreferredSize(new Dimension(600, 50));
		
		
		dialog = new JFrame(title);
		
		dialog.add(progressBar, BorderLayout.NORTH);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		dialog.setSize(600, 100);
		dialog.setResizable(false);
	}
	
	public void setContentPane(Component component){
		dialog.add(component, BorderLayout.CENTER);
		dialog.setSize(600, (int) (component.getPreferredSize().getHeight() + 150));
	}
	
	public void setShowRemainingAsTitle(boolean showRemainingAsTitle) {
		this.showRemainingAsTitle = showRemainingAsTitle;
	}
	
	public void setMinimum(int min) {
		this.min = min;
		progressBar.setMinimum(min);
	}
	
	/**
	 * this will close the program if set true, and dialog is closed manually by user, (disposing dialog does not close the program)
	 * @param b
	 */
	public void setDefaultCloseOperation(int operation){
			dialog.setDefaultCloseOperation(operation);
	}
	public void setOnClosing(Consumer<WindowEvent> c) {
	    dialog.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            c.accept(e);
	        }
        });
	}
	public void setMaximum(int max) {
		this.max = max;
		progressBar.setMaximum(max);
	}
	
	public void setLocationRelativeTo(Component c){
		dialog.setLocationRelativeTo(c);
	}
	public void  setLocation(int x, int y){
		dialog.setLocation(x, y);
	};
	
	public void setString(String text){
		progressBar.setString(text);
	}
	
	public void setProgress(int value){
		value = value > max ? max : value;
		value = value < min ? min : value;
		
		progressBar.setValue(currentProgress = value);
	}
	
	public int getCurrentProgress() {
		return currentProgress;
	}
	
	/**
	 * ++currentProgress
	 */
	public void increaseBy1(){
		setProgress(++currentProgress);
		
		if(showRemainingAsTitle)
			setTitle(currentProgress+" / "+max);
	}
	
	public void setVisible(boolean b){
		dialog.setVisible(b);
	}
	
	public void setFailed(){
		progressBar.setFont(new Font(null, 0, 25));
		dialog.setTitle(":: Failed ::");
		progressBar.setString(":: Failed ::");
		progressBar.setValue(progressBar.getMaximum());
		progressBar.setForeground(Color.red);
		dialog.toFront();
	}
	
	public void setBackground(Color color) {
		progressBar.setBackground(color);
	}
	
	public void setForeground(Color color) {
		progressBar.setForeground(color);
	}
	
	public void setForeground(Font font) {
		progressBar.setFont(font);
	}
	
	/**
	 * to reset {@link #frame} (<b>dialog.setTitle(title);</b>)  and {@link #progressBar} (<b>progressBar.setValue(0); progressBar.setString(title);</b>)
	 * @param title
	 */
	public void setReset(String title){
		title = title == null ? "" : title;

		dialog.setTitle(title);
		progressBar.setValue(0);
		progressBar.setString(title);

	}
	
	public void setCompleted(){
		progressBar.setFont(new Font(null, 0, 25));
		dialog.setTitle(":: Completed ::");
		progressBar.setString(":: Completed ::");
		progressBar.setValue(progressBar.getMaximum());
		dialog.toFront();
	}

	public void setTitle(String title){
		dialog.setTitle(title);
	}
	
	/**
	 * resets current progress to minimum
	 */
	public void resetProgress() {
		currentProgress = min;
	}

	public void dispose(){
	    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		progressBar = null;
		dialog.dispose();
	}

	
	public void addWindowListener(WindowListener action){
	    dialog.addWindowListener(action);
	}
	public void removeWindowListener(WindowListener action){
	    dialog.removeWindowListener(action);
	}
}
