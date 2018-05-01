package sam.fx.animation.interpolator;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplineToaster extends Application {
	public static final boolean NO_PACK = true;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) throws Exception {
		Circle c = new Circle(25, 25, 20, Color.RED);

		TranslateTransition transition = new TranslateTransition(Duration.seconds(3), c);
		transition.setToY(400);
		
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("y");
		NumberAxis tAxis = new NumberAxis();
		tAxis.setLabel("t");
		
		LineChart<Number, Number> chart = new LineChart<>(tAxis, yAxis);
		chart.setLegendVisible(false);
		chart.setAnimated(true);
		chart.setCreateSymbols(false);
		

		ObservableList<Data<Number, Number>> data = FXCollections.observableArrayList();
		transition.setOnFinished(e -> chart.getData().add(new XYChart.Series<>(data)));
		c.translateYProperty().addListener(e -> data.add(new XYChart.Data(transition.getCurrentTime().toMillis(), c.getTranslateY())));

		BorderPane root = new BorderPane(chart, null, null, null, new Pane(c));
		root.setStyle("-fx-background-color:white");
		
		primaryStage.setScene(new Scene(root, 1000, 500, Color.BLACK));
		primaryStage.show();
		primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> System.exit(0));
		
		final GridPane p = new GridPane();
		p.setHgap(3);
		p.setVgap(3);

		TextField[] tfs = new TextField[4];
		Slider[] sls = new Slider[4];
		Label[] lbs = new Label[4];
		for (int i = 0; i < 4; i++) {
			TextField tf = tfs[i] = new TextField();
			tf.setEditable(true);
			Slider sl = sls[i] = new Slider(0, 1, 0);
			sl.minWidth(200);
			Label l = lbs[i] =  new Label("0");
			
			sl.valueProperty().addListener((p1, o, n) -> l.setText(String.format("%.3f", n)));
			tf.textProperty().addListener((p1, o, n) -> {
				String s = tf.getText().trim(); 
				if(tf == tfs[0] && (s.indexOf(" ") > 0 || s.indexOf(',') > 0 || s.indexOf('t') > 0)) {
					double[] ds = Stream.of(s.split("\\s+|,|\t")).mapToDouble(s1 -> {
						try {
							return Double.parseDouble(s1.trim());
						} catch (Exception e) {}
						return -1d;
					})
					.filter(d -> d != -1)
					.toArray();
					
					for (int j = 0; j < lbs.length; j++)
						tfs[j].setText(ds.length > j ? String.valueOf(ds[j]) : lbs[j].getText());	
					
					return;
				}
				if(s.isEmpty()) {
					sl.setValue(0);
					return;
				}
				try {
					double d = Double.parseDouble(s);
					if(d > 1)
						tf.setText("1");
					else
						sl.setValue(d);
				} catch (NumberFormatException e) {
					tf.setText("1");
				}
			});
			p.addRow(i+2, new Label((i%2 == 0 ? "x" : "y") + (i < 2 ? "1 |  " : "2 |  ") ), l, sl);
			GridPane.setColumnSpan(sl, GridPane.REMAINING);
		}
		
		p.add(new HBox(10, tfs), 0, 0, GridPane.REMAINING, 1);
		
		Button b = new Button("Play");

		ComboBox<String> comboBox = new ComboBox<>(Stream.of(Interpolators.class.getFields()).map(Field::getName).collect(Collectors.toCollection(() -> FXCollections.observableArrayList())));
		
		comboBox.setOnAction(e -> {
			transition.stop();
			c.setTranslateY(0);
			chart.getData().clear();
			data.clear();
			
			try {
				transition.setInterpolator((Interpolator)Interpolators.class.getField(comboBox.getSelectionModel().getSelectedItem()).get(null));
				transition.playFromStart();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		});
		
		p.addRow(6, b, new Label("   Predefined easing: "), comboBox);
		p.setPadding(new Insets(10, 10, 10, 100));
		
		b.setOnAction(e -> {
			transition.stop();
			c.setTranslateY(0);
			chart.getData().clear();
			data.clear();
			
			transition.setInterpolator(Interpolator.SPLINE(
					Double.parseDouble(lbs[0].getText()), 
					Double.parseDouble(lbs[1].getText()), 
					Double.parseDouble(lbs[2].getText()), 
					Double.parseDouble(lbs[3].getText())));	
			transition.playFromStart();
		});
		root.setBottom(p);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
