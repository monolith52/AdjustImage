package monolith52.adjustimage.view;

import java.io.File;

import javafx.scene.control.TableCell;
import monolith52.adjustimage.FileTableRecord;

public class FileCell extends TableCell<FileTableRecord, File> {
	
	@Override
	protected void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setText("");
			return;
		}
		FileTableRecord record = getTableView().getItems().get(getIndex());
		if (record.getError() != null) {
			setText(String.format("%s %s", record.getFile().getName(), record.getError()));
		} else {
			setText(record.getFile().getName());
		}
	}

}
