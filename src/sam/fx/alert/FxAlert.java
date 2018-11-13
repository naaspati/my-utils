package sam.fx.alert;

import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

//VERSION = 1.2;
public final class FxAlert {
    
    
	private static Window parent;

	public static void setParent(Window parent) {
		FxAlert.parent = parent;
	}
	public static Window getParent() {
		return parent;
	}
	private static void validateParent() {
		if(parent == null)
			throw new IllegalStateException("parent is not set");
	}
	
	public static AlertBuilder alertBuilder(AlertType alertType) {
		validateParent();
		return new AlertBuilder(alertType, parent);
	} 

	/**
	 * 
	 * @param text
	 * @param header
	 * @param error if error is an Exception the stacktrace is appended otherwise error.toObject() is
	 * @param blockingModality
	 */
	public static  void showErrorDialog(Object text, Object header, Object error, boolean blockingModality) {
		validateParent();

		AlertBuilder alert = alertBuilder(AlertType.ERROR)
				.content(text)
				.buttons(ButtonType.OK)
				.header(header);

		if(error == null) {}
		else if(error instanceof Throwable) 
			alert.exception((Throwable)error);
		else
			alert.expandableText(error);

		if(blockingModality)
			alert.showAndWait();
		else
			alert.show();
	}

	public static  void showErrorDialog(Object text, Object header, Object error) {
		showErrorDialog(text, header, error, true);
	}

	public static  void showMessageDialog(AlertType alertType,  Object text, Object header, boolean blockingModality) {
		validateParent();
		
		AlertBuilder alert = alertBuilder(alertType)
				.content(text)
				.buttons(ButtonType.OK)
				.header(header);

		if(blockingModality)
			alert.showAndWait();
		else
			alert.show();		
	}

	public static  void showMessageDialog(Object text, Object header, boolean blockingModality) {
		showMessageDialog(AlertType.INFORMATION, text, header, blockingModality);
	}

	public static  void showMessageDialog(Object text, Object header){
		showMessageDialog(text, header, true);
	}

	public static  boolean showConfirmDialog(Object text, Object header) {
		validateParent();

		AlertBuilder alert = alertBuilder(AlertType.CONFIRMATION)
				.content(text)
				.buttons(ButtonType.YES, ButtonType.NO)
				.header(header);

		Optional<ButtonType> r = alert.showAndWait();
		return r.isPresent() && r.get() == ButtonType.YES;
	}

	public static  Alert showConfirmDialog(String title, Object header, Object content) {
		validateParent();

		return alertBuilder(AlertType.CONFIRMATION)
				.content(content)
				.header(header)
				.title(title)
				.build();
	}
	
	private static <E> E methodCallerWithWindow(Window window, Supplier<E> objective) {
		Window temp = parent;
		parent=window;
		E value = objective.get();
		parent = temp;
		return value;
	}
	private static void methodCallerWithWindow(Window window, Runnable objective) {
		Window temp = parent;
		parent=window;
		objective.run();
		parent = temp;
	}

	public static  void showErrorDialog(Window window,Object text, Object header, Object error, boolean blockingModality) {
		methodCallerWithWindow(window, () -> showErrorDialog(text, header, error, blockingModality));
	}

	public static  void showErrorDialog(Window window,Object text, Object header, Object error) {
		methodCallerWithWindow(window, () -> showErrorDialog(text,header,error)); 
	}

	public static  void showMessageDialog(Window window,AlertType alertType,  Object text, Object header, boolean blockingModality) {
		methodCallerWithWindow(window, () -> showMessageDialog(alertType,text,header,blockingModality)); 
	}
	public static  void showMessageDialog(Window window,Object text, Object header, boolean blockingModality) {
		methodCallerWithWindow(window, () -> showMessageDialog(text,header,blockingModality)); 
	}

	public static  void showMessageDialog(Window window,Object text, Object header){
		methodCallerWithWindow(window, () -> showMessageDialog(text,header));
	}

	public static  boolean showConfirmDialog(Window window,Object text, Object header) {
		return methodCallerWithWindow(window, () -> showConfirmDialog(text,header)); 
	}

	public static  Alert showConfirmDialog(Window window,String title, Object headerText, Object contentText) {
		return methodCallerWithWindow(window, () -> showConfirmDialog(title,headerText,contentText)); 
	}
}
