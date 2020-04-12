package sam.fx.alert;

import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import sam.functions.RunnableWithException;
import sam.fx.helpers.FxConstants;
import sam.fx.helpers.FxCss;
import sam.string.StringWriter2;

//VERSION = 1.22;
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

		if(error == null) {
			AlertBuilder alert = alertBuilder(AlertType.ERROR)
					.content(text)
					.buttons(OK)
					.header(header);

			if(blockingModality)
				alert.showAndWait();
			else
				alert.show();	
			return;
		}

		Stage stage = new Stage(StageStyle.UTILITY);
		stage.initModality(blockingModality ? Modality.APPLICATION_MODAL : Modality.WINDOW_MODAL);
		stage.initOwner(parent);
		stage.setTitle("ERROR");

		Node errorNode = null;
		
		if(error != null) {
			if(error instanceof Node) 
				errorNode = (Node)error;
			else if(error instanceof Throwable) {
				TextArea ta = new TextArea();
				StringWriter2 sw =  new StringWriter2();
				PrintWriter pw = new PrintWriter(sw);
				((Throwable) error).printStackTrace(pw);
				ta.setText(sw.toString());
				errorNode = ta;
			} else {
				TextArea ta = new TextArea(error.toString());
				errorNode = ta;
			}
		}
		
		Node headerNode = null;
		
		if(header != null) {
			if(header instanceof Node)
				headerNode = (Node)header;
			else {
				Label label = new Label(header.toString());
				label.setWrapText(true);
				headerNode = label;
				label.setFont(Font.font(15));
				label.setPadding(FxConstants.INSETS_10);
				label.setMaxWidth(Double.MAX_VALUE);
				label.setBackground(FxCss.background(Color.WHITESMOKE));
			}
		}
		
		Node textNode = null;
		if(text != null) {
			if(text instanceof Node)
				textNode = (Node)text;
			else {
				String s = text.toString();
				if(s.length() < 40) {
					Label label = new Label(s);
					label.setWrapText(true);
					label.setPadding(new Insets(10));
					textNode = label;
				} else {
					TextArea ta = new TextArea();
					ta.setText(s);
					ta.setPrefRowCount(5);
					textNode = ta;
				}
			}
		}
		
		int c = 0;
		Node[] node = new Node[3];
		if(headerNode != null)
			node[c++] = headerNode;
		if(textNode != null)
			node[c++] = textNode;
		if(errorNode != null)
			node[c++] = errorNode;
		
		Parent root;
		if(c == 1) {
			if(node[0] instanceof Parent)
				root = (Parent)node[0];
			else
				root = new BorderPane(node[0]);
		} else {
			if(c == 2)
				root = new VBox(5, node[0], node[1]);
			else
				root = new VBox(5, node);
			VBox.setVgrow(node[c - 1], Priority.ALWAYS);
		}
		
		root.setStyle("-fx-font-family:Consolas");
		stage.setScene(new Scene(root));
		
		
		if(blockingModality)
			stage.showAndWait();
		else
			stage.show();	
	}

	public static  void showErrorDialog(Object text, Object header, Object error) {
		showErrorDialog(text, header, error, true);
	}

	public static  void showMessageDialog(AlertType alertType,  Object text, Object header, boolean blockingModality) {
		validateParent();

		AlertBuilder alert = alertBuilder(alertType)
				.content(text)
				.buttons(OK)
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
				.buttons(YES, NO)
				.header(header);

		Optional<ButtonType> r = alert.showAndWait();
		return r.isPresent() && r.get() == YES;
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
	
	public static boolean showOnError(RunnableWithException action, Object title, Object msg) {
		try {
			action.run();
			return true;
		} catch (Exception e) {
			showErrorDialog(msg, title, e);
		}
		return false;
	}
}
