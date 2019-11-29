package getservicesinfo;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public abstract class TableWithCopy<T> extends TableView<T> {

    public abstract String fetchData(ObservableList<T> posList);

    protected TableWithCopy() {
        MenuItem item = new MenuItem("Copy");
        item.setOnAction(event -> {
            ObservableList<T> posList = getSelectionModel().getSelectedItems();
            String clipboardString = fetchData(posList);
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString);
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        setContextMenu(menu);
    }

}
