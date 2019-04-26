package sam.fx.dialog;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sam.reference.WeakPool;

public class StackPaneDialogViewer implements DialogViewer {
    private final Effect effect = new DropShadow();
    private final List<Node> list;
    
    public StackPaneDialogViewer(StackPane root) {
        this.list = root.getChildren();
    }

    private Temp primary;
    private boolean primary_in_use;
    private final WeakPool<Temp> dialogs = new WeakPool<>(Temp::new);
    
    private static final Runnable NO_CLOSE = () -> {};
    private static final String NO_CLOSE_S = new String();
    
    @Override
    public Runnable viewDialog(Node node) {
    	return viewDialog(NO_CLOSE_S, node, NO_CLOSE);
    }

    @Override
    public Runnable viewDialog(String title, Node node, Runnable onClose) {
       Node prev = list.isEmpty() ? null : list.get(list.size() - 1);
        Temp temp;

        if(primary_in_use) {
            temp = dialogs.poll();
        } else {
            if(primary == null)
                primary = new Temp();
            temp = primary;
            primary_in_use = true;
        }

        prev.setDisable(true);
        list.add(temp);

        Runnable r = new Runnable() {
            boolean closed = false;

            @Override
            public void run() {
                if(closed)
                    return;

                closed = true;
                temp.close = null;
                temp.pane.setCenter(null);
                list.remove(temp);
                prev.setDisable(false);

                if(temp == primary)
                    primary_in_use = false;
                else
                    dialogs.add(temp);

                if(onClose != null)
                    onClose.run();
            }
        };

        if(title == NO_CLOSE_S)
        	temp.set(NO_CLOSE_S, node, r);
        else 
        	temp.set(title, node, r);
        
        return r;
    }
    
    private class Temp extends Group implements EventHandler<ActionEvent> { 
        private Runnable close;
        private final BorderPane pane = new BorderPane();
        private Button button = new Button("X");
        private final Label title = new Label();
        private final BorderPane top;
        
        public Temp() {
            BorderPane.setAlignment(title, Pos.CENTER_LEFT);
            title.setMaxWidth(Double.MAX_VALUE);
            title.setPadding(new Insets(0, 0, 0, 10));
            this.top = new BorderPane(title, null, button, null, null);
            top.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 2, 0))));
            top.setPadding(new Insets(2));
            BorderPane.setMargin(top, new Insets(0,0,5,0));
                    
            pane.setTop(top);
            getChildren().add(pane);
            pane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), Insets.EMPTY)));
            
            button.getStyleClass().clear();
            button.setPadding(new Insets(3, 7,3, 7));
            button.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(50, true), Insets.EMPTY)));
            button.setTextFill(Color.WHITE);
            button.setFont(Font.font("Consolas", FontWeight.BOLD, -1));
            
            button.setTranslateX(10);
            button.setTranslateY(-10);
            
            button.setOnAction(this);
            
            BorderPane.setAlignment(button, Pos.TOP_RIGHT);
            StackPane.setAlignment(this, Pos.CENTER);
            pane.setEffect(effect);
        }

        public void set(String title, Node node, Runnable r) {
        	if(title == NO_CLOSE_S)
        		pane.setTop(null);
        	else if(pane.getTop() != this.top)
        		pane.setTop(this.top);
        	
        	this.close = r;
        	this.title.setText(title);
        	this.pane.setCenter(node);
		}

		@Override
        public void handle(ActionEvent event) {
            close.run();
        }
    }
}
