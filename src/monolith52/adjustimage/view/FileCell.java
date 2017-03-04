package monolith52.adjustimage.view;

import java.io.File;

import javafx.scene.control.TableCell;

public class FileCell<S> extends TableCell<S, File> {
	
	@Override
	protected void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);
		
		setText((item != null) ? item.getName() : "");
	}

}
