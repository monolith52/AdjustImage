package monolith52.adjustimage.view;

import javafx.scene.control.TableRow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import monolith52.adjustimage.FileTableRecord;

public class RecordRow extends TableRow<FileTableRecord> {
	
	private static Color bgColor = new Color(245.0d/255, 180.0d/255, 180.0d/255, 1.0d);

	@Override
	protected void updateItem(FileTableRecord item, boolean empty) {
		super.updateItem(item, empty);
		
		if (!empty && item.getError() != null) {
			setBackground(new Background(new BackgroundFill(bgColor, null, null)));
		} else {
			setBackground(Background.EMPTY);
		}
	}

}
