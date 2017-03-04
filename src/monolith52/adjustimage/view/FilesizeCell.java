package monolith52.adjustimage.view;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class FilesizeCell<S> extends TableCell<S, Long> {
	
	@Override
	protected void updateItem(Long item, boolean empty) {
		super.updateItem(item, empty);
		
		setText((item != null) ? format(item) : "");
		setAlignment(Pos.BASELINE_RIGHT);
	}
	
	protected String format(Long item) {
		return String.format("%1$,3d KB", item / 1024);
	}
}
