package sam.fx.helpers;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public interface FxCell {

    public static <E> Callback<ListView<E>, ListCell<E>> checkboxCell(BiConsumer<E, Boolean> onSelect, Predicate<E> isSelected,
            BiConsumer<ListCell<E>, E> consumer) {
        return c -> new ListCell<E>() {
            E item;
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> onSelect.accept(item, cb.isSelected()));
            }

            @Override
            protected void updateItem(E s, boolean empty) {
                this.item = s;
                super.updateItem(s, empty);
                setGraphic(null);
                if (empty || s == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    cb.setSelected(isSelected.test(s));
                    setGraphic(cb);
                    consumer.accept(this, s);
                }
            }
        };
    }

    public static <E> Callback<ListView<E>, ListCell<E>> checkboxCell(BiConsumer<E, Boolean> onSelect, Predicate<E> isSelected, Function<E, String> mapper) {
        return checkboxCell(onSelect, isSelected, (cell, e) -> cell.setText(mapper.apply(e)));
    }

    public static <E> Callback<ListView<E>, ListCell<E>> listCell(BiConsumer<ListCell<E>, E> consumer) {
        return c -> new ListCell<E>() {
            @Override
            protected void updateItem(E s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setText(null);
                    setTooltip(null);
                } else
                    consumer.accept(this, s);
            }
        };
    }

    public static <E> Callback<ListView<E>, ListCell<E>> listCell(Function<E, String> mapper) {
        return listCell((cell, e) -> cell.setText(mapper.apply(e)));
    }

    public static <E> Callback<TreeView<E>, TreeCell<E>> treeCell(BiConsumer<TreeCell<E>, E> consumer) {
        return c -> new TreeCell<E>() {
            @Override
            protected void updateItem(E s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setText(null);
                    setTooltip(null);
                } else
                    consumer.accept(this, s);
            }
        };
    }

    public static <E> Callback<TreeView<E>, TreeCell<E>> treeCell(Function<E, String> mapper) {
        return treeCell((cell, e) -> cell.setText(mapper.apply(e)));
    }

}
