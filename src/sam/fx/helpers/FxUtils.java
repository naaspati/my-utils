package sam.fx.helpers;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import sam.myutils.Checker;
import sam.myutils.MyUtilsException;

public interface FxUtils {
	// VERSION = 1.02;
	public static void makeStageDraggable(Stage stage, Node node) {
		double[] start = {0,0};

		node.addEventFilter(MouseEvent.ANY, e -> {
			if(e.getEventType() == MouseEvent.MOUSE_PRESSED) {
				start[0] = e.getScreenX() - stage.getX();
				start[1] = e.getScreenY() - stage.getY();
			} else if(e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				stage.setX(e.getScreenX() - start[0]);
				stage.setY(e.getScreenY() - start[1]);
			}
		});
	}

	@SafeVarargs
	public static <N> void each(Consumer<N> consumer, N...ns) {
		for (N n : ns) consumer.accept(n);
	}

	/**
	 * find object of class E in Object(a Node) parent
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E find(Object node, Class<E> cls) {
		if(node == null || cls.isInstance(node))
			return (E) node;

		if(!(node instanceof Node))
			throw new IllegalArgumentException("node is not an Node instance. found class: "+node.getClass());

		Node n = ((Node)node).getParent();

		while(n != null && !cls.isInstance(n)) 
			n = n.getParent();	

		return (E) n;

	}
	public static InvalidationListener invalidationListener(InvalidationListener invalidationListener) {
		return invalidationListener;
	}
	public static <E> E edit(E e, Consumer<E> edit) {
		edit.accept(e);
		return e;
	}
	public static FileChooser fileChooser(File expectedDir, String expectedName, String title, Consumer<FileChooser> editor) {
		FileChooser fc = new FileChooser();

		if (Checker.exists(expectedDir))
			fc.setInitialDirectory(expectedDir);
		if(expectedName != null)
			fc.setInitialFileName(expectedName);

		fc.setTitle(title);
		if(editor != null)
			editor.accept(fc);
		return fc;
	}
	public static void setText(String data, Node...nodes) {
		for (Node o : nodes) {
			if( o == null) {}
			else if(o instanceof Labeled)
				((Labeled)o).setText(null);
			else if(o instanceof TextInputControl)
				((TextInputControl)o).setText(null);
			else if(o instanceof Text)
				((Text)o).setText(null);
			else
				throw new RuntimeException("unsupported class: "+o.getClass());
		}
	}
	
	public static TextArea createErrorTa(String title, String msg, Throwable e) {
StringBuilder sb = new StringBuilder();
		
		if(title != null) {
			sb.append(title).append('\n');
			for (int i = 0; i < title.length(); i++) 
				sb.append('-');
			
			sb.append('\n');
		}
		
		if(msg != null) {
			sb.append(msg).append('\n');
			
			int n = Math.min(msg.length(), 30);
			
			for (int i = 0; i < n; i++) 
				sb.append('-');
			
			sb.append('\n');
		}
		
		if(e != null) {
			sb.append("STACKTRACE\n-------------\n");
			MyUtilsException.append(sb, e, true);
			sb.append('\n');
		}
		
		TextArea ta = new TextArea(sb.toString());
		ta.setFont(Font.font("monospace"));
		
		return ta;
	}

	public static void setErrorTa(Stage stage, String title, String msg, Throwable e) {
		Objects.requireNonNull(stage);
		stage.setTitle(title);
		
		TextArea ta = createErrorTa(title, msg, e);
		
		if(stage.getScene() == null)
			stage.setScene(new Scene(ta));
		else 
			stage.getScene().setRoot(ta);
	}
	public static ImageView imageView(Image img, int fitWidth, int fitHeight) {
		ImageView m = new ImageView(img);
		m.setPreserveRatio(true);
		if(fitWidth > 0)
			m.setFitWidth(fitWidth);
		if(fitHeight > 0)
			m.setFitWidth(fitHeight);
		return m;
	}
	public static void center(Window child, Window parent) {
		Platform.runLater(() -> {
			child.setX(parent.getWidth()/2 - child.getWidth()/2);
			child.setY(parent.getHeight()/2 - child.getHeight()/2);
		});
	}
}
