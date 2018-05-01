package sam.fx.transformer;

import java.util.Arrays;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

interface AbstractTransform {
	String title();
	String toString();
	void change(Node old, Node _new);

	default HBox hbox(Node...nodes) {
		HBox hb = new HBox(5, nodes);
		hb.setAlignment(Pos.CENTER_LEFT);
		return hb;
	}
	default TextField textfield(Slider s) {
		TextField fld = new TextField("0");
		fld.setPrefColumnCount(4);
		fld.setFont(Font.font(10));
		s.valueProperty().addListener((p, o, n) -> fld.setText(n.intValue() == n.doubleValue() ? String.valueOf(n.intValue()) : String.format("%.3f", n.doubleValue())));

		fld.setOnAction(e -> {
			try {
				s.valueProperty().set(Double.parseDouble(fld.getText().trim()));
			} catch (NumberFormatException e2) {
				System.err.println(e2);
			}
		});

		return fld;
	}
	default void unbind(Slider sld, DoubleProperty dp) {
		sld.valueProperty().unbindBidirectional(dp);
	}
	default void bind(Slider sld, DoubleProperty dp) {
		sld.valueProperty().bindBidirectional(dp);
	}
	default Slider slider(double min, double max, String tooltipMsg) {
		Slider s = new Slider(min, max, 0);
		s.setTooltip(new Tooltip(tooltipMsg+"\n"+ Arrays.toString(numbers2(min, max))));
		s.getTooltip().setStyle("-fx-background-color:black;-fx-font-fill:white;-fx-fill:white;-fx-font-weight:bold;-fx-font-size:14");
		HBox.setHgrow(s, Priority.ALWAYS);
		return s;
	}
	default Text text(String s) {
		return text(s,null);
	}
	default Text text(String s, Color color) {
		Text t = new Text(s);
		if(color != null)
			t.setFill(Color.DARKGREEN);
		t.setTextAlignment(TextAlignment.CENTER);
		return t;
	}

	default Object[] numbers(double...values) {
		return numbers2(values);
	}
	static Object[] numbers2(double...values) {
		Object[] obj = new Object[values.length];
		for (int i = 0; i < obj.length; i++)
			obj[i] = number2(values[i]);
		
		return obj;
	}
	default Object number(double d) {
		return number2(d);
	}
	static Object number2(double d) {
		int d2 = (int)d;
		
		if(d == d2)
			return d2;
		else 
			return d;
	}
}
