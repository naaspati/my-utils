package sam.fx.alert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import sam.myutils.Checker;

//VERSION = 1.2;
public final class AlertBuilder {

    private final Alert alert;

    public AlertBuilder(AlertType alertType, Window window) {
        alert = new Alert(alertType);
        alert.initOwner(window);
    }
    public AlertBuilder alertType(AlertType alertType){
        alert.setAlertType(alertType);
        return this;
    }
    public AlertBuilder content(String text){
        alert.setContentText(text);
        return this;
    }
    public AlertBuilder content(Object obj){
        if(obj == null || obj instanceof Node) return content((Node)obj);
        else return content(String.valueOf(obj));
    }
    public AlertBuilder content(Node content){
        alert.getDialogPane().setContent(content);
        return this;
    }

    public AlertBuilder dialogPane(DialogPane value){
        alert.setDialogPane(value);
        return this;
    }

    public AlertBuilder header(String headerText){
        alert.setHeaderText(headerText);
        return this;
    }
    public AlertBuilder header(Object obj){
        if(obj == null || obj instanceof Node) return header((Node)obj);
        else return header(String.valueOf(obj));
    }
    public AlertBuilder header(Node header){
        alert.getDialogPane().setHeader(header);
        return this;
    }
    public AlertBuilder graphic(Node graphic){
        alert.setGraphic(graphic);
        return this;
    }
    public AlertBuilder resizable(boolean resizable){
        alert.setResizable(resizable);
        return this;
    }
    public AlertBuilder size(double width, double height){
        alert.setWidth(width);
        alert.setHeight(height);
        return this;
    }
    public AlertBuilder width(double width){
        alert.setWidth(width);
        return this;
    }
    public AlertBuilder height(double height){
        alert.setHeight(height);
        return this;
    }
    public AlertBuilder title(String title){
        alert.setTitle(title);
        return this;
    }

    public AlertBuilder x(double x){
        alert.setX(x);
        return this;
    }

    public AlertBuilder y(double y){
        alert.setY(y);
        return this;
    }

    public AlertBuilder onShowing(EventHandler<DialogEvent> value){
        alert.setOnShowing(value);
        return this;
    }

    public AlertBuilder onShown(EventHandler<DialogEvent> value){
        alert.setOnShown(value);
        return this;
    }
    public AlertBuilder resultConverter(Callback<ButtonType, ButtonType> value){
        alert.setResultConverter(value);
        return this;
    }
    public AlertBuilder onCloseRequest(EventHandler<DialogEvent> value){
        alert.setOnCloseRequest(value);
        return this;
    }
    public AlertBuilder onHiding(EventHandler<DialogEvent> value){
        alert.setOnHiding(value);
        return this;
    }
    public AlertBuilder onHidden(EventHandler<DialogEvent> value){
        alert.setOnHidden(value);
        return this;
    }
    public AlertBuilder buttons(ButtonType...buttonTypes) {
        if(buttonTypes == null) {
            alert.getButtonTypes().clear();
            return this;
        };
        alert.getButtonTypes().setAll(buttonTypes);
        return this;
    }
    public AlertBuilder exception(Throwable e) {
        if(e == null) {
            alert.getDialogPane().setExpandableContent(null);
            return this;
        };

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        sw.append("--------------------------------------------------\r\n") 
        .append("------------------ Stack Trace -------------------\r\n") 
        .append("--------------------------------------------------\n\n");
        
        if(Checker.isEmpty(alert.getContentText()) && Checker.isNotEmpty(e.getMessage())) 
        	content(e.getClass().getSimpleName()+": "+e.getMessage());

        e.printStackTrace(pw);

        expandableText(sw);
        expanded(true);

        return this;
    }

    public AlertBuilder expandableText(Object text) {
        alert.getDialogPane().setExpandableContent(text  == null ? null : new TextArea(String.valueOf(text)));
        return this;
    }
    public AlertBuilder expandableContent(Node content){
        alert.getDialogPane().setExpandableContent(content == null ? null : content);
        return this;
    }
    public AlertBuilder expanded(boolean value) {
        alert.getDialogPane().setExpanded(value);
        return this;
    }
    public Optional<ButtonType> showAndWait() {
        return alert.showAndWait();
    }
    public void show() {
        alert.show();
    }
    public final AlertBuilder modality(Modality modality) {
        alert.initModality(modality);
        return this;
    }
    public final AlertBuilder style(StageStyle style) {
        alert.initStyle(style);
        return this;
    }
    public final AlertBuilder owner(Window window) {
        alert.initOwner(window);
        return this;
    }
    public final AlertBuilder result(ButtonType value) {
        alert.setResult(value);
        return this;
    }
    /**
     * build the create Alert
     * @return
     */
    public Alert build() {
        return alert;
    }
}
