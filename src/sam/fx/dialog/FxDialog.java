package sam.fx.dialog;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.util.Callback;

public class FxDialog {
    public static final double VERSION = 1.2;
    
    public static <E> Dialog<E> showDialog(Node content, Node header, Callback<ButtonType, E> resultConverter, ButtonType...buttons) {
        Objects.requireNonNull(resultConverter, "result converter cannot be null");
        if(buttons.length == 0)
            throw new IllegalArgumentException("no buttons given");
        
        Dialog<E> dialog = new Dialog<>();
        DialogPane dp = dialog.getDialogPane();
        dp.getButtonTypes().addAll(buttons);
        if(header != null) {
            dp.setHeader(header);
            header.getStyleClass().add("content");
        }
        if(content != null) {
            dp.setContent(content);
            content.getStyleClass().add("header");
        }

        dialog.setResultConverter(resultConverter);
        return dialog;
    }
}

